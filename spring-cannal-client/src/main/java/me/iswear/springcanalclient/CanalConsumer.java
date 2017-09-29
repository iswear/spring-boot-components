package me.iswear.springcanalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

@Data
public class CanalConsumer {

    private Object target;

    private Method method;

    public CanalConsumer(Object target, Method method) {
        this.target = target;
        this.method = method;
    }

    public void consumerCancelMessage(CanalConnector connector, Message message) throws InvocationTargetException, IllegalAccessException {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes != null && parameterTypes.length > 0) {
            List<Object> parameters = new LinkedList<>();
            for (Class<?> parameterType : parameterTypes) {
                if (parameterType.isAssignableFrom(CanalConnector.class)) {
                    parameters.add(connector);
                } else if (parameterType.isAssignableFrom(Message.class)) {
                    parameters.add(message);
                } else {
                    parameters.add(null);
                }
            }
            method.invoke(target, parameters.toArray());
        } else {
            method.invoke(target);
        }
    }

}
