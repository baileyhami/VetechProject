package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.ReimTripMapper;
import com.example.vetechspringboot.service.ReimTripService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReimTripServiceImpl extends ServiceImpl<ReimTripMapper, ReimTrip> implements ReimTripService {

	@Override
	public boolean saveOrReplaceTrips(List<ReimTrip> tripList) {
		if (tripList == null || tripList.isEmpty()) {
			return true;
		}

		String mainId = tripList.get(0).getMainId();
		if (mainId == null || mainId.trim().isEmpty()) {
			return false;
		}

		this.remove(new LambdaQueryWrapper<ReimTrip>().eq(ReimTrip::getMainId, mainId));
		for (ReimTrip trip : tripList) {
			if (trip.getId() == null || trip.getId().trim().isEmpty()) {
				trip.setId(UUID.randomUUID().toString().replace("-", ""));
			}
			trip.setMainId(mainId);
		}
		return this.saveBatch(tripList);
	}

}

