package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ads_customer_value_segmentation")
public class AdsCustomerValueSegmentation {
    private String valueSegment;
    private Integer customerCount;
    private Double avgBalance;
    private Double avgCreditScore;
    private Double activeCustomerRate;
    private Double churnRate;
}