package com.example.projectbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_reim_department")
public class ReimDepartment {
    private String reimDepartmentId;
    private String reimDepartmentNo;
    private String reimDepartmentName;
}

