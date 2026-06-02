package com.example.vetechspringboot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.service.ReimApportionService;
import com.example.vetechspringboot.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reim-apportion")
public class ReimApportionController {

    @Autowired
    private ReimApportionService apportionService;

    @PostMapping("/saveBatch")
    public Result<Boolean> saveBatch(@RequestBody List<ReimApportion> apportionList) {
        if (apportionList == null || apportionList.isEmpty()) return Result.success(true);
        String mainId = apportionList.get(0).getMainId();
        // 先删后插
        apportionService.remove(new LambdaQueryWrapper<ReimApportion>().eq(ReimApportion::getMainId, mainId));
        apportionList.forEach(a -> {
            a.setId(UUID.randomUUID().toString().replace("-", ""));
            a.setMainId(mainId);
        });
        return Result.success(apportionService.saveBatch(apportionList));
    }

    @GetMapping("/list/{mainId}")
    public Result<List<ReimApportion>> listByMainId(@PathVariable String mainId) {
        return Result.success(apportionService.list(new LambdaQueryWrapper<ReimApportion>().eq(ReimApportion::getMainId, mainId)));
    }
}
