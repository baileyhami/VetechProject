package com.example.vetechspringboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vetechspringboot.entity.User;

public interface UserService extends IService<User> {

    IPage<User> selectUserByAge(int current, int size, Integer age);
}
