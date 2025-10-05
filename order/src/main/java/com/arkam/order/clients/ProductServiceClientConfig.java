package com.arkam.order.clients;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ProductServiceClientConfig {
    @Bean
    public ProductServiceClient productServiceInterface(WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder
                            .baseUrl("http://product-service")
                            .build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                                            .builderFor(adapter)
                                            .build();
        return factory.createClient(ProductServiceClient.class);
    }
}
