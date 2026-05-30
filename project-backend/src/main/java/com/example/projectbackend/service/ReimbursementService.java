package com.example.projectbackend.service;

import com.example.projectbackend.dto.*;
import com.example.projectbackend.exception.ApiException;
import com.example.projectbackend.repository.LookupRepository;
import com.example.projectbackend.repository.ReimbursementRepository;
import com.example.projectbackend.util.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ReimbursementService {

    private final ReimbursementRepository reimbursementRepository;
    private final LookupRepository lookupRepository;
    private final SubsidyCalculator subsidyCalculator = new SubsidyCalculator();

    public ReimbursementService(ReimbursementRepository reimbursementRepository, LookupRepository lookupRepository) {
        this.reimbursementRepository = reimbursementRepository;
        this.lookupRepository = lookupRepository;
    }

    public List<ReimbursementListItemDto> list(String id, String title, String reason, String companyId,
                                               String departmentId, String reimburserId, String businessTypeId) {
        return reimbursementRepository.findList(id, title, reason, companyId, departmentId, reimburserId, businessTypeId);
    }

    public ReimbursementDetailDto detail(String id) {
        MainDto main = reimbursementRepository.findMainById(id)
                .orElseThrow(() -> new ApiException("报销单不存在"));
        ReimbursementDetailDto dto = new ReimbursementDetailDto();
        dto.setMain(main);
        dto.setTrips(reimbursementRepository.findTrips(id));
        dto.setSubsidies(reimbursementRepository.findSubsidies(id));
        dto.setSubsidyDetails(reimbursementRepository.findSubsidyDetails(id));
        dto.setApportion(reimbursementRepository.findApportion(id));
        return dto;
    }

    @Transactional
    public String create(ReimbursementUpsertRequest request) {
        validateTrips(request.getTrips());

        MainDto main = buildMain(request.getMain(), null);
        main.setId(IdGenerator.newId());
        main.setCreationTime(LocalDateTime.now());
        main.setStatus(0);

        persistAll(main, request.getTrips(), request.getApportion());
        return main.getId();
    }

    @Transactional
    public void update(String id, ReimbursementUpsertRequest request) {
        MainDto existing = reimbursementRepository.findMainById(id)
                .orElseThrow(() -> new ApiException("报销单不存在"));
        validateTrips(request.getTrips());

        MainDto main = buildMain(request.getMain(), existing);
        main.setId(id);
        main.setCreationTime(existing.getCreationTime());

        reimbursementRepository.deleteByMainId(id);
        persistAll(main, request.getTrips(), request.getApportion());
    }

    @Transactional
    public void submit(String id) {
        MainDto main = reimbursementRepository.findMainById(id)
                .orElseThrow(() -> new ApiException("报销单不存在"));
        if (main.getStatus() == 2) {
            throw new ApiException("单据已作废" );
        }

        validateSubmit(id, main);
        main.setStatus(1);
        reimbursementRepository.updateMain(main);
    }

    @Transactional
    public void voidDocument(String id) {
        MainDto main = reimbursementRepository.findMainById(id)
                .orElseThrow(() -> new ApiException("报销单不存在"));
        main.setStatus(2);
        reimbursementRepository.updateMain(main);
    }

    private MainDto buildMain(MainInput input, MainDto fallback) {
        MainDto main = new MainDto();
        main.setReimbursementTitle(input.getReimbursementTitle());
        main.setReimburserId(input.getReimburserId());
        main.setReimDepartmentId(input.getReimDepartmentId());
        main.setReimCompanyId(input.getReimCompanyId());
        main.setBusinessTypeId(input.getBusinessTypeId());
        main.setBusinessTripReason(input.getBusinessTripReason());
        main.setRemarks(input.getRemarks());

        if (fallback != null) {
            main.setStatus(fallback.getStatus());
        }
        return main;
    }

    private void persistAll(MainDto main, List<TripInput> trips, List<ApportionInput> apportions) {
        List<TripInput> safeTrips = trips == null ? new ArrayList<>() : trips;
        List<ApportionInput> safeApportion = apportions == null ? new ArrayList<>() : apportions;

        Map<String, CityDto> cityMap = new HashMap<>();
        for (CityDto city : lookupRepository.findCities()) {
            cityMap.put(city.getNo(), city);
        }

        BigDecimal subsidyTotal = BigDecimal.ZERO;
        BigDecimal mealTotal = BigDecimal.ZERO;
        BigDecimal transportTotal = BigDecimal.ZERO;
        BigDecimal phoneTotal = BigDecimal.ZERO;

        reimbursementRepository.insertMain(main);

        for (TripInput tripInput : safeTrips) {
            TripDto trip = new TripDto();
            trip.setId(IdGenerator.newId());
            trip.setMainId(main.getId());
            trip.setTravelerId(tripInput.getTravelerId());
            trip.setDepartureCityNo(tripInput.getDepartureCityNo());
            trip.setArrivalCityNo(tripInput.getArrivalCityNo());
            trip.setDepartureDate(tripInput.getDepartureDate());
            trip.setArrivalDate(tripInput.getArrivalDate());
            trip.setTripRemark(tripInput.getTripRemark());
            trip.setCreateTime(LocalDateTime.now());
            reimbursementRepository.insertTrip(trip);

            tripInput.setId(trip.getId());
            CityDto city = cityMap.getOrDefault(tripInput.getArrivalCityNo(), new CityDto());
            if (city.getNo() == null) {
                city.setNo(tripInput.getArrivalCityNo());
                city.setType("3");
            }

            SubsidyCalculator.TripSubsidyResult result = subsidyCalculator.calculate(tripInput, city);
            SubsidyDto subsidy = result.getSubsidy();
            subsidy.setMainId(main.getId());
            subsidy.setTripId(trip.getId());
            subsidy.setTravelerId(trip.getTravelerId());
            subsidy.setTripRoute(formatTripRoute(tripInput, cityMap));
            subsidy.setSubsidyCityNo(tripInput.getArrivalCityNo());

            reimbursementRepository.insertSubsidy(subsidy);

            for (SubsidyDetailDto detail : result.getDetails()) {
                detail.setSubsidyId(subsidy.getId());
                reimbursementRepository.insertSubsidyDetail(detail);
            }

            subsidyTotal = subsidyTotal.add(subsidy.getSubsidyAmount());
            mealTotal = mealTotal.add(subsidy.getMealAllowance());
            transportTotal = transportTotal.add(subsidy.getTransportationAllowance());
            phoneTotal = phoneTotal.add(subsidy.getPhoneAllowance());
        }

        int sortNo = 1;
        for (ApportionInput input : safeApportion) {
            ApportionDto apportion = new ApportionDto();
            apportion.setId(IdGenerator.newId());
            apportion.setMainId(main.getId());
            apportion.setReimCompanyId(input.getReimCompanyId());
            apportion.setProjectId(input.getProjectId());
            apportion.setApportionRatio(input.getApportionRatio());
            apportion.setApportionAmount(input.getApportionAmount());
            apportion.setSortNo(input.getSortNo() != null ? input.getSortNo() : sortNo++);
            reimbursementRepository.insertApportion(apportion);
        }

        main.setSubsidyTotal(scale(subsidyTotal));
        main.setMealAllowance(scale(mealTotal));
        main.setTransportationAllowance(scale(transportTotal));
        main.setPhoneAllowance(scale(phoneTotal));
        reimbursementRepository.updateMain(main);
    }

    private void validateTrips(List<TripInput> trips) {
        if (trips == null || trips.isEmpty()) {
            return;
        }

        Map<String, List<DateRange>> travelerRanges = new HashMap<>();
        for (TripInput trip : trips) {
            if (trip.getArrivalDate().isBefore(trip.getDepartureDate())) {
                throw new ApiException("到达日期不可早于出发日期");
            }
            travelerRanges.computeIfAbsent(trip.getTravelerId(), key -> new ArrayList<>())
                    .add(new DateRange(trip.getDepartureDate(), trip.getArrivalDate()));
        }

        for (Map.Entry<String, List<DateRange>> entry : travelerRanges.entrySet()) {
            List<DateRange> ranges = entry.getValue();
            ranges.sort(Comparator.comparing(DateRange::getStart));
            for (int i = 1; i < ranges.size(); i++) {
                if (!ranges.get(i).getStart().isAfter(ranges.get(i - 1).getEnd())) {
                    throw new ApiException("同一出行人行程日期重叠");
                }
            }
        }
    }

    private void validateSubmit(String id, MainDto main) {
        if (main.getReimbursementTitle() == null || main.getReimbursementTitle().isBlank()) {
            throw new ApiException("报销标题必填");
        }
        if (main.getBusinessTripReason() == null || main.getBusinessTripReason().isBlank()) {
            throw new ApiException("出差事由必填");
        }
        if (main.getReimburserId() == null || main.getReimburserId().isBlank()) {
            throw new ApiException("报销人必填");
        }
        if (main.getReimDepartmentId() == null || main.getReimDepartmentId().isBlank()) {
            throw new ApiException("报销部门必填");
        }
        if (main.getReimCompanyId() == null || main.getReimCompanyId().isBlank()) {
            throw new ApiException("费用归���公司必填");
        }
        if (main.getBusinessTypeId() == null || main.getBusinessTypeId().isBlank()) {
            throw new ApiException("业务类型必填");
        }

        List<TripDto> trips = reimbursementRepository.findTrips(id);
        if (trips.isEmpty()) {
            throw new ApiException("请补录行程");
        }

        Map<String, List<DateRange>> travelerRanges = new HashMap<>();
        for (TripDto trip : trips) {
            travelerRanges.computeIfAbsent(trip.getTravelerId(), key -> new ArrayList<>())
                    .add(new DateRange(trip.getDepartureDate(), trip.getArrivalDate()));
        }
        for (List<DateRange> ranges : travelerRanges.values()) {
            ranges.sort(Comparator.comparing(DateRange::getStart));
            for (int i = 1; i < ranges.size(); i++) {
                if (!ranges.get(i).getStart().isAfter(ranges.get(i - 1).getEnd())) {
                    throw new ApiException("提交失败：行程日期重复");
                }
            }
        }

        List<ApportionDto> apportion = reimbursementRepository.findApportion(id);
        BigDecimal ratioTotal = BigDecimal.ZERO;
        BigDecimal amountTotal = BigDecimal.ZERO;
        for (ApportionDto item : apportion) {
            ratioTotal = ratioTotal.add(defaultZero(item.getApportionRatio()));
            amountTotal = amountTotal.add(defaultZero(item.getApportionAmount()));
        }

        if (ratioTotal.compareTo(BigDecimal.ONE) != 0) {
            throw new ApiException("分摊比例合计必须为100%" );
        }
        if (amountTotal.compareTo(defaultZero(main.getSubsidyTotal())) != 0) {
            throw new ApiException("分摊金额合计必须等于补助总金额" );
        }
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatTripRoute(TripInput trip, Map<String, CityDto> cityMap) {
        CityDto depart = cityMap.get(trip.getDepartureCityNo());
        CityDto arrive = cityMap.get(trip.getArrivalCityNo());
        String departName = depart != null ? depart.getName() : trip.getDepartureCityNo();
        String arriveName = arrive != null ? arrive.getName() : trip.getArrivalCityNo();
        return departName + "-" + arriveName;
    }

    private static class DateRange {
        private final LocalDate start;
        private final LocalDate end;

        private DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }

        public LocalDate getStart() {
            return start;
        }

        public LocalDate getEnd() {
            return end;
        }
    }
}

