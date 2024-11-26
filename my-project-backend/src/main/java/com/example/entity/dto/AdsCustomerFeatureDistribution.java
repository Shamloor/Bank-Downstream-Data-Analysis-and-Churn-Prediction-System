package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ads_customer_feature_distribution")
public class AdsCustomerFeatureDistribution {
    private String gender;
    private String ageGroup;
    private Integer customerCount;
    private Integer activeCustomerCount;
    private Integer inactiveCustomerCount;
}