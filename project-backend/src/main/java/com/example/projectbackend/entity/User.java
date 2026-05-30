package com.example.vetechspringboot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user")
public class User {
    private Integer id;
    private String name;
    private Integer age;
}
