package com.example.projectbackend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("fk_reim_company")
public class ReimCompany {
    private String reimCompanyId;
    private String reimCompanyNo;
    private String reimCompanyName;
}

