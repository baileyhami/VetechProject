package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.vetechspringboot.dto.ReimbursementDetailDTO;
import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.ReimMainMapper;
import com.example.vetechspringboot.service.ReimApportionService;
import com.example.vetechspringboot.service.ReimSubsidyDetailService;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.service.ReimTripService;
import com.example.vetechspringboot.vo.Result;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReimMainServiceImplTest {

    @Test
    void saveOrUpdateDetailRemovesExistingSubsidiesBeforeReplacingTrips() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        ReimTripService tripService = mock(ReimTripService.class);
        ReimApportionService apportionService = mock(ReimApportionService.class);
        ReimSubsidyService subsidyService = mock(ReimSubsidyService.class);
        ReimSubsidyDetailService subsidyDetailService = mock(ReimSubsidyDetailService.class);

        when(mainMapper.updateById(any(ReimMain.class))).thenReturn(1);
        when(tripService.saveOrReplaceTrips(any())).thenReturn(true);
        when(subsidyService.calculateSubsidy("main-1")).thenReturn(Result.success(List.of()));

        ReimSubsidy existingSubsidy = new ReimSubsidy();
        existingSubsidy.setId("subsidy-1");
        when(subsidyService.list(any(Wrapper.class))).thenReturn(List.of(existingSubsidy));

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);
        ReflectionTestUtils.setField(service, "reimTripService", tripService);
        ReflectionTestUtils.setField(service, "reimApportionService", apportionService);
        ReflectionTestUtils.setField(service, "reimSubsidyService", subsidyService);
        ReflectionTestUtils.setField(service, "reimSubsidyDetailService", subsidyDetailService);

        ReimMain main = new ReimMain();
        main.setId("main-1");
        ReimTrip trip = new ReimTrip();
        trip.setTravelerId("traveler-1");

        ReimbursementDetailDTO dto = new ReimbursementDetailDTO();
        dto.setMain(main);
        dto.setTrips(List.of(trip));

        service.saveOrUpdateDetail(dto);

        InOrder order = inOrder(subsidyDetailService, subsidyService, tripService);
        order.verify(subsidyDetailService).remove(any());
        order.verify(subsidyService).remove(any());
        order.verify(tripService).saveOrReplaceTrips(any());
    }

    @Test
    void saveOrUpdateDetailNormalizesBlankApportionProjectIdToNull() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        ReimTripService tripService = mock(ReimTripService.class);
        ReimApportionService apportionService = mock(ReimApportionService.class);
        ReimSubsidyService subsidyService = mock(ReimSubsidyService.class);
        ReimSubsidyDetailService subsidyDetailService = mock(ReimSubsidyDetailService.class);

        when(mainMapper.updateById(any(ReimMain.class))).thenReturn(1);
        when(tripService.saveOrReplaceTrips(any())).thenReturn(true);
        when(subsidyService.calculateSubsidy("main-1")).thenReturn(Result.success(List.of()));
        when(subsidyService.list(any(Wrapper.class))).thenReturn(List.of());

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);
        ReflectionTestUtils.setField(service, "reimTripService", tripService);
        ReflectionTestUtils.setField(service, "reimApportionService", apportionService);
        ReflectionTestUtils.setField(service, "reimSubsidyService", subsidyService);
        ReflectionTestUtils.setField(service, "reimSubsidyDetailService", subsidyDetailService);

        ReimMain main = new ReimMain();
        main.setId("main-1");
        ReimTrip trip = new ReimTrip();
        trip.setTravelerId("traveler-1");
        ReimApportion apportion = new ReimApportion();
        apportion.setProjectId("");

        ReimbursementDetailDTO dto = new ReimbursementDetailDTO();
        dto.setMain(main);
        dto.setTrips(List.of(trip));
        dto.setApportion(List.of(apportion));

        service.saveOrUpdateDetail(dto);

        verify(apportionService).saveBatch(List.of(apportion));
        org.junit.jupiter.api.Assertions.assertNull(apportion.getProjectId());
    }
}
