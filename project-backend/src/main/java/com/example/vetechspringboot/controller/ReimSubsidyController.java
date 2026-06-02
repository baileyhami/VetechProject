package com.example.vetechspringboot.controller;

import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for subsidy calculation operations.
 */
@RestController
@RequestMapping("/api/reim-subsidy")
public class ReimSubsidyController {

    @Autowired
    private ReimSubsidyService reimSubsidyService;

    @PostMapping("/calculate/{mainId}")
    public Result<List<ReimSubsidyDetail>> calculate(@PathVariable("mainId") String mainId) {
        return reimSubsidyService.calculateSubsidy(mainId);
    }
}

