package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_isactivemember")
public class TrainIsActiveMember {
    @TableId
    private String isactivemember;
    private Float count;
}
