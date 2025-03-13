package com.sid.app.model.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

/**
 * @author Siddhant Patni
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetaDTO {

    private String symbol;
    private String companyName;
    private String industry;
    private ArrayList<String> activeSeries;
    private ArrayList<Object> debtSeries;
    private boolean isFNOSec;
    private boolean isCASec;
    private boolean isSLBSec;
    private boolean isDebtSec;
    private boolean isSuspended;
    private ArrayList<Object> tempSuspendedSeries;
    private boolean isETFSec;
    private boolean isDelisted;
    private String isin;
    private String slb_isin;
    private String listingDate;
    private boolean isMunicipalBond;
    private boolean isHybridSymbol;
    private QuotePreOpenStatusDTO quotepreopenstatus;

}