package com.example.projectbackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReimbursementListItemDto {
    private String id;
    private int status;
    private String reimburserName;
    private String reimburserNo;
    private String departmentName;
    private String departmentNo;
    private String companyName;
    private String businessTypeName;
    private String title;
    private String reason;
    private BigDecimal subsidyTotal;
    private LocalDateTime creationTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReimburserName() {
        return reimburserName;
    }

    public void setReimburserName(String reimburserName) {
        this.reimburserName = reimburserName;
    }

    public String getReimburserNo() {
        return reimburserNo;
    }

    public void setReimburserNo(String reimburserNo) {
        this.reimburserNo = reimburserNo;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentNo() {
        return departmentNo;
    }

    public void setDepartmentNo(String departmentNo) {
        this.departmentNo = departmentNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getSubsidyTotal() {
        return subsidyTotal;
    }

    public void setSubsidyTotal(BigDecimal subsidyTotal) {
        this.subsidyTotal = subsidyTotal;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}

