package com.example.vetechspringboot.controller;

import com.example.vetechspringboot.entity.ReimCompany;
import com.example.vetechspringboot.entity.ReimDepartment;
import com.example.vetechspringboot.entity.ReimEmployee;
import com.example.vetechspringboot.entity.BusinessType;
import com.example.vetechspringboot.entity.ReimCity;
import com.example.vetechspringboot.entity.ReimProject;
import com.example.vetechspringboot.mapper.ReimCompanyMapper;
import com.example.vetechspringboot.mapper.ReimDepartmentMapper;
import com.example.vetechspringboot.mapper.ReimEmployeeMapper;
import com.example.vetechspringboot.mapper.BusinessTypeMapper;
import com.example.vetechspringboot.mapper.ReimCityMapper;
import com.example.vetechspringboot.mapper.ReimProjectMapper;
import com.example.vetechspringboot.vo.ReimCompanyOptionVO;
import com.example.vetechspringboot.vo.ReimDepartmentOptionVO;
import com.example.vetechspringboot.vo.ReimEmployeeOptionVO;
import com.example.vetechspringboot.vo.BusinessTypeOptionVO;
import com.example.vetechspringboot.vo.ReimCityOptionVO;
import com.example.vetechspringboot.vo.ReimProjectOptionVO;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller that exposes lookup (dictionary) data for front-end dropdowns.
 * Provides endpoints for companies, departments, employees, business types,
 * and also cities/projects (with fallback mock data if DB tables are empty).
 */
@RestController
@RequestMapping("/api/lookup")
public class LookupController {
    @Autowired
    private ReimCompanyMapper reimCompanyMapper;

    @Autowired
    private ReimDepartmentMapper reimDepartmentMapper;

    @Autowired
    private ReimEmployeeMapper reimEmployeeMapper;

    @Autowired
    private BusinessTypeMapper businessTypeMapper;

    @Autowired
    private ReimCityMapper reimCityMapper;

    @Autowired
    private ReimProjectMapper reimProjectMapper;

    @GetMapping("/companies")
    public Result<List<ReimCompanyOptionVO>> listCompanies() {
        List<ReimCompany> list = reimCompanyMapper.selectList(null);
        List<ReimCompanyOptionVO> vos = list.stream().map(item -> {
            ReimCompanyOptionVO vo = new ReimCompanyOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/departments")
    public Result<List<ReimDepartmentOptionVO>> listDepartments() {
        List<ReimDepartment> list = reimDepartmentMapper.selectList(null);
        List<ReimDepartmentOptionVO> vos = list.stream().map(item -> {
            ReimDepartmentOptionVO vo = new ReimDepartmentOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/employees")
    public Result<List<ReimEmployeeOptionVO>> listEmployees() {
        List<ReimEmployee> list = reimEmployeeMapper.selectList(null);
        List<ReimEmployeeOptionVO> vos = list.stream().map(item -> {
            ReimEmployeeOptionVO vo = new ReimEmployeeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/business-types")
    public Result<List<BusinessTypeOptionVO>> listBusinessTypes() {
        List<BusinessType> list = businessTypeMapper.selectList(null);
        List<BusinessTypeOptionVO> vos = list.stream().map(item -> {
            BusinessTypeOptionVO vo = new BusinessTypeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/cities")
    public Result<List<ReimCityOptionVO>> listCities() {
        List<ReimCity> list = reimCityMapper.selectList(null);
        if (list == null || list.isEmpty()) {
            // Fallback mock data
            ReimCityOptionVO a = new ReimCityOptionVO();
            a.setCityNo("10119"); a.setCityName("北京"); a.setCityType("1");
            ReimCityOptionVO b = new ReimCityOptionVO();
            b.setCityNo("10621"); b.setCityName("上海"); b.setCityType("1");
            ReimCityOptionVO c = new ReimCityOptionVO();
            c.setCityNo("10458"); c.setCityName("武汉"); c.setCityType("2");
            ReimCityOptionVO d = new ReimCityOptionVO();
            d.setCityNo("10216"); d.setCityName("杭州"); d.setCityType("2");
            ReimCityOptionVO e = new ReimCityOptionVO();
            e.setCityNo("10455"); e.setCityName("荆州"); e.setCityType("3");
            return Result.success(List.of(a, b, c, d, e));
        }
        List<ReimCityOptionVO> vos = list.stream().map(item -> {
            ReimCityOptionVO vo = new ReimCityOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @GetMapping("/projects")
    public Result<List<ReimProjectOptionVO>> listProjects() {
        List<ReimProject> list = reimProjectMapper.selectList(null);
        if (list == null || list.isEmpty()) {
            ReimProjectOptionVO a = new ReimProjectOptionVO();
            a.setProjectId("12BC248B25083001"); a.setProjectNo("nonProjectRelated"); a.setProjectName("非项目类费用归集");
            ReimProjectOptionVO b = new ReimProjectOptionVO();
            b.setProjectId("1C811ABF96195000"); b.setProjectNo("standardR&D"); b.setProjectName("标准研发迭代项目");
            return Result.success(List.of(a, b));
        }
        List<ReimProjectOptionVO> vos = list.stream().map(item -> {
            ReimProjectOptionVO vo = new ReimProjectOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }
}

