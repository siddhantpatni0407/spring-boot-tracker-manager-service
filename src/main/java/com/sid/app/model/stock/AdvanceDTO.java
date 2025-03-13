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
public class AdvanceDTO {

    private int declines;
    private int advances;
    private int unchanged;

}