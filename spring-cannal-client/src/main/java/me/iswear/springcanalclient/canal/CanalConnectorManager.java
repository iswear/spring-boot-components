package me.iswear.springcanalclient.canal;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Getter;
import lombok.Setter;
import me.iswear.springcanalclient.CanalConsumer;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class CanalConnectorManager {

    @Getter
    @Setter
    private CanalConnector connector;

    @Setter
    @Getter
    private AtomicBoolean running = new AtomicBoolean(false);

    private ReentrantLock lock = new ReentrantLock();

    private List<CanalConsumer> consumers = Collections.synchronizedList(new LinkedList<>());

    public void addConsumer(CanalConsumer consumer) {
        this.consumers.add(consumer);
    }

    public void consumeMessageByConsumers() throws InvocationTargetException, IllegalAccessException {
        if (!this.connector.checkValid()) {
            if (this.lock.tryLock()) {
                try {
                    this.connector.connect();
                    this.connector.subscribe();
                    this.connector.rollback();
                    for (CanalConsumer consumer : this.consumers) {
                        Message message = this.connector.get(100);
                        consumer.consumerCancelMessage(this.connector, message);
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        } else {
            this.connector.subscribe();
            this.connector.rollback();
            for (CanalConsumer consumer : this.consumers) {
                Message message = this.connector.getWithoutAck(100);
                consumer.consumerCancelMessage(this.connector, message);
            }
        }
    }

}
