package com.example.vetechspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.service.ReimTripService;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

/**
 * Controller for trip sub-table operations.
 */
@RestController
@RequestMapping("/api/reim-trip")
public class ReimTripController {

    @Autowired
    private ReimTripService reimTripService;

    /**
     * Save trips by mainId using delete-then-insert strategy.
     */
    @PostMapping("/saveBatch")
    public Result<Boolean> saveBatch(@RequestBody List<ReimTrip> tripList) {
        return Result.success(reimTripService.saveOrReplaceTrips(tripList));
    }

    /**
     * Query trip list by main reimbursement id.
     */
    @GetMapping("/list/{mainId}")
    public Result<List<ReimTrip>> listByMainId(@PathVariable("mainId") String mainId) {
        if (mainId == null || mainId.trim().isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<ReimTrip> trips = reimTripService.list(
                new LambdaQueryWrapper<ReimTrip>()
                        .eq(ReimTrip::getMainId, mainId)
                        .orderByAsc(ReimTrip::getDepartureDate)
        );
        return Result.success(trips);
    }
}

