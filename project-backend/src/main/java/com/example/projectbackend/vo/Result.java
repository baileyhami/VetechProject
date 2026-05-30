package com.example.vetechspringboot.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private  T data;
    public Result() {}
    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>("200", "success", data);
    }
    public static <T> Result<T> error(T data) {
        return new Result<>("500", "error", data);
    }
    public static <T> Result<IPage<T>> page(IPage<T> page) {
        Result<IPage<T>> result = new Result<IPage<T>>();
        result.setCode("200");
        result.setMessage("success");
        result.setData(page);
        return result;
    }

}

