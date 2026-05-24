package com.example.vetechspringboot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.service.ReimMainService;
import com.example.vetechspringboot.vo.BusinessTypeOptionVO;
import com.example.vetechspringboot.vo.ReimCompanyOptionVO;
import com.example.vetechspringboot.vo.ReimDepartmentOptionVO;
import com.example.vetechspringboot.vo.ReimEmployeeOptionVO;
import com.example.vetechspringboot.vo.ReimMainListVO;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReimMainController {
    @Autowired
    private ReimMainService reimMainService;

    @GetMapping("/reimbursements")
    public Result<IPage<ReimMainListVO>> getReimMainList(@RequestParam int current,
                                                         @RequestParam int size,
                                                         ReimMainQueryDTO query) {
        return Result.page(reimMainService.selectReimMainList(current, size, query));
    }

    @GetMapping("/reimbursements/options/companies")
    public Result<List<ReimCompanyOptionVO>> getCompanyOptions() {
        return Result.success(reimMainService.listCompanyOptions());
    }

    @GetMapping("/reimbursements/options/departments")
    public Result<List<ReimDepartmentOptionVO>> getDepartmentOptions() {
        return Result.success(reimMainService.listDepartmentOptions());
    }

    @GetMapping("/reimbursements/options/employees")
    public Result<List<ReimEmployeeOptionVO>> getEmployeeOptions() {
        return Result.success(reimMainService.listEmployeeOptions());
    }

    @GetMapping("/reimbursements/options/business-types")
    public Result<List<BusinessTypeOptionVO>> getBusinessTypeOptions() {
        return Result.success(reimMainService.listBusinessTypeOptions());
    }

    @GetMapping("/reimbursements/{id}")
    public Result<Object> getReimbursementDetail(@PathVariable String id) {
        return new Result<>("501", "not_implemented", null);
    }

    @PostMapping("/reimbursements")
    public Result<Object> createReimbursement() {
        return new Result<>("501", "not_implemented", null);
    }

    @PutMapping("/reimbursements/{id}")
    public Result<Object> updateReimbursement(@PathVariable String id) {
        return new Result<>("501", "not_implemented", null);
    }
}

