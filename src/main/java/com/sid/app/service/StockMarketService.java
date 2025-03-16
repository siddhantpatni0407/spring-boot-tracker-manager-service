package com.sid.app.service;

import com.sid.app.config.AppProperties;
import com.sid.app.constants.AppConstants;
import com.sid.app.exception.StockException;
import com.sid.app.model.stock.StockResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Service for fetching stock market data from NSE API.
 * Handles API requests and processes responses.
 * <p>
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class StockMarketService {

    private final WebClient webClientNSE;
    private final AppProperties properties;

    public StockMarketService(WebClient webClientNSE, AppProperties properties) {
        this.properties = properties;
        this.webClientNSE = webClientNSE;
    }

    /**
     * Fetches stock market data for a given index from NSE API.
     *
     * @param index The stock index (e.g., "NIFTY 50").
     * @return Mono containing stock market response.
     */
    public Mono<StockResponseDTO> invokeStockData(String index) {
        String requestUrl = properties.getNifty50URL() + "?index=" + index;
        log.info(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_FETCHING_STOCK_DATA, requestUrl);

        return webClientNSE
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("index", index).build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.UNAUTHORIZED) {
                        log.warn(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_UNAUTHORIZED_ACCESS, requestUrl);
                        return Mono.error(new StockException(AppConstants.UNAUTHORIZED_ACCESS));
                    }
                    return clientResponse.createException().flatMap(Mono::error);
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.warn(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_SERVER_ERROR, requestUrl, clientResponse.statusCode());
                    return Mono.error(new RuntimeException(AppConstants.NSE_API_UNAVAILABLE));
                })
                .bodyToMono(StockResponseDTO.class)
                .flatMap(response -> {
                    if (response.getData() != null && !response.getData().isEmpty()) {
                        log.info(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_STOCK_DATA_RETRIEVED, index);
                        return Mono.just(response);
                    } else {
                        log.warn(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_NO_STOCK_DATA, index);
                        return Mono.error(new StockException(AppConstants.NO_STOCK_DATA_FOUND));
                    }
                })
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2))
                        .filter(ex -> ex instanceof WebClientResponseException.TooManyRequests)
                        .doBeforeRetry(retrySignal -> log.warn(AppConstants.LOG_RETRYING_API_CALL, retrySignal.totalRetries() + 1))
                )
                .onErrorResume(Exception.class, ex -> {
                    log.warn(AppConstants.METHOD_INVOKE_STOCK_DATA + AppConstants.LOG_UNEXPECTED_ERROR, requestUrl);
                    return Mono.error(new RuntimeException(AppConstants.UNEXPECTED_ERROR));
                });
    }

}