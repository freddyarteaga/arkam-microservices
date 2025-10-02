package com.arkam.order.infrastructure.clients;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Configuration
public class ProductServiceClientConfig {
    @Bean
    public ProductServiceClient  productServiceInterface(RestClient.Builder restClientBuilder) {
//        se crea la instancia del cliente rest
        RestClient restClient = restClientBuilder
                .baseUrl("http://product-service")
//                gestor de estado de error por defecto, que devuelve un opcional y luego lo construye
                .defaultStatusHandler(HttpStatusCode::is4xxClientError,
                        ((request, response) -> Optional.empty()))
                .build();
//        adaptador de cliente qu eobtiene el servicio http
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter).build();

        return factory.createClient(ProductServiceClient.class);
    }
}
