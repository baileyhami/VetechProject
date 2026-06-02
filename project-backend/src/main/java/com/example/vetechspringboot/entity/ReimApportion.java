package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("fk_reim_apportion")
public class ReimApportion {
    @TableId("id")
    private String id;
    private String mainId;
    private String reimCompanyId;
    private String projectId;
    private BigDecimal apportionRatio;
    private BigDecimal apportionAmount;
    private Integer sortNo;
}

