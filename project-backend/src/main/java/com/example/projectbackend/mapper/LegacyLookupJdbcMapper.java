package com.example.projectbackend.mapper;

import com.example.projectbackend.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public class LegacyLookupJdbcMapper {

    private final JdbcTemplate jdbcTemplate;

    public LegacyLookupJdbcMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CompanyDto> findCompanies() {
        String sql = "SELECT reim_company_id, reim_company_no, reim_company_name FROM fk_reim_company ORDER BY reim_company_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CompanyDto dto = new CompanyDto();
            dto.setId(rs.getString("reim_company_id"));
            dto.setNo(rs.getString("reim_company_no"));
            dto.setName(rs.getString("reim_company_name"));
            return dto;
        });
    }

    public List<DepartmentDto> findDepartments() {
        String sql = "SELECT reim_department_id, reim_department_no, reim_department_name FROM fk_reim_department ORDER BY reim_department_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            DepartmentDto dto = new DepartmentDto();
            dto.setId(rs.getString("reim_department_id"));
            dto.setNo(rs.getString("reim_department_no"));
            dto.setName(rs.getString("reim_department_name"));
            return dto;
        });
    }

    public List<EmployeeDto> findEmployees() {
        String sql = "SELECT reimburser_id, reimburser_no, reimburser_name FROM fk_reim_employee ORDER BY reimburser_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EmployeeDto dto = new EmployeeDto();
            dto.setId(rs.getString("reimburser_id"));
            dto.setNo(rs.getString("reimburser_no"));
            dto.setName(rs.getString("reimburser_name"));
            return dto;
        });
    }

    public List<BusinessTypeDto> findBusinessTypes() {
        String sql = "SELECT business_type_id, business_type_no, business_type_name, superior_id, there_subordinate_node FROM fk_business_type ORDER BY business_type_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BusinessTypeDto dto = new BusinessTypeDto();
            dto.setId(rs.getString("business_type_id"));
            dto.setNo(rs.getString("business_type_no"));
            dto.setName(rs.getString("business_type_name"));
            dto.setSuperiorId(rs.getString("superior_id"));
            dto.setHasChildren(rs.getString("there_subordinate_node"));
            return dto;
        });
    }

    public List<CityDto> findCities() {
        String sql = "SELECT city_no, city_name, city_type FROM fk_city ORDER BY city_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            CityDto dto = new CityDto();
            dto.setNo(rs.getString("city_no"));
            dto.setName(rs.getString("city_name"));
            dto.setType(rs.getString("city_type"));
            return dto;
        });
    }

    public List<ProjectDto> findProjects() {
        String sql = "SELECT project_id, project_no, project_name FROM fk_project ORDER BY project_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProjectDto dto = new ProjectDto();
            dto.setId(rs.getString("project_id"));
            dto.setNo(rs.getString("project_no"));
            dto.setName(rs.getString("project_name"));
            return dto;
        });
    }
}


