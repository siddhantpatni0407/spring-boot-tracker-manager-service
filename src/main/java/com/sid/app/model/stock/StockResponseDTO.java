package com.sid.app.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Siddhant Patni
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDTO {

    private String name;
    private AdvanceDTO advance;
    private String timestamp;
    private List<StockDataDTO> data;
    private MetadataDTO metadata;
    private MarketStatusDTO marketStatus;
    private String date30dAgo;
    private String date365dAgo;

}