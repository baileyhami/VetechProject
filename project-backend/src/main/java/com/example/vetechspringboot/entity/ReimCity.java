package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * Entity for city lookup (fk_city)
 */
@Data
@TableName("fk_city")
public class ReimCity {
    @TableId("city_no")
    private String cityNo;
    private String cityName;
    private String cityType;
}

