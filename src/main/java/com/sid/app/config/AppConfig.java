package com.sid.app.config;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * The type App config.
 *
 * @author Siddhant Patni
 */
@Configuration
public class AppConfig {

    /**
     * The Properties.
     */
    @Autowired
    AppProperties properties;

   /* @Bean(AppConstants.BEAN_GET_GET_ENTITLEMENT)
    public WebClient webClientGetEntitlement(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth.setDefaultClientRegistrationId("entitlement");
        return getWebClient(oauth);
    }*/

    /**
     * Web client chat gpt web client.
     *
     * @return the web client
     */
    @Bean
    public WebClient webClient() {
        return WebClient
                .builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(properties.getBufferSize()))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create().keepAlive(false).responseTimeout(Duration.ofSeconds(properties.getTimeout()))))
                .build();
    }

    /**
     * This is WebClient Bean for Email.
     *
     * @return WebClient Email Bean
     */
    /*@Bean(AppConstants.BEAN_EMAIL)
    public WebClient webClientEmail() {
        HttpClient httpClient = HttpClient
                .create()
                .responseTimeout(Duration.ofSeconds(properties.getTimeout()))
                .keepAlive(false);

        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(properties.getBufferSize()))
                .build();
    }*/

    private HttpClient getHttpClient() {
        return HttpClient
                .create()
                .keepAlive(false)
                .responseTimeout(Duration.ofSeconds(properties.getTimeout()))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(properties.getTimeout(), TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(properties.getTimeout(), TimeUnit.SECONDS)));
    }

}