package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.entity.BusinessType;
import com.example.vetechspringboot.entity.ReimCompany;
import com.example.vetechspringboot.entity.ReimDepartment;
import com.example.vetechspringboot.entity.ReimEmployee;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.mapper.BusinessTypeMapper;
import com.example.vetechspringboot.mapper.ReimCompanyMapper;
import com.example.vetechspringboot.mapper.ReimDepartmentMapper;
import com.example.vetechspringboot.mapper.ReimEmployeeMapper;
import com.example.vetechspringboot.mapper.ReimMainMapper;
import com.example.vetechspringboot.service.ReimMainService;
import com.example.vetechspringboot.vo.BusinessTypeOptionVO;
import com.example.vetechspringboot.vo.ReimCompanyOptionVO;
import com.example.vetechspringboot.vo.ReimDepartmentOptionVO;
import com.example.vetechspringboot.vo.ReimEmployeeOptionVO;
import com.example.vetechspringboot.vo.ReimMainListVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReimMainServiceImpl extends ServiceImpl<ReimMainMapper, ReimMain> implements ReimMainService {
    @Autowired
    private ReimMainMapper reimMainMapper;
    @Autowired
    private ReimCompanyMapper reimCompanyMapper;
    @Autowired
    private ReimDepartmentMapper reimDepartmentMapper;
    @Autowired
    private ReimEmployeeMapper reimEmployeeMapper;
    @Autowired
    private BusinessTypeMapper businessTypeMapper;

    @Override
    public IPage<ReimMainListVO> selectReimMainList(int current, int size, ReimMainQueryDTO query) {
        Page<ReimMainListVO> page = new Page<>(current, size);
        return reimMainMapper.selectReimMainList(page, query);
    }

    @Override
    public List<ReimCompanyOptionVO> listCompanyOptions() {
        List<ReimCompany> companies = reimCompanyMapper.selectList(null);
        return companies.stream().map(item -> {
            ReimCompanyOptionVO vo = new ReimCompanyOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReimDepartmentOptionVO> listDepartmentOptions() {
        List<ReimDepartment> departments = reimDepartmentMapper.selectList(null);
        return departments.stream().map(item -> {
            ReimDepartmentOptionVO vo = new ReimDepartmentOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReimEmployeeOptionVO> listEmployeeOptions() {
        List<ReimEmployee> employees = reimEmployeeMapper.selectList(null);
        return employees.stream().map(item -> {
            ReimEmployeeOptionVO vo = new ReimEmployeeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BusinessTypeOptionVO> listBusinessTypeOptions() {
        List<BusinessType> businessTypes = businessTypeMapper.selectList(null);
        return businessTypes.stream().map(item -> {
            BusinessTypeOptionVO vo = new BusinessTypeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}

