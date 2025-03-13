package com.sid.app.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Siddhant Patni
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketStatusDTO {

    private String market;
    private String marketStatus;
    private String tradeDate;
    private String index;
    private double last;
    private double variation;
    private double percentChange;
    private String marketStatusMessage;

}