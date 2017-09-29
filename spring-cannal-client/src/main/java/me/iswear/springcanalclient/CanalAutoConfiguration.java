package me.iswear.springcanalclient;


import com.alibaba.otter.canal.client.CanalConnector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(CanalConnector.class)
public class CanalAutoConfiguration {

    @Bean
    public CanalConsumerScanProcessor injectCanalConsumerScanProcessor() {
        return new CanalConsumerScanProcessor();
    }

}
