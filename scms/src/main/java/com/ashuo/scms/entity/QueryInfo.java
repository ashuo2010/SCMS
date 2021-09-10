package com.ashuo.scms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryInfo {
    private String query;
    private Integer currentPage;
    private Integer pageSize;
}
