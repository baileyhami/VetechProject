package com.example.vetechspringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.vetechspringboot.dto.UserDTO;
import com.example.vetechspringboot.entity.User;
import com.example.vetechspringboot.service.UserService;
import com.example.vetechspringboot.vo.Result;
import com.example.vetechspringboot.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/getUser")
    public Result<UserVO> getUser(@RequestBody UserDTO userDTO) {
        UserVO user = new UserVO();
        user.setId(1);
        user.setName(userDTO.getAge());
        return Result.success(user);

    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.list();
        return Result.success(users);
    }

    @GetMapping("/users/getUserPages")
    public Result<?> getUsersPages(@RequestParam int current, @RequestParam int size,
                                   @RequestParam(required = false) Integer age) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (age != null) {
            wrapper.eq("age", age);
        }
        return Result.page(userService.page(page, wrapper));
    }

}
