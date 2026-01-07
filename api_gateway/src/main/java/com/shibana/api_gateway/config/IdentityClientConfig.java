package com.shibana.api_gateway.config;

import com.shibana.api_gateway.httpClient.IdentityClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class IdentityClientConfig {

    @Bean
    public WebClient identityWebClient(
            WebClient.Builder webClientBuilder,
            @Value("${service.identity.base-url}") String identityServiceUrl
            ) {
        return webClientBuilder
                .baseUrl(identityServiceUrl)
                .build();
    }

    @Bean
    IdentityClient identityClient(WebClient identityWebClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(identityWebClient)).build();

        return factory.createClient(IdentityClient.class);
    }
}
