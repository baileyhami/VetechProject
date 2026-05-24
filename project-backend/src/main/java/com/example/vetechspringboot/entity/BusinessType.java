package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_business_type")
public class BusinessType {
    @TableId("business_type_id")
    private String businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;
    private String thereSubordinateNode;
    private String superiorId;
}

