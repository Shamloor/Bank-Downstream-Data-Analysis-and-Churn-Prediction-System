package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_geography")
public class TrainGeography {
    @TableId
    private String geography;
    private Float count;
}