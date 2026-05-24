package com.example.vetechspringboot.vo;

import lombok.Data;

@Data
public class BusinessTypeOptionVO {
    private String businessTypeId;
    private String businessTypeNo;
    private String businessTypeName;
    private String superiorId;
    private String thereSubordinateNode;
}

