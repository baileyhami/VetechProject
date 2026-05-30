package com.example.projectbackend.mapper;

import com.example.projectbackend.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Deprecated
public class LegacyReimbursementJdbcMapper {

    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;

    public LegacyReimbursementJdbcMapper(NamedParameterJdbcTemplate namedJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ReimbursementListItemDto> findList(String id, String title, String reason, String companyId,
                                                   String departmentId, String reimburserId, String businessTypeId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.id, m.status, m.reimbursement_title, m.business_trip_reason, m.subsidy_total, m.creation_time,")
                .append(" e.reimburser_name, e.reimburser_no, d.reim_department_name, d.reim_department_no,")
                .append(" c.reim_company_name, b.business_type_name")
                .append(" FROM fk_reim_main m")
                .append(" LEFT JOIN fk_reim_employee e ON m.reimburser_id = e.reimburser_id")
                .append(" LEFT JOIN fk_reim_department d ON m.reim_department_id = d.reim_department_id")
                .append(" LEFT JOIN fk_reim_company c ON m.reim_company_id = c.reim_company_id")
                .append(" LEFT JOIN fk_business_type b ON m.business_type_id = b.business_type_id")
                .append(" WHERE 1=1");

        MapSqlParameterSource params = new MapSqlParameterSource();
        if (id != null && !id.isBlank()) {
            sql.append(" AND m.id LIKE :id");
            params.addValue("id", "%" + id + "%");
        }
        if (title != null && !title.isBlank()) {
            sql.append(" AND m.reimbursement_title LIKE :title");
            params.addValue("title", "%" + title + "%");
        }
        if (reason != null && !reason.isBlank()) {
            sql.append(" AND m.business_trip_reason LIKE :reason");
            params.addValue("reason", "%" + reason + "%");
        }
        if (companyId != null && !companyId.isBlank()) {
            sql.append(" AND m.reim_company_id = :companyId");
            params.addValue("companyId", companyId);
        }
        if (departmentId != null && !departmentId.isBlank()) {
            sql.append(" AND m.reim_department_id = :departmentId");
            params.addValue("departmentId", departmentId);
        }
        if (reimburserId != null && !reimburserId.isBlank()) {
            sql.append(" AND m.reimburser_id = :reimburserId");
            params.addValue("reimburserId", reimburserId);
        }
        if (businessTypeId != null && !businessTypeId.isBlank()) {
            sql.append(" AND m.business_type_id = :businessTypeId");
            params.addValue("businessTypeId", businessTypeId);
        }
        sql.append(" ORDER BY m.creation_time DESC");

        return namedJdbcTemplate.query(sql.toString(), params, (rs, rowNum) -> mapListItem(rs));
    }

    public Optional<MainDto> findMainById(String id) {
        String sql = "SELECT id, creation_time, reimbursement_title, reimburser_id, reim_department_id, reim_company_id, "
                + "business_type_id, business_trip_reason, subsidy_total, meal_allowance, transportation_allowance, phone_allowance, remarks, status "
                + "FROM fk_reim_main WHERE id = ?";
        List<MainDto> list = jdbcTemplate.query(sql, (rs, rowNum) -> mapMain(rs), id);
        return list.stream().findFirst();
    }

    public List<TripDto> findTrips(String mainId) {
        String sql = "SELECT id, main_id, traveler_id, departure_city_no, arrival_city_no, departure_date, arrival_date, trip_remark, create_time "
                + "FROM fk_reim_trip WHERE main_id = ? ORDER BY create_time";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapTrip(rs), mainId);
    }

    public List<SubsidyDto> findSubsidies(String mainId) {
        String sql = "SELECT id, main_id, trip_id, traveler_id, travel_date_range, subsidy_days, trip_route, subsidy_city_no, "
                + "apply_amount, subsidy_amount, meal_allowance, transportation_allowance, phone_allowance "
                + "FROM fk_reim_subsidy WHERE main_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapSubsidy(rs), mainId);
    }

