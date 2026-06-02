package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("fk_reim_trip")
public class ReimTrip {
    @TableId("id")
    private String id;
    private String mainId;
    private String travelerId;
    private String departureCityNo;
    private String arrivalCityNo;
    private Date departureDate;
    private Date arrivalDate;
    private String tripRemark;
    private Date createTime;
}

