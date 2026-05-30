package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vetechspringboot.entity.User;
import com.example.vetechspringboot.mapper.UserMapper;
import com.example.vetechspringboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public IPage<User> selectUserByAge(int current, int size, Integer age) {
        Page<User> page = new Page<>(current, size);
        page.setCurrent(current);
        page.setSize(size);
        IPage<User> iPage = userMapper.selectUserByAge(page, age);
        return iPage;
    }
}
