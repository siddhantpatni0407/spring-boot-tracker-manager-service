package com.sid.app.service;

import com.sid.app.config.AppProperties;
import com.sid.app.model.stock.StockResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * Service for fetching stock market data from NSE API.
 * Handles API requests and processes responses.
 * <p>
 * Author: Siddhant Patni
 */
@Service
@Slf4j
public class StockMarketService {

    private final WebClient webClient;
    private final AppProperties properties;

    /**
     * Constructor for initializing WebClient and application properties.
     *
     * @param webClientBuilder WebClient builder instance
     * @param properties       Application properties configuration
     */
    public StockMarketService(WebClient.Builder webClientBuilder, AppProperties properties) {
        this.webClient = webClientBuilder
                .baseUrl(properties.getNifty50URL())
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .defaultHeader("Referer", "https://www.nseindia.com")
                .defaultHeader("Accept", "application/json, text/plain, */*")
                .defaultHeader("Accept-Language", "en-US,en;q=0.9")
                .defaultHeader("Sec-Fetch-Site", "same-origin")
                .build();

        this.properties = properties;
    }

    /**
     * Fetches stock market data for a given index from NSE API.
     *
     * @param index The stock index (e.g., "NIFTY 50").
     * @return Mono containing stock market response.
     */
    public Mono<StockResponseDTO> getStockData(String index) {
        String requestUrl = properties.getNifty50URL() + "?index=" + index;
        log.info("Calling NSE API: {}", requestUrl); // Log Full URL

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("index", index)
                        .build())
                .retrieve()
                .bodyToMono(StockResponseDTO.class)
                .map(response -> {
                    if (response.getData() != null && !response.getData().isEmpty()) {
                        log.info("Stock data retrieved successfully for index: {}", index);
                        log.debug("Stock Data Response: {}", response);
                    } else {
                        log.warn("No stock data found for index: {}", index);
                        response.setData(Collections.emptyList());
                    }
                    return response;
                })
                .doOnError(error -> {
                    if (error instanceof WebClientResponseException webClientException) {
                        log.error("Error calling NSE API [{}]: {} \nResponse Body: {}",
                                requestUrl,
                                webClientException.getMessage(),
                                webClientException.getResponseBodyAsString());
                    } else {
                        log.error("Unexpected error calling NSE API at [{}]", requestUrl, error);
                    }
                })
                .onErrorReturn(new StockResponseDTO("ERROR", null, null, Collections.emptyList(), null, null, null, null));
    }
}