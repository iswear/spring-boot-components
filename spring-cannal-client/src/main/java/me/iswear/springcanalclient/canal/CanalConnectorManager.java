package me.iswear.springcanalclient.canal;


import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.iswear.springcanalclient.CanalConsumer;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class CanalConnectorManager {

    @Getter
    @Setter
    private CanalConnector connector;

    private AtomicBoolean running = new AtomicBoolean(false);

    private ReentrantLock lock = new ReentrantLock();

    private List<CanalConsumer> consumers = Collections.synchronizedList(new LinkedList<>());

    public boolean isRunning() {
        return this.running.get();
    }

    public void addConsumer(CanalConsumer consumer) {
        this.consumers.add(consumer);
    }

    public int consumeMessageByConsumers(int batchSize) throws InvocationTargetException, IllegalAccessException {
        if (!this.connector.checkValid()) {
            if (this.lock.tryLock()) {
                try {
                    this.running.set(true);
                    this.connector.connect();
                    this.connector.subscribe();
                    this.connector.rollback();
                    Message message = this.connector.get(batchSize);
                    for (CanalConsumer consumer : this.consumers) {
                        consumer.consumeCanalMessage(this.connector, message);
                    }
                    this.connector.ack(message.getId());
                    return message.getEntries().size();
                } catch (Exception ex) {
                    log.error("消费binlog异常", ex);
                } finally {
                    this.lock.unlock();
                    this.running.set(false);
                }
            }
        } else {
            try {
                this.running.set(true);
                this.connector.subscribe();
                this.connector.rollback();
                Message message = this.connector.getWithoutAck(batchSize);
                for (CanalConsumer consumer : this.consumers) {
                    consumer.consumeCanalMessage(this.connector, message);
                }
                this.connector.ack(message.getId());
                return message.getEntries().size();
            } catch (Exception ex) {
                log.error("消费binlog异常", ex);
            } finally {
                this.running.set(false);
            }
        }
        return 0;
    }
}
