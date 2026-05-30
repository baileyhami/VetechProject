package com.example.projectbackend.service;

import com.example.projectbackend.dto.CityDto;
import com.example.projectbackend.dto.SubsidyDetailDto;
import com.example.projectbackend.dto.SubsidyDto;
import com.example.projectbackend.dto.SubsidyDetailInput;
import com.example.projectbackend.dto.TripInput;
import com.example.projectbackend.util.DateUtils;
import com.example.projectbackend.util.IdGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubsidyCalculator {

    public TripSubsidyResult calculate(TripInput trip, CityDto city) {
        List<SubsidyDetailDto> details = new ArrayList<>();
        Map<LocalDate, SubsidyDetailInput> overrides = toOverrideMap(trip.getSubsidyDetails());

        LocalDate current = trip.getDepartureDate();
        while (!current.isAfter(trip.getArrivalDate())) {
            SubsidyDetailDto detail = buildDetail(current, city, overrides.get(current));
            details.add(detail);
            current = current.plusDays(1);
        }

        SubsidyDto summary = summarize(trip, details, city);
        return new TripSubsidyResult(summary, details);
    }

    private Map<LocalDate, SubsidyDetailInput> toOverrideMap(List<SubsidyDetailInput> overrides) {
        Map<LocalDate, SubsidyDetailInput> map = new HashMap<>();
        if (overrides == null) {
            return map;
        }
        for (SubsidyDetailInput input : overrides) {
            if (input.getDetailDate() != null) {
                map.put(input.getDetailDate(), input);
            }
        }
        return map;
    }

    private SubsidyDetailDto buildDetail(LocalDate date, CityDto city, SubsidyDetailInput override) {
        BigDecimal mealStd = mealStd(city);
        BigDecimal transportStd = new BigDecimal("40.00");
        BigDecimal phoneStd = new BigDecimal("40.00");
        boolean selected = override == null || override.getSelected() == null || override.getSelected();

        BigDecimal mealAmount = amountOrStd(override == null ? null : override.getMealAmount(), mealStd, selected);
        BigDecimal transportAmount = amountOrStd(override == null ? null : override.getTransportAmount(), transportStd, selected);
        BigDecimal phoneAmount = amountOrStd(override == null ? null : override.getPhoneAmount(), phoneStd, selected);

        SubsidyDetailDto detail = new SubsidyDetailDto();
        detail.setId(IdGenerator.newId());
        detail.setDetailDate(date);
        detail.setWeekDay(DateUtils.toWeekday(date));
        detail.setCityNo(override != null && override.getCityNo() != null ? override.getCityNo() : city.getNo());
        detail.setMealStd(mealStd);
        detail.setMealAmount(mealAmount);
        detail.setTransportStd(transportStd);
        detail.setTransportAmount(transportAmount);
        detail.setPhoneStd(phoneStd);
        detail.setPhoneAmount(phoneAmount);
        detail.setSelected(selected);
        return detail;
    }

    private SubsidyDto summarize(TripInput trip, List<SubsidyDetailDto> details, CityDto city) {
        BigDecimal applyTotal = BigDecimal.ZERO;
        BigDecimal subsidyTotal = BigDecimal.ZERO;
        BigDecimal mealTotal = BigDecimal.ZERO;
        BigDecimal transportTotal = BigDecimal.ZERO;
        BigDecimal phoneTotal = BigDecimal.ZERO;

        for (SubsidyDetailDto detail : details) {
            if (!detail.isSelected()) {
                continue;
            }
            applyTotal = applyTotal.add(detail.getMealStd()).add(detail.getTransportStd()).add(detail.getPhoneStd());
            subsidyTotal = subsidyTotal.add(detail.getMealAmount()).add(detail.getTransportAmount()).add(detail.getPhoneAmount());
            mealTotal = mealTotal.add(detail.getMealAmount());
            transportTotal = transportTotal.add(detail.getTransportAmount());
            phoneTotal = phoneTotal.add(detail.getPhoneAmount());
        }

        SubsidyDto summary = new SubsidyDto();
        summary.setId(IdGenerator.newId());
        summary.setTripId(trip.getId());
        summary.setTravelerId(trip.getTravelerId());
        summary.setTravelDateRange(trip.getDepartureDate() + "~" + trip.getArrivalDate());
        summary.setSubsidyDays(details.size());
        summary.setTripRoute(cityName(trip.getDepartureCityNo(), trip.getArrivalCityNo()));
        summary.setSubsidyCityNo(trip.getArrivalCityNo());
        summary.setApplyAmount(applyTotal.setScale(2, RoundingMode.HALF_UP));
        summary.setSubsidyAmount(subsidyTotal.setScale(2, RoundingMode.HALF_UP));
        summary.setMealAllowance(mealTotal.setScale(2, RoundingMode.HALF_UP));
        summary.setTransportationAllowance(transportTotal.setScale(2, RoundingMode.HALF_UP));
        summary.setPhoneAllowance(phoneTotal.setScale(2, RoundingMode.HALF_UP));
        return summary;
    }

    private BigDecimal mealStd(CityDto city) {
        if (city == null || city.getType() == null) {
            return new BigDecimal("50.00");
        }
        switch (city.getType()) {
            case "1":
                return new BigDecimal("100.00");
            case "2":
                return new BigDecimal("80.00");
            default:
                return new BigDecimal("50.00");
        }
    }

    private BigDecimal amountOrStd(BigDecimal input, BigDecimal std, boolean selected) {
        if (!selected) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        if (input == null) {
            return std.setScale(2, RoundingMode.HALF_UP);
        }
        if (input.compareTo(std) > 0) {
            return std.setScale(2, RoundingMode.HALF_UP);
        }
        return input.setScale(2, RoundingMode.HALF_UP);
    }

    private String cityName(String departureCityNo, String arrivalCityNo) {
        return departureCityNo + "-" + arrivalCityNo;
    }

    public static class TripSubsidyResult {
        private final SubsidyDto subsidy;
        private final List<SubsidyDetailDto> details;

        public TripSubsidyResult(SubsidyDto subsidy, List<SubsidyDetailDto> details) {
            this.subsidy = subsidy;
            this.details = details;
        }

        public SubsidyDto getSubsidy() {
            return subsidy;
        }

        public List<SubsidyDetailDto> getDetails() {
            return details;
        }
    }
}

