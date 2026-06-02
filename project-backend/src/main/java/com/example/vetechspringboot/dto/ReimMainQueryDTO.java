package com.example.vetechspringboot.dto;

import lombok.Data;

@Data
public class ReimMainQueryDTO {
    private Integer current;
    private Integer size;
    private String reimbursementNo;
    private String reimbursementTitle;
    private String businessTripReason;
    private String reimCompanyId;
    private String reimDepartmentId;
    private String reimburserId;
    private String businessTypeId;
    private Integer status;
}

