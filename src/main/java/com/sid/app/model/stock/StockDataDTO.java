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
public class StockDataDTO {

    private int priority;
    private String symbol;
    private String identifier;
    private String series;
    private double open;
    private double dayHigh;
    private double dayLow;
    private double lastPrice;
    private double previousClose;
    private double change;
    private double pChange;
    private double totalTradedVolume;
    private int stockIndClosePrice;
    private double totalTradedValue;
    private String lastUpdateTime;
    private double yearHigh;
    private double ffmc;
    private double yearLow;
    private double nearWKH;
    private double nearWKL;
    private double perChange365d;
    private String date365dAgo;
    private String chart365dPath;
    private String date30dAgo;
    private double perChange30d;
    private String chart30dPath;
    private String chartTodayPath;
    //private MetaDTO meta;

}