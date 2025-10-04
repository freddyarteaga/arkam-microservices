package com.arkam.order.infrastructure.adapter.out;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ProductServiceClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder productWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient productServiceWebClient(WebClient.Builder productWebClientBuilder) {
        return productWebClientBuilder
                .baseUrl("http://product-service")
                .build();
    }
}
