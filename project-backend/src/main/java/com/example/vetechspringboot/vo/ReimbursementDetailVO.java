package com.example.vetechspringboot.vo;

import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.entity.ReimTrip;
import lombok.Data;

import java.util.List;

@Data
public class ReimbursementDetailVO {
    private ReimMain main;
    private List<ReimTrip> trips;
    private List<ReimApportion> apportion;
    private List<ReimSubsidy> subsidies;
    private List<ReimSubsidyDetail> subsidyDetails;
}
