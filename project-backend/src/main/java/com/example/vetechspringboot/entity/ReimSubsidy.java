package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("fk_reim_subsidy")
public class ReimSubsidy {
    @TableId("id")
    private String id;
    private String mainId;
    private String tripId;
    private String travelerId;
    private String travelDateRange;
    private Integer subsidyDays;
    private String tripRoute;
    private String subsidyCityNo;
    private BigDecimal applyAmount;
    private BigDecimal subsidyAmount;
    private BigDecimal mealAllowance;
    private BigDecimal transportationAllowance;
    private BigDecimal phoneAllowance;

    @TableField(exist = false)
    private String detailDate;

    @TableField(exist = false)
    private String cityName;

    @TableField(exist = false)
    private String cityType;

    @TableField(exist = false)
    private BigDecimal dailySubsidyAmount;
}

