package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.exception.StockException;
import com.sid.app.model.ResponseDTO;
import com.sid.app.model.stock.StockResponseDTO;
import com.sid.app.service.StockMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

/**
 * Controller for managing stock market data retrieval from NSE.
 * <p>
 * Author: Siddhant Patni
 */
@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class StockMarketController {

    private final StockMarketService stockService;

    /**
     * Retrieves stock market data from NSE for the given index.
     *
     * @param index The stock index (e.g., "NIFTY 50").
     * @return ResponseEntity containing the response status, message, and stock data.
     */
    @GetMapping(AppConstants.STOCK_NIFTY_STOCK_DATA_ENDPOINT)
    public Mono<ResponseEntity<ResponseDTO<StockResponseDTO>>> getStockData(@RequestParam String index) {
        log.info(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.FLOW_START);
        log.info(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.LOG_REQUEST_FETCH_STOCK_DATA, index);

        return stockService.invokeStockData(index)
                .map(stockData -> {
                    log.info(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.LOG_STOCK_DATA_RETRIEVED, index);
                    ResponseEntity<ResponseDTO<StockResponseDTO>> response = ResponseEntity.ok(ResponseDTO.<StockResponseDTO>builder()
                            .status(AppConstants.STATUS_SUCCESS)
                            .message(AppConstants.MSG_STOCK_DATA_RETRIEVED + index)
                            .data(stockData)
                            .build());
                    return response;
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse(AppConstants.ERROR_STOCK_DATA_NOT_FOUND, HttpStatus.NOT_FOUND))))
                .onErrorResume(StockException.class, ex -> {
                    log.warn(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.LOG_STOCK_EXCEPTION, ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST)));
                })
                .onErrorResume(WebClientResponseException.Unauthorized.class, ex -> {
                    log.warn(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.LOG_UNAUTHORIZED_ACCESS, index);
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(createErrorResponse(AppConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED)));
                })
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.LOG_EXTERNAL_API_ERROR, ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                            .body(createErrorResponse(AppConstants.ERROR_EXTERNAL_API_FAILURE, HttpStatus.BAD_GATEWAY)));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.warn(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.ERROR_UNEXPECTED);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(createErrorResponse(AppConstants.ERROR_UNEXPECTED, HttpStatus.INTERNAL_SERVER_ERROR)));
                })
                .doFinally(signalType -> log.info(AppConstants.METHOD_GET_STOCK_DATA + AppConstants.FLOW_END));
    }

    /**
     * Creates an error response DTO.
     *
     * @param message Error message.
     * @param status  HTTP status code.
     * @return ResponseDTO with error details.
     */
    private ResponseDTO<StockResponseDTO> createErrorResponse(String message, HttpStatus status) {
        return ResponseDTO.<StockResponseDTO>builder()
                .status(AppConstants.STATUS_FAILED)
                .message(message)
                .data(null)
                .build();
    }

}