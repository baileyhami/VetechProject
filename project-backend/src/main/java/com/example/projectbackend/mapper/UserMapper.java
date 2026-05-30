package com.example.vetechspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.vetechspringboot.entity.User;

public interface UserMapper extends BaseMapper<User> {

    IPage<User> selectUserByAge(Page<User> page, Integer age);
}
