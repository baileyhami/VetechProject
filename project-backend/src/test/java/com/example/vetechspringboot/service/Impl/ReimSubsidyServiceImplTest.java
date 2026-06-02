package com.example.vetechspringboot.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.example.vetechspringboot.entity.ReimCity;
import com.example.vetechspringboot.entity.ReimSubsidy;
import com.example.vetechspringboot.entity.ReimSubsidyDetail;
import com.example.vetechspringboot.entity.ReimTrip;
import com.example.vetechspringboot.mapper.ReimCityMapper;
import com.example.vetechspringboot.mapper.ReimTripMapper;
import com.example.vetechspringboot.service.ReimSubsidyDetailService;
import com.example.vetechspringboot.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReimSubsidyServiceImplTest {

    @Test
    void calculateSubsidyUsesDocumentTransportAndPhoneStandards() {
        ReimTripMapper tripMapper = mock(ReimTripMapper.class);
        ReimSubsidyDetailService detailService = mock(ReimSubsidyDetailService.class);
        ReimCityMapper cityMapper = mock(ReimCityMapper.class);

        ReimTrip trip = new ReimTrip();
        trip.setId("trip-1");
        trip.setMainId("main-1");
        trip.setTravelerId("traveler-1");
        trip.setDepartureCityNo("10119");
        trip.setArrivalCityNo("10621");
        trip.setDepartureDate(Date.valueOf("2026-06-01"));
        trip.setArrivalDate(Date.valueOf("2026-06-01"));
        when(tripMapper.selectList(any())).thenReturn(List.of(trip));

        ReimCity city = new ReimCity();
        city.setCityNo("10621");
        city.setCityType("1");
        when(cityMapper.selectById("10621")).thenReturn(city);

        ReimSubsidyServiceImpl service = spy(new ReimSubsidyServiceImpl());
        doReturn(true).when(service).saveBatch(any());
        doReturn(List.<ReimSubsidy>of()).when(service).list(any(Wrapper.class));
        when(detailService.saveBatch(any())).thenReturn(true);
        ReflectionTestUtils.setField(service, "reimTripMapper", tripMapper);
        ReflectionTestUtils.setField(service, "reimSubsidyDetailService", detailService);
        ReflectionTestUtils.setField(service, "reimCityMapper", cityMapper);

        Result<List<ReimSubsidyDetail>> result = service.calculateSubsidy("main-1");

        assertEquals("200", result.getCode());
        ReimSubsidyDetail detail = result.getData().get(0);
        assertEquals(new BigDecimal("40"), detail.getTransportStd());
        assertEquals(new BigDecimal("40"), detail.getTransportAmount());
        assertEquals(new BigDecimal("40"), detail.getPhoneStd());
        assertEquals(new BigDecimal("40"), detail.getPhoneAmount());
        verify(detailService).saveBatch(any());
    }
}
