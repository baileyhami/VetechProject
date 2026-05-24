package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_reim_company")
public class ReimCompany {
    @TableId("reim_company_id")
    private String reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;
}

