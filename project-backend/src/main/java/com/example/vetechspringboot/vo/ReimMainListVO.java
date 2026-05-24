package com.example.vetechspringboot.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReimMainListVO {
    private String id;
    private Integer status;
    private String reimbursementTitle;
    private String businessTripReason;
    private BigDecimal subsidyTotal;
    private LocalDateTime creationTime;

    private String reimburserId;
    private String reimburserName;
    private String reimburserNo;

    private String reimDepartmentId;
    private String reimDepartmentName;
    private String reimDepartmentNo;

    private String reimCompanyId;
    private String reimCompanyName;

    private String businessTypeId;
    private String businessTypeName;
}

