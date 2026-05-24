package com.example.vetechspringboot.dto;

import lombok.Data;

@Data
public class ReimMainQueryDTO {
    private String reimbursementNo;
    private String reimbursementTitle;
    private String businessTripReason;
    private String reimCompanyId;
    private String reimDepartmentId;
    private String reimburserId;
    private String businessTypeId;
    private Integer status;
}

