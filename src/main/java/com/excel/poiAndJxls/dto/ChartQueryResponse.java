package com.excel.poiAndJxls.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChartQueryResponse {
    public List<ChartResult> result;
}
