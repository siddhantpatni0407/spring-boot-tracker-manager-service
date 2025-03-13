package com.sid.app.controller;

import com.sid.app.constants.AppConstants;
import com.sid.app.model.ResponseDTO;
import com.sid.app.model.stock.StockResponseDTO;
import com.sid.app.service.StockMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Controller for managing stock market data retrieval from NSE.
 * <p>
 * Author: Siddhant Patni
 */
@RestController
@Slf4j
@CrossOrigin
public class StockMarketController {

    @Autowired
    private StockMarketService stockService;

    /**
     * Retrieves stock market data from NSE for the given index.
     *
     * @param index The stock index (e.g., "NIFTY 50").
     * @return ResponseEntity containing the response status, message, and stock data.
     */
    @GetMapping(AppConstants.STOCK_NIFTY_50_DATA_ENDPOINT)
    public Mono<ResponseEntity<ResponseDTO<StockResponseDTO>>> getStockData(@RequestParam String index) {
        log.info("Received request to fetch stock data for index: {}", index);

        return stockService.getStockData(index)
                .map(stockData -> {
                    log.info("Successfully retrieved stock data for index: {}", index);
                    log.debug("Stock Data Response: {}", stockData); // Detailed log

                    ResponseDTO<StockResponseDTO> response = ResponseDTO.<StockResponseDTO>builder()
                            .status("SUCCESS")
                            .message("Stock data retrieved successfully.")
                            .data(stockData)
                            .build();

                    return ResponseEntity.ok(response);
                })
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ResponseDTO.<StockResponseDTO>builder()
                                .status("FAILURE")
                                .message("No stock data found for the provided index.")
                                .data(null)
                                .build()));
    }

}