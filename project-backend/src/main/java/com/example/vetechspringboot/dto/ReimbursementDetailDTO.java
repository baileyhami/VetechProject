package com.example.vetechspringboot.dto;

import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimTrip;
import lombok.Data;

import java.util.List;

@Data
public class ReimbursementDetailDTO {
    private ReimMain main;
    private List<ReimTrip> trips;
    private List<ReimApportion> apportion;
}
