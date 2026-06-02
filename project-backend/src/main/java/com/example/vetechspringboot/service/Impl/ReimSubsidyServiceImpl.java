package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vetechspringboot.entity.ReimCity;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.ReimCityMapper;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.mapper.ReimSubsidyMapper;
import com.example.vetechspringboot.mapper.ReimTripMapper;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReimSubsidyServiceImpl extends ServiceImpl<ReimSubsidyMapper, ReimSubsidy> implements ReimSubsidyService {

	@Autowired
	private ReimTripMapper reimTripMapper;

	@Autowired
	private com.example.vetechspringboot.service.ReimSubsidyDetailService reimSubsidyDetailService;

	@Autowired
	private ReimCityMapper reimCityMapper;

	@Override
	public Result<List<ReimSubsidyDetail>> calculateSubsidy(String mainId) {
		try {
			if (mainId == null || mainId.trim().isEmpty()) {
				return Result.error(null);
			}

			List<ReimTrip> trips = reimTripMapper.selectList(
					new LambdaQueryWrapper<ReimTrip>()
							.eq(ReimTrip::getMainId, mainId)
							.orderByAsc(ReimTrip::getDepartureDate)
			);

			List<ReimSubsidy> oldSubsidies = this.list(new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, mainId));
			if (!oldSubsidies.isEmpty()) {
				List<String> oldIds = oldSubsidies.stream().map(ReimSubsidy::getId).collect(Collectors.toList());
				reimSubsidyDetailService.remove(new LambdaQueryWrapper<ReimSubsidyDetail>().in(ReimSubsidyDetail::getSubsidyId, oldIds));
				this.remove(new LambdaQueryWrapper<ReimSubsidy>().eq(ReimSubsidy::getMainId, mainId));
			}

			List<ReimSubsidy> subsidies = new ArrayList<>();
			List<ReimSubsidyDetail> details = new ArrayList<>();

			for (ReimTrip trip : trips) {
				LocalDate start = toLocalDate(trip.getDepartureDate());
				LocalDate end = toLocalDate(trip.getArrivalDate());
				if (start == null || end == null || end.isBefore(start)) {
					continue;
				}

				String cityNo = trip.getArrivalCityNo();
				String cityType = resolveCityType(cityNo);
				BigDecimal mealStd = resolveMealStd(cityType);
				BigDecimal transportStd = BigDecimal.valueOf(40);
				BigDecimal phoneStd = BigDecimal.valueOf(40);
				BigDecimal dailyAmount = mealStd.add(transportStd).add(phoneStd);

				ReimSubsidy subsidy = new ReimSubsidy();
				subsidy.setId(UUID.randomUUID().toString().replace("-", ""));
				subsidy.setMainId(mainId);
				subsidy.setTripId(trip.getId());
				subsidy.setTravelerId(trip.getTravelerId());
				subsidy.setTravelDateRange(start + "~" + end);
				subsidy.setSubsidyDays((int) (end.toEpochDay() - start.toEpochDay() + 1));
				subsidy.setTripRoute(trip.getDepartureCityNo() + "-" + trip.getArrivalCityNo());
				subsidy.setSubsidyCityNo(cityNo);
				subsidy.setMealAllowance(mealStd.multiply(BigDecimal.valueOf(subsidy.getSubsidyDays())));
				subsidy.setTransportationAllowance(transportStd.multiply(BigDecimal.valueOf(subsidy.getSubsidyDays())));
				subsidy.setPhoneAllowance(phoneStd.multiply(BigDecimal.valueOf(subsidy.getSubsidyDays())));
				subsidy.setApplyAmount(dailyAmount.multiply(BigDecimal.valueOf(subsidy.getSubsidyDays())));
				subsidy.setSubsidyAmount(subsidy.getApplyAmount());
				subsidies.add(subsidy);

				for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
					ReimSubsidyDetail detail = new ReimSubsidyDetail();
					detail.setId(UUID.randomUUID().toString().replace("-", ""));
					detail.setSubsidyId(subsidy.getId());
					detail.setDetailDate(date);
					detail.setWeekDay(toWeekDay(date));
					detail.setCityNo(cityNo);
					detail.setMealStd(mealStd);
					detail.setMealAmount(mealStd);
					detail.setTransportStd(transportStd);
					detail.setTransportAmount(transportStd);
					detail.setPhoneStd(phoneStd);
					detail.setPhoneAmount(phoneStd);
					detail.setIsSelected(1);
					details.add(detail);
				}
			}

			if (!subsidies.isEmpty()) {
				this.saveBatch(subsidies);
			}
			if (!details.isEmpty()) {
				reimSubsidyDetailService.saveBatch(details);
			}

			return Result.success(details);
		} catch (Exception e) {
			return Result.error(null);
		}
	}

	private LocalDate toLocalDate(java.util.Date date) {
		if (date == null) {
			return null;
		}
		if (date instanceof java.sql.Date sqlDate) {
			return sqlDate.toLocalDate();
		}
		return date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	}

	private String resolveCityType(String cityNo) {
		if (cityNo == null || cityNo.trim().isEmpty()) {
			return "2";
		}
		ReimCity city = reimCityMapper.selectById(cityNo);
		return city == null || city.getCityType() == null ? "2" : city.getCityType();
	}

	private BigDecimal resolveMealStd(String cityType) {
		if ("1".equals(cityType)) {
			return BigDecimal.valueOf(100);
		}
		if ("3".equals(cityType)) {
			return BigDecimal.valueOf(50);
		}
		return BigDecimal.valueOf(80);
	}

	private String toWeekDay(LocalDate date) {
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		switch (dayOfWeek) {
			case MONDAY: return "周一";
			case TUESDAY: return "周二";
			case WEDNESDAY: return "周三";
			case THURSDAY: return "周四";
			case FRIDAY: return "周五";
			case SATURDAY: return "周六";
			default: return "周日";
		}
	}

}

