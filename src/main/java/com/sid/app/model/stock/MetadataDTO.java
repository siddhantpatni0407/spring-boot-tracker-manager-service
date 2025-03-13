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
public class MetadataDTO {

    private String indexName;
    private double open;
    private double high;
    private double low;
    private double last;
    private double change;
    private double percChange;

}