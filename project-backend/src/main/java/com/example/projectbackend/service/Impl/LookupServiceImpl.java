package com.example.projectbackend.service.Impl;

import com.example.projectbackend.dto.*;
import com.example.projectbackend.entity.*;
import com.example.projectbackend.mapper.*;
import com.example.projectbackend.service.LookupService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LookupServiceImpl implements LookupService {

    private final ReimCompanyMapper companyMapper;
    private final ReimDepartmentMapper departmentMapper;
    private final ReimEmployeeMapper employeeMapper;
    private final BusinessTypeMapper businessTypeMapper;
    private final CityMapper cityMapper;
    private final ProjectMapper projectMapper;

    public LookupServiceImpl(ReimCompanyMapper companyMapper,
                             ReimDepartmentMapper departmentMapper,
                             ReimEmployeeMapper employeeMapper,
                             BusinessTypeMapper businessTypeMapper,
                             CityMapper cityMapper,
                             ProjectMapper projectMapper) {
        this.companyMapper = companyMapper;
        this.departmentMapper = departmentMapper;
        this.employeeMapper = employeeMapper;
        this.businessTypeMapper = businessTypeMapper;
        this.cityMapper = cityMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public LookupResponse getLookups() {
        LookupResponse response = new LookupResponse();
        response.setCompanies(toCompanyDtos(companyMapper.selectList(null)));
        response.setDepartments(toDepartmentDtos(departmentMapper.selectList(null)));
        response.setEmployees(toEmployeeDtos(employeeMapper.selectList(null)));
        response.setBusinessTypes(toBusinessTypeDtos(businessTypeMapper.selectList(null)));
        response.setCities(toCityDtos(cityMapper.selectList(null)));
        response.setProjects(toProjectDtos(projectMapper.selectList(null)));
        return response;
    }

    private List<CompanyDto> toCompanyDtos(List<ReimCompany> entities) {
        return entities.stream().map(entity -> {
            CompanyDto dto = new CompanyDto();
            dto.setId(entity.getReimCompanyId());
            dto.setNo(entity.getReimCompanyNo());
            dto.setName(entity.getReimCompanyName());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<DepartmentDto> toDepartmentDtos(List<ReimDepartment> entities) {
        return entities.stream().map(entity -> {
            DepartmentDto dto = new DepartmentDto();
            dto.setId(entity.getReimDepartmentId());
            dto.setNo(entity.getReimDepartmentNo());
            dto.setName(entity.getReimDepartmentName());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<EmployeeDto> toEmployeeDtos(List<ReimEmployee> entities) {
        return entities.stream().map(entity -> {
            EmployeeDto dto = new EmployeeDto();
            dto.setId(entity.getReimburserId());
            dto.setNo(entity.getReimburserNo());
            dto.setName(entity.getReimburserName());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<BusinessTypeDto> toBusinessTypeDtos(List<BusinessType> entities) {
        return entities.stream().map(entity -> {
            BusinessTypeDto dto = new BusinessTypeDto();
            dto.setId(entity.getBusinessTypeId());
            dto.setNo(entity.getBusinessTypeNo());
            dto.setName(entity.getBusinessTypeName());
            dto.setSuperiorId(entity.getSuperiorId());
            dto.setHasChildren(entity.getThereSubordinateNode());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<CityDto> toCityDtos(List<City> entities) {
        return entities.stream().map(entity -> {
            CityDto dto = new CityDto();
            dto.setNo(entity.getCityNo());
            dto.setName(entity.getCityName());
            dto.setType(entity.getCityType());
            return dto;
        }).collect(Collectors.toList());
    }

    private List<ProjectDto> toProjectDtos(List<Project> entities) {
        return entities.stream().map(entity -> {
            ProjectDto dto = new ProjectDto();
            dto.setId(entity.getProjectId());
            dto.setNo(entity.getProjectNo());
            dto.setName(entity.getProjectName());
            return dto;
        }).collect(Collectors.toList());
    }
}

