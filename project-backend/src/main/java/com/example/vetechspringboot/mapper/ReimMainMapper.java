package com.example.vetechspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.vetechspringboot.dto.ReimMainQueryDTO;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.vo.ReimMainListVO;
import org.apache.ibatis.annotations.Param;

public interface ReimMainMapper extends BaseMapper<ReimMain> {
    IPage<ReimMainListVO> selectReimMainList(Page<?> page, @Param("query") ReimMainQueryDTO query);
}

