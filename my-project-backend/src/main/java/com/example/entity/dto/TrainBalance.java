package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_balance")
public class TrainBalance {
    private float balance;
}