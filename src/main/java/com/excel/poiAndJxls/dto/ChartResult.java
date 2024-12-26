package com.excel.poiAndJxls.dto;

import lombok.Data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ChartResult {
    public String cache_key;
    public String cached_dttm;
    public Long cache_timeout;
    public String query;
    public Long rowcount;
    public String status;
    public List<String> colnames;
    public List<Long> indexnames;
    public Map<String , List<String>> label_map;
    public List<Map<String ,String>> data;
    public List<Integer> coltypes;
}
