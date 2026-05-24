package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_reim_employee")
public class ReimEmployee {
    @TableId("reimburser_id")
    private String reimburserId;
    private String reimburserNo;
    private String reimburserName;
}

