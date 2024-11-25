package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_estimatedsalary")
public class TrainEstimatedSalary {
    private float estimatedSalary;
}