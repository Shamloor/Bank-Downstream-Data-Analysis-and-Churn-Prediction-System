package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_tenure")
public class TrainTenure {
    @TableId
    private String tenure;
    private Float count;
}