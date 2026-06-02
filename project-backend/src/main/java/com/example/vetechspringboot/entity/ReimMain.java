package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fk_reim_main")
public class ReimMain {
    @TableId("id")
    private String id;
    private LocalDateTime creationTime;
    private String reimbursementTitle;
    private String reimburserId;
    private String reimDepartmentId;
    private String reimCompanyId;
    private String businessTypeId;
    private String businessTripReason;
    @TableField("subsidy_total")
    @JsonProperty("subsidyTotal")
    private BigDecimal totalAmount;
    private BigDecimal mealAllowance;
    private BigDecimal transportationAllowance;
    private BigDecimal phoneAllowance;
    @TableField("remarks")
    @JsonProperty("remarks")
    private String remark;
    private Integer status;
}

