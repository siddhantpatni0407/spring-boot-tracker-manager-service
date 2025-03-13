package com.sid.app.model.stock;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Siddhant Patni
 */
public class QuotePreOpenStatusDTO {

    private String equityTime;

    private String preOpenTime;

    @JsonProperty("QuotePreOpenFlag")
    private boolean quotePreOpenFlag;

}