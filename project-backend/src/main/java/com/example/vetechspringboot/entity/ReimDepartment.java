package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_reim_department")
public class ReimDepartment {
    @TableId("reim_department_id")
    private String reimDepartmentId;
    private String reimDepartmentNo;
    private String reimDepartmentName;
}

