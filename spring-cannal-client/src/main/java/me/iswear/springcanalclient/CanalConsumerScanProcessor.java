package me.iswear.springcanalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springcanalclient.annotation.CanalListener;
import me.iswear.springcanalclient.canal.CanalConnectorManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class CanalConsumerScanProcessor implements BeanPostProcessor, BeanFactoryAware,
        ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {

    private BeanFactory beanFactory;

    private ConfigurableEnvironment environment;

    private List<CanalConsumer> canalConsumers = Collections.synchronizedList(new LinkedList<>());

    private List<CanalConnectorManager> canalConnectorManagers = Collections.synchronizedList(new LinkedList<>());

    private ExecutorService executorPool;

    private AtomicInteger threadCount = new AtomicInteger(0);

    private Object lock = new Object();

    @Setter
    @Getter
    private CanalClientConfig clientConfig;

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Class superClass = o.getClass();
        while (superClass != null) {
            Method[] methods = superClass.getDeclaredMethods();
            if (methods != null) {
                for (Method method : methods) {
                    CanalListener listener = method.getAnnotation(CanalListener.class);
                    if (listener != null) {
                        this.canalConsumers.add(new CanalConsumer(o, method, this.beanFactory, this.environment));
                    }
                }
            }
            superClass = superClass.getSuperclass();
        }
        return o;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        if (environment instanceof ConfigurableEnvironment) {
            this.environment = (ConfigurableEnvironment) environment;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!this.canalConsumers.isEmpty()) {
            for (CanalConsumer consumer : this.canalConsumers) {
                Method method = consumer.getMethod();
                CanalListener listener = method.getAnnotation(CanalListener.class);
                String[] connectorNames = listener.connectorNames();
                if (connectorNames != null && connectorNames.length > 0) {
                    for (String connectorName : connectorNames) {
                        CanalConnector connector = (CanalConnector) this.beanFactory.getBean(connectorName);
                        this.addCanalConnectorManager(connector, consumer);
                    }
                } else {
                    CanalConnector connector = this.beanFactory.getBean(CanalConnector.class);
                    this.addCanalConnectorManager(connector, consumer);
                }
            }
        }
        try {
            this.clientConfig = this.beanFactory.getBean(CanalClientConfig.class);
            this.executorPool = new ThreadPoolExecutor(
                    this.clientConfig.getPoolCoreSize(),
                    this.clientConfig.getPoolMaxSize(),
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>()
            );
            this.startConsumer();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void addCanalConnectorManager(CanalConnector connector, CanalConsumer consumer) {
        if (connector == null || consumer == null) {
            throw new NullPointerException();
        }
        for (CanalConnectorManager manager : canalConnectorManagers) {
            if (manager.getConnector() == connector) {
                manager.addConsumer(consumer);
                return;
            }
        }
        CanalConnectorManager manager = new CanalConnectorManager();
        manager.setConnector(connector);
        manager.addConsumer(consumer);
        this.canalConnectorManagers.add(manager);
    }

    public void startConsumer() throws InvocationTargetException, IllegalAccessException {
        this.executorPool.execute(() -> {
            while (true) {
                while (this.threadCount.get() >= this.canalConnectorManagers.size()) {
                    synchronized (this.lock) {
                        try {
                            this.lock.wait();
                        } catch (InterruptedException e) {
                            log.error("Wait线程异常", e);
                        }
                    }
                }
                for (CanalConnectorManager manager : canalConnectorManagers) {
                    if (!manager.isRunning()) {
                        this.threadCount.incrementAndGet();
                        this.executorPool.execute(() -> {
                            try {
                                manager.consumeMessageByConsumers(clientConfig.getBatchSize());
                            } catch (InvocationTargetException e) {
                                log.error("反射调用方法异常", e);
                            } catch (IllegalAccessException e) {
                                log.error("反射调用方法异常", e);
                            } finally {
                                if (this.threadCount.decrementAndGet() < this.canalConnectorManagers.size()) {
                                    lock.notifyAll();
                                }
                            }
                        });
                    }
                }
            }
        });
    }

}
