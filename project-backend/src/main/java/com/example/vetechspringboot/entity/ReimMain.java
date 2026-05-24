package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
    private BigDecimal subsidyTotal;
    private BigDecimal mealAllowance;
    private BigDecimal transportationAllowance;
    private BigDecimal phoneAllowance;
    private String remarks;
    private Integer status;
}

