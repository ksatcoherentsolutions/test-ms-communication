package com.issoft.rnd.ms2.config;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheClientConfig {

    @Bean
    CloseableHttpAsyncClient getApacheClient() {
        CloseableHttpAsyncClient apacheClient = HttpAsyncClients.custom()
                .setMaxConnPerRoute(2000)
                .setMaxConnTotal(2000).build();
        apacheClient.start();
        return apacheClient;
    }
}
