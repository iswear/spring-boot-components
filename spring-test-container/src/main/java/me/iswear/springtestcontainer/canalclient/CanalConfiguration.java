package me.iswear.springtestcontainer.canalclient;

import com.alibaba.otter.canal.client.CanalConnector;
import me.iswear.springcanalclient.CanalConnectorFactory;
import me.iswear.springcanalclient.annotation.EnableCanalListener;
import me.iswear.springcanalclient.config.CanalSingleConnectorConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCanalListener
public class CanalConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "CanalConnectorConfig")
    public CanalSingleConnectorConfig injectCanalConnectorConfig() {
        return new CanalSingleConnectorConfig();
    }

    @Bean
    public CanalConnector injectCanalConnector(CanalSingleConnectorConfig config) {
        return CanalConnectorFactory.createCanalConnector(config);
    }

}