    public List<SubsidyDetailDto> findSubsidyDetails(String mainId) {
        String sql = "SELECT d.id, d.subsidy_id, d.detail_date, d.week_day, d.city_no, d.meal_std, d.meal_amount, "
                + "d.transport_std, d.transport_amount, d.phone_std, d.phone_amount, d.is_selected "
                + "FROM fk_reim_subsidy_detail d JOIN fk_reim_subsidy s ON d.subsidy_id = s.id "
                + "WHERE s.main_id = ? ORDER BY d.detail_date";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapSubsidyDetail(rs), mainId);
    }

    public List<ApportionDto> findApportion(String mainId) {
        String sql = "SELECT id, main_id, reim_company_id, project_id, apportion_ratio, apportion_amount, sort_no "
                + "FROM fk_reim_apportion WHERE main_id = ? ORDER BY sort_no";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapApportion(rs), mainId);
    }

    public void insertMain(MainDto main) {
        String sql = "INSERT INTO fk_reim_main (id, creation_time, reimbursement_title, reimburser_id, reim_department_id, reim_company_id, "
                + "business_type_id, business_trip_reason, subsidy_total, meal_allowance, transportation_allowance, phone_allowance, remarks, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, main.getId(), main.getCreationTime(), main.getReimbursementTitle(), main.getReimburserId(),
                main.getReimDepartmentId(), main.getReimCompanyId(), main.getBusinessTypeId(), main.getBusinessTripReason(),
                main.getSubsidyTotal(), main.getMealAllowance(), main.getTransportationAllowance(), main.getPhoneAllowance(),
                main.getRemarks(), main.getStatus());
    }

    public void updateMain(MainDto main) {
        String sql = "UPDATE fk_reim_main SET reimbursement_title = ?, reimburser_id = ?, reim_department_id = ?, reim_company_id = ?, "
                + "business_type_id = ?, business_trip_reason = ?, subsidy_total = ?, meal_allowance = ?, transportation_allowance = ?, "
                + "phone_allowance = ?, remarks = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql, main.getReimbursementTitle(), main.getReimburserId(), main.getReimDepartmentId(), main.getReimCompanyId(),
                main.getBusinessTypeId(), main.getBusinessTripReason(), main.getSubsidyTotal(), main.getMealAllowance(),
                main.getTransportationAllowance(), main.getPhoneAllowance(), main.getRemarks(), main.getStatus(), main.getId());
    }

    public void insertTrip(TripDto trip) {
        String sql = "INSERT INTO fk_reim_trip (id, main_id, traveler_id, departure_city_no, arrival_city_no, departure_date, arrival_date, trip_remark, create_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, trip.getId(), trip.getMainId(), trip.getTravelerId(), trip.getDepartureCityNo(),
                trip.getArrivalCityNo(), trip.getDepartureDate(), trip.getArrivalDate(), trip.getTripRemark(), trip.getCreateTime());
    }

    public void insertSubsidy(SubsidyDto subsidy) {
        String sql = "INSERT INTO fk_reim_subsidy (id, main_id, trip_id, traveler_id, travel_date_range, subsidy_days, trip_route, subsidy_city_no, "
                + "apply_amount, subsidy_amount, meal_allowance, transportation_allowance, phone_allowance) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, subsidy.getId(), subsidy.getMainId(), subsidy.getTripId(), subsidy.getTravelerId(),
                subsidy.getTravelDateRange(), subsidy.getSubsidyDays(), subsidy.getTripRoute(), subsidy.getSubsidyCityNo(),
                subsidy.getApplyAmount(), subsidy.getSubsidyAmount(), subsidy.getMealAllowance(),
                subsidy.getTransportationAllowance(), subsidy.getPhoneAllowance());
    }

    public void insertSubsidyDetail(SubsidyDetailDto detail) {
        String sql = "INSERT INTO fk_reim_subsidy_detail (id, subsidy_id, detail_date, week_day, city_no, meal_std, meal_amount, transport_std, "
                + "transport_amount, phone_std, phone_amount, is_selected) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, detail.getId(), detail.getSubsidyId(), detail.getDetailDate(), detail.getWeekDay(), detail.getCityNo(),
                detail.getMealStd(), detail.getMealAmount(), detail.getTransportStd(), detail.getTransportAmount(),
                detail.getPhoneStd(), detail.getPhoneAmount(), detail.isSelected() ? 1 : 0);
    }

    public void insertApportion(ApportionDto apportion) {
        String sql = "INSERT INTO fk_reim_apportion (id, main_id, reim_company_id, project_id, apportion_ratio, apportion_amount, sort_no) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, apportion.getId(), apportion.getMainId(), apportion.getReimCompanyId(), apportion.getProjectId(),
                apportion.getApportionRatio(), apportion.getApportionAmount(), apportion.getSortNo());
    }

    public void deleteByMainId(String mainId) {
        jdbcTemplate.update("DELETE FROM fk_reim_subsidy_detail WHERE subsidy_id IN (SELECT id FROM fk_reim_subsidy WHERE main_id = ?)", mainId);
        jdbcTemplate.update("DELETE FROM fk_reim_subsidy WHERE main_id = ?", mainId);
        jdbcTemplate.update("DELETE FROM fk_reim_trip WHERE main_id = ?", mainId);
        jdbcTemplate.update("DELETE FROM fk_reim_apportion WHERE main_id = ?", mainId);
    }

    private ReimbursementListItemDto mapListItem(ResultSet rs) throws SQLException {
        ReimbursementListItemDto dto = new ReimbursementListItemDto();
        dto.setId(rs.getString("id"));
        dto.setStatus(rs.getInt("status"));
        dto.setTitle(rs.getString("reimbursement_title"));
        dto.setReason(rs.getString("business_trip_reason"));
        dto.setSubsidyTotal(rs.getBigDecimal("subsidy_total"));
        dto.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
        dto.setReimburserName(rs.getString("reimburser_name"));
        dto.setReimburserNo(rs.getString("reimburser_no"));
        dto.setDepartmentName(rs.getString("reim_department_name"));
        dto.setDepartmentNo(rs.getString("reim_department_no"));
        dto.setCompanyName(rs.getString("reim_company_name"));
        dto.setBusinessTypeName(rs.getString("business_type_name"));
        return dto;
    }

    private MainDto mapMain(ResultSet rs) throws SQLException {
        MainDto dto = new MainDto();
        dto.setId(rs.getString("id"));
        dto.setCreationTime(rs.getObject("creation_time", LocalDateTime.class));
        dto.setReimbursementTitle(rs.getString("reimbursement_title"));
        dto.setReimburserId(rs.getString("reimburser_id"));
        dto.setReimDepartmentId(rs.getString("reim_department_id"));
        dto.setReimCompanyId(rs.getString("reim_company_id"));
        dto.setBusinessTypeId(rs.getString("business_type_id"));
        dto.setBusinessTripReason(rs.getString("business_trip_reason"));
        dto.setSubsidyTotal(rs.getBigDecimal("subsidy_total"));
        dto.setMealAllowance(rs.getBigDecimal("meal_allowance"));
        dto.setTransportationAllowance(rs.getBigDecimal("transportation_allowance"));
        dto.setPhoneAllowance(rs.getBigDecimal("phone_allowance"));
        dto.setRemarks(rs.getString("remarks"));
        dto.setStatus(rs.getInt("status"));
        return dto;
    }

    private TripDto mapTrip(ResultSet rs) throws SQLException {
        TripDto dto = new TripDto();
        dto.setId(rs.getString("id"));
        dto.setMainId(rs.getString("main_id"));
        dto.setTravelerId(rs.getString("traveler_id"));
        dto.setDepartureCityNo(rs.getString("departure_city_no"));
        dto.setArrivalCityNo(rs.getString("arrival_city_no"));
        dto.setDepartureDate(rs.getObject("departure_date", LocalDate.class));
        dto.setArrivalDate(rs.getObject("arrival_date", LocalDate.class));
        dto.setTripRemark(rs.getString("trip_remark"));
        dto.setCreateTime(rs.getObject("create_time", LocalDateTime.class));
        return dto;
    }

    private SubsidyDto mapSubsidy(ResultSet rs) throws SQLException {
        SubsidyDto dto = new SubsidyDto();
        dto.setId(rs.getString("id"));
        dto.setMainId(rs.getString("main_id"));
        dto.setTripId(rs.getString("trip_id"));
        dto.setTravelerId(rs.getString("traveler_id"));
        dto.setTravelDateRange(rs.getString("travel_date_range"));
        dto.setSubsidyDays(rs.getInt("subsidy_days"));
        dto.setTripRoute(rs.getString("trip_route"));
        dto.setSubsidyCityNo(rs.getString("subsidy_city_no"));
        dto.setApplyAmount(rs.getBigDecimal("apply_amount"));
        dto.setSubsidyAmount(rs.getBigDecimal("subsidy_amount"));
        dto.setMealAllowance(rs.getBigDecimal("meal_allowance"));
        dto.setTransportationAllowance(rs.getBigDecimal("transportation_allowance"));
        dto.setPhoneAllowance(rs.getBigDecimal("phone_allowance"));
        return dto;
    }

    private SubsidyDetailDto mapSubsidyDetail(ResultSet rs) throws SQLException {
        SubsidyDetailDto dto = new SubsidyDetailDto();
        dto.setId(rs.getString("id"));
        dto.setSubsidyId(rs.getString("subsidy_id"));
        dto.setDetailDate(rs.getObject("detail_date", LocalDate.class));
        dto.setWeekDay(rs.getString("week_day"));
        dto.setCityNo(rs.getString("city_no"));
        dto.setMealStd(rs.getBigDecimal("meal_std"));
        dto.setMealAmount(rs.getBigDecimal("meal_amount"));
        dto.setTransportStd(rs.getBigDecimal("transport_std"));
        dto.setTransportAmount(rs.getBigDecimal("transport_amount"));
        dto.setPhoneStd(rs.getBigDecimal("phone_std"));
        dto.setPhoneAmount(rs.getBigDecimal("phone_amount"));
        dto.setSelected(rs.getInt("is_selected") == 1);
        return dto;
    }

    private ApportionDto mapApportion(ResultSet rs) throws SQLException {
        ApportionDto dto = new ApportionDto();
        dto.setId(rs.getString("id"));
        dto.setMainId(rs.getString("main_id"));
        dto.setReimCompanyId(rs.getString("reim_company_id"));
        dto.setProjectId(rs.getString("project_id"));
        dto.setApportionRatio(rs.getBigDecimal("apportion_ratio"));
        dto.setApportionAmount(rs.getBigDecimal("apportion_amount"));
        dto.setSortNo(rs.getInt("sort_no"));
        return dto;
    }
}


