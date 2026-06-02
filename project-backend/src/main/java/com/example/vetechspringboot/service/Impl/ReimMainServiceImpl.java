package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.dto.ReimbursementDetailDTO;
import com.example.vetechspringboot.entity.BusinessType;
import com.example.vetechspringboot.entity.ReimCompany;
import com.example.vetechspringboot.entity.ReimDepartment;
import com.example.vetechspringboot.entity.ReimEmployee;
import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.BusinessTypeMapper;
import com.example.vetechspringboot.mapper.ReimApportionMapper;
import com.example.vetechspringboot.mapper.ReimSubsidyMapper;
import com.example.vetechspringboot.mapper.ReimTripMapper;
import com.example.vetechspringboot.service.ReimApportionService;
import com.example.vetechspringboot.service.ReimSubsidyDetailService;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.service.ReimTripService;
import com.example.vetechspringboot.vo.ReimbursementDetailVO;
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
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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
    @Autowired
    private ReimTripMapper tripMapper;
    @Autowired
    private ReimApportionMapper apportionMapper;
    @Autowired
    private ReimSubsidyMapper reimSubsidyMapper;
    @Autowired
    private ReimTripService reimTripService;
    @Autowired
    private ReimSubsidyService reimSubsidyService;
    @Autowired
    private ReimSubsidyDetailService reimSubsidyDetailService;
    @Autowired
    private ReimApportionService reimApportionService;

    @Override
    public IPage<ReimMainListVO> selectReimMainList(int current, int size, ReimMainQueryDTO query) {
        Page<ReimMainListVO> page = new Page<>(current, size);
        return reimMainMapper.selectReimMainList(page, query);
    }

    @Override
    public List<ReimCompanyOptionVO> listCompanyOptions() {
        List<ReimCompany> companies = reimCompanyMapper.selectList(
                new LambdaQueryWrapper<ReimCompany>().orderByAsc(ReimCompany::getReimCompanyNo)
        );
        return companies.stream().map(item -> {
            ReimCompanyOptionVO vo = new ReimCompanyOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReimDepartmentOptionVO> listDepartmentOptions() {
        List<ReimDepartment> departments = reimDepartmentMapper.selectList(
                new LambdaQueryWrapper<ReimDepartment>().orderByAsc(ReimDepartment::getReimDepartmentNo)
        );
        return departments.stream().map(item -> {
            ReimDepartmentOptionVO vo = new ReimDepartmentOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ReimEmployeeOptionVO> listEmployeeOptions() {
        List<ReimEmployee> employees = reimEmployeeMapper.selectList(
                new LambdaQueryWrapper<ReimEmployee>().orderByAsc(ReimEmployee::getReimburserNo)
        );
        return employees.stream().map(item -> {
            ReimEmployeeOptionVO vo = new ReimEmployeeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<BusinessTypeOptionVO> listBusinessTypeOptions() {
        List<BusinessType> businessTypes = businessTypeMapper.selectList(
                new LambdaQueryWrapper<BusinessType>().orderByAsc(BusinessType::getBusinessTypeNo)
        );
        return businessTypes.stream().map(item -> {
            BusinessTypeOptionVO vo = new BusinessTypeOptionVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public Result<String> saveOrUpdateMain(ReimMain reimMain) {
        try {
            if (reimMain == null) {
                return Result.error("request_body_is_null");
            }
            if (reimMain.getId() == null || reimMain.getId().trim().isEmpty()) {
                String newId = generateReimbursementNo();
                reimMain.setId(newId);
                if (reimMain.getCreationTime() == null) {
                    reimMain.setCreationTime(LocalDateTime.now());
                }
                if (reimMain.getStatus() == null) {
                    reimMain.setStatus(0);
                }
                int rows = reimMainMapper.insert(reimMain);
                return rows > 0 ? Result.success(newId) : Result.error("insert_failed");
            }
            int rows = reimMainMapper.updateById(reimMain);
            return rows > 0 ? Result.success(reimMain.getId()) : Result.error("update_failed");
        } catch (Exception e) {
            return Result.error("save_or_update_failed: " + e.getMessage());
        }
    }

    @Override
    public Result<Page<ReimMain>> pageMain(ReimMainQueryDTO queryDTO) {
        try {
            int current = queryDTO != null && queryDTO.getCurrent() != null && queryDTO.getCurrent() > 0
                    ? queryDTO.getCurrent() : 1;
            int size = queryDTO != null && queryDTO.getSize() != null && queryDTO.getSize() > 0
                    ? queryDTO.getSize() : 10;

            Page<ReimMain> page = new Page<>(current, size);
            LambdaQueryWrapper<ReimMain> wrapper = new LambdaQueryWrapper<>();

            if (queryDTO != null) {
                wrapper.like(hasText(queryDTO.getReimbursementNo()), ReimMain::getId, queryDTO.getReimbursementNo())
                        .like(hasText(queryDTO.getReimbursementTitle()), ReimMain::getReimbursementTitle, queryDTO.getReimbursementTitle())
                        .like(hasText(queryDTO.getBusinessTripReason()), ReimMain::getBusinessTripReason, queryDTO.getBusinessTripReason())
                        .eq(hasText(queryDTO.getReimCompanyId()), ReimMain::getReimCompanyId, queryDTO.getReimCompanyId())
                        .eq(hasText(queryDTO.getReimDepartmentId()), ReimMain::getReimDepartmentId, queryDTO.getReimDepartmentId())
                        .eq(hasText(queryDTO.getReimburserId()), ReimMain::getReimburserId, queryDTO.getReimburserId())
                        .eq(hasText(queryDTO.getBusinessTypeId()), ReimMain::getBusinessTypeId, queryDTO.getBusinessTypeId())
                        .eq(queryDTO.getStatus() != null, ReimMain::getStatus, queryDTO.getStatus());
            }

            wrapper.orderByDesc(ReimMain::getCreationTime);
            Page<ReimMain> result = reimMainMapper.selectPage(page, wrapper);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(null);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveOrUpdateDetail(ReimbursementDetailDTO dto) {
        if (dto == null || dto.getMain() == null) {
            return Result.error("request_body_is_null");
        }

        Result<String> mainResult = saveOrUpdateMain(dto.getMain());
        if (mainResult == null || !"200".equals(mainResult.getCode()) || mainResult.getData() == null) {
            return mainResult != null ? mainResult : Result.error("main_save_failed");
        }
        String mainId = mainResult.getData();

        List<ReimTrip> trips = dto.getTrips() != null ? dto.getTrips() : Collections.emptyList();
        removeSubsidiesByMainId(mainId);
        if (trips.isEmpty()) {
            reimTripService.remove(new LambdaQueryWrapper<ReimTrip>().eq(ReimTrip::getMainId, mainId));
        } else {
            trips.forEach(trip -> trip.setMainId(mainId));
            if (!reimTripService.saveOrReplaceTrips(trips)) {
                throw new IllegalStateException("trip_save_failed");
            }
        }

        Result<List<ReimSubsidyDetail>> subsidyResult = reimSubsidyService.calculateSubsidy(mainId);
        if (subsidyResult == null || !"200".equals(subsidyResult.getCode())) {
            throw new IllegalStateException("subsidy_calculate_failed");
        }

        List<ReimApportion> apportions = dto.getApportion() != null ? dto.getApportion() : Collections.emptyList();
        saveOrReplaceApportions(mainId, apportions);

        return Result.success(mainId);
    }

    private void removeSubsidiesByMainId(String mainId) {
        List<ReimSubsidy> oldSubsidies = reimSubsidyService.list(
                new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, mainId)
        );
        if (oldSubsidies == null || oldSubsidies.isEmpty()) {
            return;
        }
        List<String> oldIds = oldSubsidies.stream().map(ReimSubsidy::getId).collect(Collectors.toList());
        reimSubsidyDetailService.remove(
                new LambdaQueryWrapper<ReimSubsidyDetail>().in(ReimSubsidyDetail::getSubsidyId, oldIds)
        );
        reimSubsidyService.remove(new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, mainId));
    }

    @Override
    public Result<ReimbursementDetailVO> getReimbursementDetail(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Result.error(null);
        }
        ReimMain main = reimMainMapper.selectById(id);
        if (main == null) {
            return new Result<>("500", "单据不存在", null);
        }

        List<ReimTrip> trips = tripMapper.selectList(
                new LambdaQueryWrapper<ReimTrip>()
                        .eq(ReimTrip::getMainId, id)
                        .orderByAsc(ReimTrip::getDepartureDate)
        );
        List<ReimApportion> apportion = apportionMapper.selectList(
                new LambdaQueryWrapper<ReimApportion>()
                        .eq(ReimApportion::getMainId, id)
                        .orderByAsc(ReimApportion::getSortNo)
        );
        List<ReimSubsidy> subsidies = reimSubsidyMapper.selectList(
                new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, id)
        );

        List<ReimSubsidyDetail> subsidyDetails = Collections.emptyList();
        if (!subsidies.isEmpty()) {
            List<String> subsidyIds = subsidies.stream().map(ReimSubsidy::getId).collect(Collectors.toList());
            subsidyDetails = reimSubsidyDetailService.list(
                    new LambdaQueryWrapper<ReimSubsidyDetail>()
                            .in(ReimSubsidyDetail::getSubsidyId, subsidyIds)
                            .orderByAsc(ReimSubsidyDetail::getDetailDate)
            );
        }

        ReimbursementDetailVO detail = new ReimbursementDetailVO();
        detail.setMain(main);
        detail.setTrips(trips);
        detail.setApportion(apportion);
        detail.setSubsidies(subsidies);
        detail.setSubsidyDetails(subsidyDetails);
        return Result.success(detail);
    }

    private void saveOrReplaceApportions(String mainId, List<ReimApportion> apportionList) {
        reimApportionService.remove(new LambdaQueryWrapper<ReimApportion>().eq(ReimApportion::getMainId, mainId));
        if (apportionList == null || apportionList.isEmpty()) {
            return;
        }
        apportionList.forEach(item -> {
            if (item.getId() == null || item.getId().trim().isEmpty()) {
                item.setId(UUID.randomUUID().toString().replace("-", ""));
            }
            if (item.getProjectId() != null && item.getProjectId().trim().isEmpty()) {
                item.setProjectId(null);
            }
            if (item.getReimCompanyId() != null && item.getReimCompanyId().trim().isEmpty()) {
                item.setReimCompanyId(null);
            }
            item.setMainId(mainId);
        });
        reimApportionService.saveBatch(apportionList);
    }

    @Override
    public Result<Boolean> submitReimbursement(String mainId) {
        // 1. 检查主表状态
        ReimMain main = reimMainMapper.selectById(mainId);
        if (main == null) return new Result<>("500", "单据不存在", false);
        if (main.getStatus() != null && main.getStatus() != 0) {
            return new Result<>("500", "单据已提交，无法重复提交", false);
        }

        // 2. 校验行程完整性
        Long tripCount = tripMapper.selectCount(new LambdaQueryWrapper<ReimTrip>().eq(ReimTrip::getMainId, mainId));
        if (tripCount == null || tripCount == 0) {
            return new Result<>("500", "请至少录入一条出差行程", false);
        }

        // 3. 校验分摊总额是否等于报销总额
        List<ReimApportion> apportions = apportionMapper.selectList(
                new LambdaQueryWrapper<ReimApportion>().eq(ReimApportion::getMainId, mainId));
        BigDecimal apportionSum = apportions.stream()
                .map(ReimApportion::getApportionAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (main.getTotalAmount() != null && main.getTotalAmount().compareTo(apportionSum) != 0) {
            return new Result<>("500", "费用分摊总额(" + apportionSum + ")与报销总金额(" + main.getTotalAmount() + ")不一致，请检查！", false);
        }

        // 4. 更新状态为 1 (审批中)
        main.setStatus(1);
        reimMainMapper.updateById(main);
        return Result.success(true);
    }

    private String generateReimbursementNo() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String prefix = "BX" + datePart;
        Long count = reimMainMapper.selectCount(new LambdaQueryWrapper<ReimMain>().likeRight(ReimMain::getId, prefix));
        long seq = (count == null ? 0 : count) + 1;
        return prefix + String.format("%04d", seq);
    }
}
