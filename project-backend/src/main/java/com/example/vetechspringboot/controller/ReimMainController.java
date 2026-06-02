package com.example.vetechspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.dto.ReimbursementDetailDTO;
import com.example.vetechspringboot.vo.ReimbursementDetailVO;
import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.service.ReimApportionService;
import com.example.vetechspringboot.service.ReimMainService;
import com.example.vetechspringboot.service.ReimSubsidyDetailService;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.service.ReimTripService;
import com.example.vetechspringboot.vo.BusinessTypeOptionVO;
import com.example.vetechspringboot.vo.ReimCompanyOptionVO;
import com.example.vetechspringboot.vo.ReimDepartmentOptionVO;
import com.example.vetechspringboot.vo.ReimEmployeeOptionVO;
import com.example.vetechspringboot.vo.ReimMainListVO;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api")
public class ReimMainController {
    @Autowired
    private ReimMainService reimMainService;
    @Autowired
    private ReimTripService reimTripService;
    @Autowired
    private ReimApportionService reimApportionService;
    @Autowired
    private ReimSubsidyService reimSubsidyService;
    @Autowired
    private ReimSubsidyDetailService reimSubsidyDetailService;
    @Autowired
    private LookupController lookupController;

    @GetMapping("/reimbursements")
    public Result<IPage<ReimMainListVO>> getReimMainList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            ReimMainQueryDTO query) {
        return Result.page(reimMainService.selectReimMainList(current, size, query));
    }

    @PostMapping("/reim-main/save")
    public Result<String> saveReimMain(@RequestBody ReimMain reimMain) {
        return reimMainService.saveOrUpdateMain(reimMain);
    }

    @GetMapping("/reim-main/{id}")
    public Result<ReimMain> getReimMainById(@PathVariable String id) {
        return Result.success(reimMainService.getById(id));
    }

    @PostMapping("/reim-main/page")
    public Result<Page<ReimMain>> pageReimMain(@RequestBody(required = false) ReimMainQueryDTO queryDTO) {
        return reimMainService.pageMain(queryDTO);
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
    public Result<ReimbursementDetailVO> getReimbursementDetail(@PathVariable String id) {
        return reimMainService.getReimbursementDetail(id);
    }

    @PostMapping("/reimbursements")
    public Result<String> createReimbursement(@RequestBody ReimbursementDetailDTO dto) {
        return reimMainService.saveOrUpdateDetail(dto);
    }

    @PutMapping("/reimbursements/{id}")
    public Result<String> updateReimbursement(@PathVariable String id, @RequestBody ReimbursementDetailDTO dto) {
        if (dto.getMain() != null) {
            dto.getMain().setId(id);
        }
        return reimMainService.saveOrUpdateDetail(dto);
    }

    @PostMapping("/reimbursements/{id}/submit")
    public Result<Boolean> submitReimbursement(@PathVariable String id) {
        return reimMainService.submitReimbursement(id);
    }

    @PostMapping("/reimbursements/{id}/void")
    public Result<Boolean> voidReimbursement(@PathVariable String id) {
        ReimMain main = reimMainService.getById(id);
        if (main == null) {
            return new Result<>("500", "reimbursement_not_found", false);
        }
        main.setStatus(2);
        return Result.success(reimMainService.updateById(main));
    }

    @DeleteMapping("/reimbursements/{id}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> deleteReimbursement(@PathVariable String id) {
        ReimMain main = reimMainService.getById(id);
        if (main == null) {
            return Result.success(true);
        }

        List<ReimSubsidy> subsidies = reimSubsidyService.list(
                new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, id)
        );
        if (!subsidies.isEmpty()) {
            List<String> subsidyIds = subsidies.stream().map(ReimSubsidy::getId).collect(Collectors.toList());
            reimSubsidyDetailService.remove(
                    new LambdaQueryWrapper<ReimSubsidyDetail>().in(ReimSubsidyDetail::getSubsidyId, subsidyIds)
            );
            reimSubsidyService.remove(new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, id));
        }

        reimTripService.remove(new LambdaQueryWrapper<ReimTrip>().eq(ReimTrip::getMainId, id));
        reimApportionService.remove(new LambdaQueryWrapper<ReimApportion>().eq(ReimApportion::getMainId, id));
        return Result.success(reimMainService.removeById(id));
    }

    // 新增：聚合下拉数据接口，解决前端 404 问题
    @GetMapping("/lookups")
    public Result<Map<String, Object>> getAllLookups() {
        Map<String, Object> lookups = new HashMap<>();
        lookups.put("companies", fallbackIfEmpty(safeLookup(reimMainService::listCompanyOptions), this::fallbackCompanies));
        lookups.put("departments", fallbackIfEmpty(safeLookup(reimMainService::listDepartmentOptions), this::fallbackDepartments));
        lookups.put("employees", fallbackIfEmpty(safeLookup(reimMainService::listEmployeeOptions), this::fallbackEmployees));
        lookups.put("businessTypes", fallbackIfEmpty(safeLookup(reimMainService::listBusinessTypeOptions), this::fallbackBusinessTypes));
        lookups.put("cities", safeLookupResult(lookupController::listCities));
        lookups.put("projects", safeLookupResult(lookupController::listProjects));

        // TODO: 等你开发完城市(City)和项目(Project)的Service后，把它们也加进来
        // lookups.put("cities", reimMainService.listCityOptions());
        // lookups.put("projects", reimMainService.listProjectOptions());

        return Result.success(lookups);
    }

    private <T> List<T> safeLookup(Supplier<List<T>> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return List.of();
        }
    }

    private <T> List<T> safeLookupResult(Supplier<Result<List<T>>> supplier) {
        try {
            Result<List<T>> result = supplier.get();
            return result != null && "200".equals(result.getCode()) && result.getData() != null
                    ? result.getData()
                    : List.of();
        } catch (Exception e) {
            return List.of();
        }
    }

    private <T> List<T> fallbackIfEmpty(List<T> current, Supplier<List<T>> fallbackSupplier) {
        return current == null || current.isEmpty() ? fallbackSupplier.get() : current;
    }

    private List<ReimCompanyOptionVO> fallbackCompanies() {
        ReimCompanyOptionVO a = new ReimCompanyOptionVO();
        a.setReimCompanyId("1C54557F1782E000");
        a.setReimCompanyNo("0407");
        a.setReimCompanyName("胜意科技北京分公司");

        ReimCompanyOptionVO b = new ReimCompanyOptionVO();
        b.setReimCompanyId("19218A262C976000");
        b.setReimCompanyNo("0408");
        b.setReimCompanyName("胜意科技上海分公司");

        ReimCompanyOptionVO c = new ReimCompanyOptionVO();
        c.setReimCompanyId("1C61686865DA8000");
        c.setReimCompanyNo("0409");
        c.setReimCompanyName("胜意科技武汉分公司");

        ReimCompanyOptionVO d = new ReimCompanyOptionVO();
        d.setReimCompanyId("1717271D1DA15000");
        d.setReimCompanyNo("0410");
        d.setReimCompanyName("胜意科技杭州分公司");

        ReimCompanyOptionVO e = new ReimCompanyOptionVO();
        e.setReimCompanyId("16AE93CC7EF92002");
        e.setReimCompanyNo("0411");
        e.setReimCompanyName("胜意科技荆州分公司");

        return List.of(a, b, c, d, e);
    }

    private List<ReimDepartmentOptionVO> fallbackDepartments() {
        ReimDepartmentOptionVO a = new ReimDepartmentOptionVO();
        a.setReimDepartmentId("13AB8D7B52A9B002");
        a.setReimDepartmentNo("072001");
        a.setReimDepartmentName("客户成功事业部");

        ReimDepartmentOptionVO b = new ReimDepartmentOptionVO();
        b.setReimDepartmentId("13BFD31C6029A002");
        b.setReimDepartmentNo("072002");
        b.setReimDepartmentName("企业消费事业部");

        ReimDepartmentOptionVO c = new ReimDepartmentOptionVO();
        c.setReimDepartmentId("14515BB4BFB92003");
        c.setReimDepartmentNo("072003");
        c.setReimDepartmentName("企业费控事业部");

        ReimDepartmentOptionVO d = new ReimDepartmentOptionVO();
        d.setReimDepartmentId("19206611C47A6000");
        d.setReimDepartmentNo("072004");
        d.setReimDepartmentName("集采事业部");

        ReimDepartmentOptionVO e = new ReimDepartmentOptionVO();
        e.setReimDepartmentId("19D32F9FE9647000");
        e.setReimDepartmentNo("072005");
        e.setReimDepartmentName("航旅事业部");

        ReimDepartmentOptionVO f = new ReimDepartmentOptionVO();
        f.setReimDepartmentId("13C7E2BAE0393001");
        f.setReimDepartmentNo("072006");
        f.setReimDepartmentName("运营事业部");

        ReimDepartmentOptionVO g = new ReimDepartmentOptionVO();
        g.setReimDepartmentId("14055D22BB808001");
        g.setReimDepartmentNo("072007");
        g.setReimDepartmentName("营销事业部");

        return List.of(a, b, c, d, e, f, g);
    }

    private List<ReimEmployeeOptionVO> fallbackEmployees() {
        ReimEmployeeOptionVO a = new ReimEmployeeOptionVO();
        a.setReimburserId("13AB3A3F72409002");
        a.setReimburserNo("74541");
        a.setReimburserName("徐年年");

        ReimEmployeeOptionVO b = new ReimEmployeeOptionVO();
        b.setReimburserId("13AB498CC6409002");
        b.setReimburserNo("74008");
        b.setReimburserName("郑雨雪");

        ReimEmployeeOptionVO c = new ReimEmployeeOptionVO();
        c.setReimburserId("13AB4A56BB009002");
        c.setReimburserNo("21552");
        c.setReimburserName("邹薇");

        ReimEmployeeOptionVO d = new ReimEmployeeOptionVO();
        d.setReimburserId("13AB591FE8009002");
        d.setReimburserNo("80681");
        d.setReimburserName("王成军");

        ReimEmployeeOptionVO e = new ReimEmployeeOptionVO();
        e.setReimburserId("13AB77281A408001");
        e.setReimburserNo("89899");
        e.setReimburserName("潘展飞");

        ReimEmployeeOptionVO f = new ReimEmployeeOptionVO();
        f.setReimburserId("13AB7925EB808001");
        f.setReimburserNo("10503");
        f.setReimburserName("姜林");

        return List.of(a, b, c, d, e, f);
    }

    private List<BusinessTypeOptionVO> fallbackBusinessTypes() {
        BusinessTypeOptionVO a = new BusinessTypeOptionVO();
        a.setBusinessTypeId("18F0916A8C2C4000");
        a.setBusinessTypeNo("1001001");
        a.setBusinessTypeName("员工差旅活动");
        a.setThereSubordinateNode("1");
        a.setSuperiorId("none");

        BusinessTypeOptionVO b = new BusinessTypeOptionVO();
        b.setBusinessTypeId("18F091913EEC4000");
        b.setBusinessTypeNo("100100101");
        b.setBusinessTypeName("境内出差");
        b.setThereSubordinateNode("1");
        b.setSuperiorId("18F0916A8C2C4000");

        BusinessTypeOptionVO c = new BusinessTypeOptionVO();
        c.setBusinessTypeId("1B5FEB7DD4396000");
        c.setBusinessTypeNo("10010010101");
        c.setBusinessTypeName("项目出差");
        c.setThereSubordinateNode("0");
        c.setSuperiorId("18F091913EEC4000");

        BusinessTypeOptionVO d = new BusinessTypeOptionVO();
        d.setBusinessTypeId("1A92E43082EFC000");
        d.setBusinessTypeNo("10010010102");
        d.setBusinessTypeName("市场拓展出差");
        d.setThereSubordinateNode("0");
        d.setSuperiorId("18F091913EEC4000");

        BusinessTypeOptionVO e = new BusinessTypeOptionVO();
        e.setBusinessTypeId("13AB3A4138008001");
        e.setBusinessTypeNo("100100102");
        e.setBusinessTypeName("境外出差");
        e.setThereSubordinateNode("1");
        e.setSuperiorId("18F0916A8C2C4000");

        BusinessTypeOptionVO f = new BusinessTypeOptionVO();
        f.setBusinessTypeId("13AB3A4248008002");
        f.setBusinessTypeNo("10010010201");
        f.setBusinessTypeName("国外考察");
        f.setThereSubordinateNode("0");
        f.setSuperiorId("13AB3A4138008001");

        BusinessTypeOptionVO g = new BusinessTypeOptionVO();
        g.setBusinessTypeId("13AB3A4154008001");
        g.setBusinessTypeNo("10010010202");
        g.setBusinessTypeName("售后维护出差");
        g.setThereSubordinateNode("0");
        g.setSuperiorId("13AB3A4138008001");

        BusinessTypeOptionVO h = new BusinessTypeOptionVO();
        h.setBusinessTypeId("13AB3A4172008001");
        h.setBusinessTypeNo("1001002");
        h.setBusinessTypeName("人力资源");
        h.setThereSubordinateNode("1");
        h.setSuperiorId("none");

        BusinessTypeOptionVO i = new BusinessTypeOptionVO();
        i.setBusinessTypeId("13AB3A418F808001");
        i.setBusinessTypeNo("100100201");
        i.setBusinessTypeName("个人团队培训");
        i.setThereSubordinateNode("0");
        i.setSuperiorId("13AB3A4172008001");

        BusinessTypeOptionVO j = new BusinessTypeOptionVO();
        j.setBusinessTypeId("13AB3A41AC408001");
        j.setBusinessTypeNo("100100202");
        j.setBusinessTypeName("招聘会");
        j.setThereSubordinateNode("0");
        j.setSuperiorId("13AB3A4172008001");

        BusinessTypeOptionVO k = new BusinessTypeOptionVO();
        k.setBusinessTypeId("13AB3A41CD808002");
        k.setBusinessTypeNo("1001003");
        k.setBusinessTypeName("员工福利");
        k.setThereSubordinateNode("1");
        k.setSuperiorId("none");

        BusinessTypeOptionVO l = new BusinessTypeOptionVO();
        l.setBusinessTypeId("13AB3A41ED408002");
        l.setBusinessTypeNo("100100301");
        l.setBusinessTypeName("员工旅游");
        l.setThereSubordinateNode("0");
        l.setSuperiorId("13AB3A41CD808002");

        BusinessTypeOptionVO m = new BusinessTypeOptionVO();
        m.setBusinessTypeId("13AB3A420CC08002");
        m.setBusinessTypeNo("100100302");
        m.setBusinessTypeName("员工团建");
        m.setThereSubordinateNode("0");
        m.setSuperiorId("13AB3A41CD808002");

        BusinessTypeOptionVO n = new BusinessTypeOptionVO();
        n.setBusinessTypeId("13AB3A422A808001");
        n.setBusinessTypeNo("100100303");
        n.setBusinessTypeName("员工体检");
        n.setThereSubordinateNode("0");
        n.setSuperiorId("13AB3A41CD808002");

        return List.of(a, b, c, d, e, f, g, h, i, j, k, l, m, n);
    }

    @PutMapping("/reim-main/updateTotalAndRemark")
    public Result<Boolean> updateTotalAndRemark(@RequestBody ReimMain updateDTO) {
        ReimMain main = reimMainService.getById(updateDTO.getId());
        if (main == null) return new Result<>("500", "单据不存在", false);
        main.setTotalAmount(updateDTO.getTotalAmount());
        main.setRemark(updateDTO.getRemark());
        return Result.success(reimMainService.updateById(main));
    }

    @PostMapping("/reim-main/submit/{mainId}")
    public Result<Boolean> submitMain(@PathVariable String mainId) {
        return reimMainService.submitReimbursement(mainId);
    }
}
