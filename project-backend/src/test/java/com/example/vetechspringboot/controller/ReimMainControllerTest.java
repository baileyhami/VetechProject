package com.example.vetechspringboot.controller;

import com.example.vetechspringboot.service.ReimMainService;
import com.example.vetechspringboot.vo.ReimCityOptionVO;
import com.example.vetechspringboot.vo.ReimProjectOptionVO;
import com.example.vetechspringboot.vo.Result;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReimMainControllerTest {

    @Test
    void getAllLookupsIncludesCitiesAndProjects() {
        ReimMainService reimMainService = mock(ReimMainService.class);
        when(reimMainService.listCompanyOptions()).thenReturn(List.of());
        when(reimMainService.listDepartmentOptions()).thenReturn(List.of());
        when(reimMainService.listEmployeeOptions()).thenReturn(List.of());
        when(reimMainService.listBusinessTypeOptions()).thenReturn(List.of());

        ReimCityOptionVO city = new ReimCityOptionVO();
        city.setCityNo("10119");
        city.setCityName("Beijing");
        city.setCityType("1");

        ReimProjectOptionVO project = new ReimProjectOptionVO();
        project.setProjectId("12BC248B25083001");
        project.setProjectNo("nonProjectRelated");
        project.setProjectName("Non Project");

        LookupController lookupController = mock(LookupController.class);
        when(lookupController.listCities()).thenReturn(Result.success(List.of(city)));
        when(lookupController.listProjects()).thenReturn(Result.success(List.of(project)));

        ReimMainController controller = new ReimMainController();
        ReflectionTestUtils.setField(controller, "reimMainService", reimMainService);
        ReflectionTestUtils.setField(controller, "lookupController", lookupController);

        Result<Map<String, Object>> result = controller.getAllLookups();

        assertEquals("200", result.getCode());
        assertTrue(result.getData().containsKey("cities"));
        assertTrue(result.getData().containsKey("projects"));
    }
}
