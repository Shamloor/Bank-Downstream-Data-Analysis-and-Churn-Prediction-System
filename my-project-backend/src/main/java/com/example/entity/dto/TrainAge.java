package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_age")
public class TrainAge {
    private int age;
}