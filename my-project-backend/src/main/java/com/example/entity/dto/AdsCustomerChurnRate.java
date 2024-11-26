package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ads_customer_churn_rate")
public class AdsCustomerChurnRate {
    private String geography;
    private String ageGroup;
    private String gender;
    private Integer exitedCount;
    private Integer totalCustomerCount;
    private Double churnRate;
}