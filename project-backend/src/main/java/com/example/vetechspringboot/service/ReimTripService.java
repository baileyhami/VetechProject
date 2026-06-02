package com.example.vetechspringboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vetechspringboot.entity.ReimTrip;

import java.util.List;

public interface ReimTripService extends IService<ReimTrip> {

	boolean saveOrReplaceTrips(List<ReimTrip> tripList);
}

