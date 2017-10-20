package me.iswear.springcanalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Data;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;

@Data
public class CanalConsumer {

    private Object target;

    private Method method;

    private BeanFactory beanFactory;

    private ConfigurableEnvironment environment;

    public CanalConsumer(Object target, Method method, BeanFactory beanFactory, ConfigurableEnvironment environment) {
        this.target = target;
        this.method = method;
        this.beanFactory = beanFactory;
        this.environment = environment;
    }

    public void consumeCanalMessage(CanalConnector connector, Message message) throws InvocationTargetException, IllegalAccessException {
        Parameter[] parameters = method.getParameters();
        if (parameters != null && parameters.length > 0) {
            List<Object> args = new LinkedList<>();
            for (Parameter parameter : parameters) {
                if (parameter.getType() == CanalConnector.class) {
                    args.add(connector);
                } else if (parameter.getType() == Message.class) {
                    args.add(message);
                } else {
                    args.add(null);
                }
            }
            method.invoke(target, args.toArray());
        } else {
            method.invoke(target);
        }
    }

}
