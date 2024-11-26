package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ads_customer_balance_distribution")
public class AdsCustomerBalanceDistribution {
    private String balanceRange;
    private Integer customerCount;
}
