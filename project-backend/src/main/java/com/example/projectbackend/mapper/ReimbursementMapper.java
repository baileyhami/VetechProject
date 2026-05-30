package com.example.projectbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.projectbackend.dto.*;
import com.example.projectbackend.entity.ReimMain;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ReimbursementMapper extends BaseMapper<ReimMain> {

    List<ReimbursementListItemDto> selectListItems(@Param("id") String id,
                                                  @Param("title") String title,
                                                  @Param("reason") String reason,
                                                  @Param("companyId") String companyId,
                                                  @Param("departmentId") String departmentId,
                                                  @Param("reimburserId") String reimburserId,
                                                  @Param("businessTypeId") String businessTypeId);

    MainDto selectMainById(@Param("id") String id);

    List<TripDto> selectTrips(@Param("mainId") String mainId);

    List<SubsidyDto> selectSubsidies(@Param("mainId") String mainId);

    List<SubsidyDetailDto> selectSubsidyDetails(@Param("mainId") String mainId);

    List<ApportionDto> selectApportion(@Param("mainId") String mainId);

    int insertMain(MainDto main);

    int updateMain(MainDto main);

    int insertTrip(TripDto trip);

    int insertSubsidy(SubsidyDto subsidy);

    int insertSubsidyDetail(SubsidyDetailDto detail);

    int insertApportion(ApportionDto apportion);

    int deleteSubsidyDetailsByMainId(@Param("mainId") String mainId);

    int deleteSubsidiesByMainId(@Param("mainId") String mainId);

    int deleteTripsByMainId(@Param("mainId") String mainId);

    int deleteApportionByMainId(@Param("mainId") String mainId);
}

