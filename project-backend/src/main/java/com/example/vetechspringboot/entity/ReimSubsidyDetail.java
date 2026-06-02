package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Daily subsidy calendar detail (fk_reim_subsidy_detail).
 */
@Data
@TableName("fk_reim_subsidy_detail")
public class ReimSubsidyDetail {
    @TableId("id")
    private String id;
    private String subsidyId;
    private LocalDate detailDate;
    private String weekDay;
    private String cityNo;
    private BigDecimal mealStd;
    private BigDecimal mealAmount;
    private BigDecimal transportStd;
    private BigDecimal transportAmount;
    private BigDecimal phoneStd;
    private BigDecimal phoneAmount;
    @JsonProperty("selected")
    private Integer isSelected;

    @TableField(exist = false)
    private String cityName;
}


