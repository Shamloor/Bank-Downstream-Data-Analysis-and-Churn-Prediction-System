package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_numofproducts")
public class TrainNumOfProducts {
    @TableId
    private String numofproducts;
    private Float count;
}