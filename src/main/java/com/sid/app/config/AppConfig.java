package com.sid.app.config;

import com.sid.app.constants.AppConstants;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
    public WebClient webClientNSE() {
        return WebClient.builder()
                .baseUrl(properties.getNifty50URL())
                .defaultHeader(HttpHeaders.USER_AGENT, AppConstants.WEBCLIENT_USER_AGENT)
                .defaultHeader(HttpHeaders.REFERER, AppConstants.WEBCLIENT_REFERER)
                .defaultHeader(HttpHeaders.ACCEPT, AppConstants.WEBCLIENT_ACCEPT)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, AppConstants.WEBCLIENT_ACCEPT_LANGUAGE)
                .defaultHeader(AppConstants.HEADER_SEC_FETCH_SITE, AppConstants.WEBCLIENT_SEC_FETCH_SITE)
                .defaultHeader(AppConstants.HEADER_CACHE_CONTROL, AppConstants.WEBCLIENT_CACHE_CONTROL)
                .defaultHeader(AppConstants.HEADER_PRAGMA, AppConstants.WEBCLIENT_PRAGMA)
                .defaultHeader(AppConstants.HEADER_CONNECTION, AppConstants.WEBCLIENT_CONNECTION)
                .defaultHeader(AppConstants.HEADER_HOST, AppConstants.WEBCLIENT_HOST)
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .keepAlive(false)
                        .responseTimeout(Duration.ofSeconds(properties.getTimeout()))))
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(properties.getBufferSize()))
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