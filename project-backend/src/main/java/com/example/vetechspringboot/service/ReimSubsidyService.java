package com.example.vetechspringboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.vo.Result;

import java.util.List;

public interface ReimSubsidyService extends IService<ReimSubsidy> {

	Result<List<ReimSubsidyDetail>> calculateSubsidy(String mainId);
}

