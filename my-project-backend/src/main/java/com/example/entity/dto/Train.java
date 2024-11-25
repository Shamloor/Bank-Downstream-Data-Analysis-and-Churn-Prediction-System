package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train")
public class Train {
    @TableId
    private String id;
    private String customerId;
    private String surname;
    private Float creditscore;
    private String geography;
    private String gender;
    private Float age;
    private Float tenure;
    private Float balance;
    private Float numofproducts;
    private Float hascrcard;
    private Float isactivemember;
    private Float estimatedsalary;
    private Float exited;
}