package com.example.vetechspringboot.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.vo.BusinessTypeOptionVO;
import com.example.vetechspringboot.vo.ReimCompanyOptionVO;
import com.example.vetechspringboot.vo.ReimDepartmentOptionVO;
import com.example.vetechspringboot.vo.ReimEmployeeOptionVO;
import com.example.vetechspringboot.vo.ReimMainListVO;

import java.util.List;

public interface ReimMainService extends IService<ReimMain> {
    IPage<ReimMainListVO> selectReimMainList(int current, int size, ReimMainQueryDTO query);

    List<ReimCompanyOptionVO> listCompanyOptions();

    List<ReimDepartmentOptionVO> listDepartmentOptions();

    List<ReimEmployeeOptionVO> listEmployeeOptions();

    List<BusinessTypeOptionVO> listBusinessTypeOptions();
}

