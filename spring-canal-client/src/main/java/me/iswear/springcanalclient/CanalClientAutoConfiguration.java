package me.iswear.springcanalclient;

import me.iswear.springcanalclient.config.CanalClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(CanalConsumerScanProcessor.class)
public class CanalClientAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "CanalClientConfig")
    public CanalClientConfig injectCanalClientConfig(CanalConsumerScanProcessor processor) {
        processor.setClientConfig(new CanalClientConfig());
        return processor.getClientConfig();
    }

}
