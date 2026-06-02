package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.vetechspringboot.dto.ReimbursementDetailDTO;
import com.example.vetechspringboot.entity.ReimApportion;
import com.example.vetechspringboot.entity.ReimMain;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.ReimApportionMapper;
import com.example.vetechspringboot.mapper.ReimMainMapper;
import com.example.vetechspringboot.mapper.ReimTripMapper;
import com.example.vetechspringboot.service.ReimApportionService;
import com.example.vetechspringboot.service.ReimSubsidyDetailService;
import com.example.vetechspringboot.service.ReimSubsidyService;
import com.example.vetechspringboot.service.ReimTripService;
import com.example.vetechspringboot.vo.Result;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        trip.setDepartureCityNo("10119");
        trip.setArrivalCityNo("10621");
        trip.setDepartureDate(Date.valueOf("2026-06-01"));
        trip.setArrivalDate(Date.valueOf("2026-06-01"));
        trip.setTripRemark("trip");

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
        trip.setDepartureCityNo("10119");
        trip.setArrivalCityNo("10621");
        trip.setDepartureDate(Date.valueOf("2026-06-01"));
        trip.setArrivalDate(Date.valueOf("2026-06-01"));
        trip.setTripRemark("trip");
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

    @Test
    void saveOrUpdateDetailAllowsDraftWithoutTrips() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        ReimTripService tripService = mock(ReimTripService.class);
        ReimApportionService apportionService = mock(ReimApportionService.class);
        ReimSubsidyService subsidyService = mock(ReimSubsidyService.class);
        ReimSubsidyDetailService subsidyDetailService = mock(ReimSubsidyDetailService.class);

        when(mainMapper.updateById(any(ReimMain.class))).thenReturn(1);
        when(subsidyService.list(any(Wrapper.class))).thenReturn(List.of());

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);
        ReflectionTestUtils.setField(service, "reimTripService", tripService);
        ReflectionTestUtils.setField(service, "reimApportionService", apportionService);
        ReflectionTestUtils.setField(service, "reimSubsidyService", subsidyService);
        ReflectionTestUtils.setField(service, "reimSubsidyDetailService", subsidyDetailService);

        ReimMain main = new ReimMain();
        main.setId("main-1");
        main.setStatus(0);

        ReimbursementDetailDTO dto = new ReimbursementDetailDTO();
        dto.setMain(main);
        dto.setTrips(List.of());

        Result<String> result = service.saveOrUpdateDetail(dto);

        assertEquals("200", result.getCode());
        assertEquals("main-1", result.getData());
        verify(tripService).remove(any());
    }

    @Test
    void saveOrUpdateMainGeneratesNextNumberFromExistingMaxId() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        String todayPrefix = "BX" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        ReimMain latest = new ReimMain();
        latest.setId(todayPrefix + "0003");
        when(mainMapper.selectOne(any())).thenReturn(latest);
        when(mainMapper.insert(any(ReimMain.class))).thenReturn(1);

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);

        ReimMain main = new ReimMain();
        main.setReimbursementTitle("draft");

        Result<String> result = service.saveOrUpdateMain(main);

        assertEquals("200", result.getCode());
        assertEquals(todayPrefix + "0004", result.getData());
    }

    @Test
    void saveOrUpdateMainNormalizesBlankForeignKeysToNull() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        when(mainMapper.selectOne(any())).thenReturn(null);
        when(mainMapper.insert(any(ReimMain.class))).thenReturn(1);

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);

        ReimMain main = new ReimMain();
        main.setReimburserId("");
        main.setReimDepartmentId("");
        main.setReimCompanyId("");
        main.setBusinessTypeId("");

        Result<String> result = service.saveOrUpdateMain(main);

        ArgumentCaptor<ReimMain> captor = ArgumentCaptor.forClass(ReimMain.class);
        verify(mainMapper).insert(captor.capture());
        ReimMain inserted = captor.getValue();
        assertEquals("200", result.getCode());
        org.junit.jupiter.api.Assertions.assertNull(inserted.getReimburserId());
        org.junit.jupiter.api.Assertions.assertNull(inserted.getReimDepartmentId());
        org.junit.jupiter.api.Assertions.assertNull(inserted.getReimCompanyId());
        org.junit.jupiter.api.Assertions.assertNull(inserted.getBusinessTypeId());
    }

    @Test
    void saveOrUpdateDetailRejectsOverlappingTripsForSameTraveler() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        ReimTripService tripService = mock(ReimTripService.class);
        ReimApportionService apportionService = mock(ReimApportionService.class);
        ReimSubsidyService subsidyService = mock(ReimSubsidyService.class);
        ReimSubsidyDetailService subsidyDetailService = mock(ReimSubsidyDetailService.class);

        when(mainMapper.updateById(any(ReimMain.class))).thenReturn(1);

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);
        ReflectionTestUtils.setField(service, "reimTripService", tripService);
        ReflectionTestUtils.setField(service, "reimApportionService", apportionService);
        ReflectionTestUtils.setField(service, "reimSubsidyService", subsidyService);
        ReflectionTestUtils.setField(service, "reimSubsidyDetailService", subsidyDetailService);

        ReimMain main = new ReimMain();
        main.setId("main-1");

        ReimTrip first = new ReimTrip();
        first.setTravelerId("traveler-1");
        first.setDepartureCityNo("10119");
        first.setArrivalCityNo("10621");
        first.setDepartureDate(Date.valueOf("2026-06-01"));
        first.setArrivalDate(Date.valueOf("2026-06-03"));
        first.setTripRemark("first");

        ReimTrip second = new ReimTrip();
        second.setTravelerId("traveler-1");
        second.setDepartureCityNo("10119");
        second.setArrivalCityNo("10621");
        second.setDepartureDate(Date.valueOf("2026-06-02"));
        second.setArrivalDate(Date.valueOf("2026-06-04"));
        second.setTripRemark("second");

        ReimbursementDetailDTO dto = new ReimbursementDetailDTO();
        dto.setMain(main);
        dto.setTrips(List.of(first, second));

        Result<String> result = service.saveOrUpdateDetail(dto);

        assertEquals("500", result.getCode());
        assertEquals("trip_date_overlap", result.getData());
    }

    @Test
    void submitReimbursementUsesCompletedStatusOne() {
        ReimMainMapper mainMapper = mock(ReimMainMapper.class);
        ReimTripMapper tripMapper = mock(ReimTripMapper.class);
        ReimApportionMapper apportionMapper = mock(ReimApportionMapper.class);

        ReimMain main = new ReimMain();
        main.setId("main-1");
        main.setStatus(0);
        main.setTotalAmount(BigDecimal.TEN);
        when(mainMapper.selectById("main-1")).thenReturn(main);
        when(tripMapper.selectCount(any())).thenReturn(1L);

        ReimApportion apportion = new ReimApportion();
        apportion.setApportionAmount(BigDecimal.TEN);
        when(apportionMapper.selectList(any())).thenReturn(List.of(apportion));

        ReimMainServiceImpl service = new ReimMainServiceImpl();
        ReflectionTestUtils.setField(service, "reimMainMapper", mainMapper);
        ReflectionTestUtils.setField(service, "tripMapper", tripMapper);
        ReflectionTestUtils.setField(service, "apportionMapper", apportionMapper);

        Result<Boolean> result = service.submitReimbursement("main-1");

        assertEquals("200", result.getCode());
        assertEquals(1, main.getStatus());
    }
}
