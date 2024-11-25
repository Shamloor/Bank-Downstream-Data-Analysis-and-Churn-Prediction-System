package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_hascrcard")
public class TrainHasCrCard {
    @TableId
    private String hascrcard;
    private Float count;
}