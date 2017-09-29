package me.iswear.springcanalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springcanalclient.annotation.CanalListener;
import me.iswear.springcanalclient.canal.CanalConnectorManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class CanalConsumerScanProcessor implements BeanPostProcessor, BeanFactoryAware, ApplicationListener<ContextRefreshedEvent> {

    private BeanFactory beanFactory;

    private List<CanalConsumer> canalConsumers = Collections.synchronizedList(new LinkedList<>());

    private List<CanalConnectorManager> canalConnectorManagers = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        Method[] methods = o.getClass().getMethods();
        if (methods != null) {
            for (Method method : methods) {
                CanalListener listener = method.getAnnotation(CanalListener.class);
                if (listener != null) {
                    this.canalConsumers.add(new CanalConsumer(o, method));
                }
            }
        }
        return o;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
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
            this.startConsumer();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    public void addCanalConnectorManager(CanalConnector connector, CanalConsumer consumer) {
        if (connector == null) {
            throw new NullPointerException();
        }
        if (connector == null) {
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
        while (true) {
            for (CanalConnectorManager manager : this.canalConnectorManagers) {
                manager.consumeMessageByConsumers();
            }
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                log.error("CANEL消费程序处于非正常状态", ex);
            }
        }
    }

}
