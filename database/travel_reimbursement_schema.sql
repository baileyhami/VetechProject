-- ========================================================
-- 差旅报销单数据库结构及初始化数据 SQL
-- 基于表结构定义模板解耦重构，按5.3基础数据拆分为独立表
-- 表数量：11张（6张基础数据表 + 5张业务表）
-- ========================================================

-- --------------------------------------------------------
-- 1. 费用归属公司控件数据表 fk_reim_company
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_company`;
CREATE TABLE `fk_reim_company` (
  `reim_company_id`   varchar(32)  NOT NULL COMMENT '费用归属公司ID',
  `reim_company_no`   varchar(20)  NOT NULL COMMENT '费用归属公司编号',
  `reim_company_name` varchar(50)  NOT NULL COMMENT '费用归属公司名称',
  PRIMARY KEY (`reim_company_id`),
  UNIQUE KEY `uk_company_no` (`reim_company_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用归属公司控件数据表';

INSERT INTO `fk_reim_company` VALUES
('1C54557F1782E000','0407','胜意科技北京分公司'),
('19218A262C976000','0408','胜意科技上海分公司'),
('1C61686865DA8000','0409','胜意科技武汉分公司'),
('1717271D1DA15000','0410','胜意科技杭州分公司'),
('16AE93CC7EF92002','0411','胜意科技荆州分公司');

-- --------------------------------------------------------
-- 2. 报销部门控件数据表 fk_reim_department
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_department`;
CREATE TABLE `fk_reim_department` (
  `reim_department_id`   varchar(32)  NOT NULL COMMENT '报销部门ID',
  `reim_department_no`   varchar(20)  NOT NULL COMMENT '报销部门编号',
  `reim_department_name` varchar(50)  NOT NULL COMMENT '报销部门名称',
  PRIMARY KEY (`reim_department_id`),
  UNIQUE KEY `uk_department_no` (`reim_department_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销部门控件数据表';

INSERT INTO `fk_reim_department` VALUES
('13AB8D7B52A9B002','072001','客户成功事业部'),
('13BFD31C6029A002','072002','企业消费事业部'),
('14515BB4BFB92003','072003','企业费控事业部'),
('19206611C47A6000','072004','集采事业部'),
('19D32F9FE9647000','072005','航旅事业部'),
('13C7E2BAE0393001','072006','运营事业部'),
('14055D22BB808001','072007','营销事业部');

-- --------------------------------------------------------
-- 3. 员工控件数据表 fk_reim_employee
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_employee`;
CREATE TABLE `fk_reim_employee` (
  `reimburser_id`   varchar(32)  NOT NULL COMMENT '报销人ID',
  `reimburser_no`   varchar(20)  NOT NULL COMMENT '报销人工号',
  `reimburser_name` varchar(20)  NOT NULL COMMENT '报销人姓名',
  PRIMARY KEY (`reimburser_id`),
  UNIQUE KEY `uk_employee_no` (`reimburser_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工控件数据表';

INSERT INTO `fk_reim_employee` VALUES
('13AB3A3F72409002','74541','徐年年'),
('13AB498CC6409002','74008','郑雨雪'),
('13AB4A56BB009002','21552','邹薇'),
('13AB591FE8009002','80681','王成军'),
('13AB77281A408001','89899','潘展飞'),
('13AB7925EB808001','10503','姜林');

-- --------------------------------------------------------
-- 4. 业务类型控件数据表 fk_business_type（树形结构）
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_business_type`;
CREATE TABLE `fk_business_type` (
  `business_type_id`       varchar(32)  NOT NULL COMMENT '业务类型ID',
  `business_type_no`       varchar(20)  NOT NULL COMMENT '业务类型编号',
  `business_type_name`     varchar(50)  NOT NULL COMMENT '业务类型名称',
  `there_subordinate_node` char(1)      NOT NULL DEFAULT '0' COMMENT '是否有下级节点：0否 1是',
  `superior_id`            varchar(32)  DEFAULT NULL COMMENT '上级业务类型ID，none表示顶级',
  PRIMARY KEY (`business_type_id`),
  UNIQUE KEY `uk_business_type_no` (`business_type_no`),
  KEY `idx_superior_id` (`superior_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务类型控件数据表';

INSERT INTO `fk_business_type` VALUES
('18F0916A8C2C4000','1001001','员工差旅活动','1','none'),
('18F091913EEC4000','100100101','境内出差','1','18F0916A8C2C4000'),
('1B5FEB7DD4396000','10010010101','项目出差','0','18F091913EEC4000'),
('1A92E43082EFC000','10010010102','市场拓展出差','0','18F091913EEC4000'),
('13AB3A4138008001','100100102','境外出差','1','18F0916A8C2C4000'),
('13AB3A4248008002','10010010201','国外考察','0','13AB3A4138008001'),
('13AB3A4154008001','10010010202','售后维护出差','0','13AB3A4138008001'),
('13AB3A4172008001','1001002','人力资源','1','none'),
('13AB3A418F808001','100100201','个人团队培训','0','13AB3A4172008001'),
('13AB3A41AC408001','100100202','招聘会','0','13AB3A4172008001'),
('13AB3A41CD808002','1001003','员工福利','1','none'),
('13AB3A41ED408002','100100301','员工旅游','0','13AB3A41CD808002'),
('13AB3A420CC08002','100100302','员工团建','0','13AB3A41CD808002'),
('13AB3A422A808001','100100303','员工体检','0','13AB3A41CD808002');

-- --------------------------------------------------------
-- 5. 城市控件数据表 fk_city
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_city`;
CREATE TABLE `fk_city` (
  `city_no`   varchar(10)  NOT NULL COMMENT '城市编号',
  `city_name` varchar(20)  NOT NULL COMMENT '城市名称',
  `city_type` char(1)      NOT NULL COMMENT '城市类型：1一线 2二线 3三线',
  PRIMARY KEY (`city_no`),
  UNIQUE KEY `uk_city_name` (`city_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='城市控件数据表';

INSERT INTO `fk_city` VALUES
('10119','北京','1'),
('10621','上海','1'),
('10458','武汉','2'),
('10216','杭州','2'),
('10455','荆州','3');

-- --------------------------------------------------------
-- 6. 项目控件数据表 fk_project
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_project`;
CREATE TABLE `fk_project` (
  `project_id`   varchar(32)  NOT NULL COMMENT '项目ID',
  `project_no`   varchar(30)  NOT NULL COMMENT '项目编号',
  `project_name` varchar(50)  NOT NULL COMMENT '项目名称',
  PRIMARY KEY (`project_id`),
  UNIQUE KEY `uk_project_no` (`project_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目控件数据表';

INSERT INTO `fk_project` VALUES
('12BC248B25083001','nonProjectRelated','非项目类费用归集'),
('1C811ABF96195000','centralChina','华中客户定制化项目'),
('1C5931735AC4A000','southChina','华南客户定制化项目'),
('1771EC45F2443000','northChina','华北客户定制化项目'),
('1762792DB4E9A002','eastChina','华东客户定制化项目'),
('17071065FC29A002','southWest','西南客户定制化项目'),
('162664EBE9ABE001','northWest','西北客户定制化项目'),
('162664B8526BE002','northEast','东北客户定制化项目');

-- --------------------------------------------------------
-- 7. 报销单主表 fk_reim_main（重构后，去除冗余字段，改为外键关联）
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_main`;
CREATE TABLE `fk_reim_main` (
  `id`                      varchar(32)      NOT NULL COMMENT '主键ID',
  `creation_time`           datetime         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `reimbursement_title`     varchar(500)     DEFAULT NULL COMMENT '报销标题',
  `reimburser_id`           varchar(32)      DEFAULT NULL COMMENT '报销人ID（外键：fk_reim_employee）',
  `reim_department_id`      varchar(32)      DEFAULT NULL COMMENT '报销部门ID（外键：fk_reim_department）',
  `reim_company_id`         varchar(32)      DEFAULT NULL COMMENT '费用归属公司ID（外键：fk_reim_company）',
  `business_type_id`        varchar(32)      DEFAULT NULL COMMENT '业务类型ID（外键：fk_business_type）',
  `business_trip_reason`  varchar(500)     DEFAULT NULL COMMENT '出差事由',
  `subsidy_total`           decimal(18,2)    DEFAULT '0.00' COMMENT '补助总金额',
  `meal_allowance`          decimal(18,2)    DEFAULT '0.00' COMMENT '餐费补助合计',
  `transportation_allowance` decimal(18,2)   DEFAULT '0.00' COMMENT '交通补助合计',
  `phone_allowance`         decimal(18,2)    DEFAULT '0.00' COMMENT '通讯补助合计',
  `remarks`                 varchar(1000)    DEFAULT NULL COMMENT '备注信息',
  `status`                  tinyint(1)       NOT NULL DEFAULT '0' COMMENT '单据状态：0草稿 1已完成 2已作废',
  PRIMARY KEY (`id`),
  KEY `idx_reimburser_id` (`reimburser_id`),
  KEY `idx_department_id` (`reim_department_id`),
  KEY `idx_company_id` (`reim_company_id`),
  KEY `idx_business_type_id` (`business_type_id`),
  KEY `idx_status` (`status`),
  KEY `idx_creation_time` (`creation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报销单主表（解耦重构版）';

-- --------------------------------------------------------
-- 8. 补录行程表 fk_reim_trip
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_trip`;
CREATE TABLE `fk_reim_trip` (
  `id`               varchar(32)  NOT NULL COMMENT '主键ID',
  `main_id`          varchar(32)  NOT NULL COMMENT '报销单主表ID（外键）',
  `traveler_id`      varchar(32)  NOT NULL COMMENT '出行人ID（外键：fk_reim_employee）',
  `departure_city_no` varchar(10) NOT NULL COMMENT '出发城市编号（外键：fk_city）',
  `arrival_city_no`  varchar(10)  NOT NULL COMMENT '到达城市编号（外键：fk_city）',
  `departure_date`   date         NOT NULL COMMENT '出发日期',
  `arrival_date`     date         NOT NULL COMMENT '到达日期',
  `trip_remark`      varchar(500) DEFAULT NULL COMMENT '行程说明',
  `create_time`      datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_main_id` (`main_id`),
  KEY `idx_traveler_id` (`traveler_id`),
  KEY `idx_departure_city` (`departure_city_no`),
  KEY `idx_arrival_city` (`arrival_city_no`),
  KEY `idx_date_range` (`departure_date`,`arrival_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补录行程表';

-- --------------------------------------------------------
-- 9. 补助信息表 fk_reim_subsidy（按行程汇总）
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_subsidy`;
CREATE TABLE `fk_reim_subsidy` (
  `id`                      varchar(32)   NOT NULL COMMENT '主键ID',
  `main_id`                 varchar(32)   NOT NULL COMMENT '报销单主表ID（外键）',
  `trip_id`                 varchar(32)   NOT NULL COMMENT '补录行程ID（外键：fk_reim_trip）',
  `traveler_id`             varchar(32)   NOT NULL COMMENT '出行人ID（外键：fk_reim_employee）',
  `travel_date_range`       varchar(50)   DEFAULT NULL COMMENT '出差日期范围',
  `subsidy_days`            int(11)       DEFAULT '0' COMMENT '补助天数',
  `trip_route`              varchar(50)   DEFAULT NULL COMMENT '行程（出发城市-到达城市）',
  `subsidy_city_no`         varchar(10)   DEFAULT NULL COMMENT '补助城市编号（外键：fk_city）',
  `apply_amount`            decimal(18,2) DEFAULT '0.00' COMMENT '申请金额',
  `subsidy_amount`          decimal(18,2) DEFAULT '0.00' COMMENT '补助金额',
  `meal_allowance`          decimal(18,2) DEFAULT '0.00' COMMENT '餐费补助',
  `transportation_allowance` decimal(18,2) DEFAULT '0.00' COMMENT '交通补助',
  `phone_allowance`         decimal(18,2) DEFAULT '0.00' COMMENT '通讯补助',
  PRIMARY KEY (`id`),
  KEY `idx_main_id` (`main_id`),
  KEY `idx_trip_id` (`trip_id`),
  KEY `idx_traveler_id` (`traveler_id`),
  KEY `idx_subsidy_city` (`subsidy_city_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补助信息表';

-- --------------------------------------------------------
-- 10. 补助日历明细表 fk_reim_subsidy_detail（按天展开）
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_subsidy_detail`;
CREATE TABLE `fk_reim_subsidy_detail` (
  `id`                  varchar(32)   NOT NULL COMMENT '主键ID',
  `subsidy_id`          varchar(32)   NOT NULL COMMENT '补助信息ID（外键：fk_reim_subsidy）',
  `detail_date`         date          NOT NULL COMMENT '明细日期',
  `week_day`            varchar(10)   DEFAULT NULL COMMENT '星期数',
  `city_no`             varchar(10)   NOT NULL COMMENT '补助城市编号（外键：fk_city）',
  `meal_std`            decimal(18,2) DEFAULT '0.00' COMMENT '餐费标准',
  `meal_amount`         decimal(18,2) DEFAULT '0.00' COMMENT '餐费补助金额',
  `transport_std`       decimal(18,2) DEFAULT '0.00' COMMENT '交通标准',
  `transport_amount`    decimal(18,2) DEFAULT '0.00' COMMENT '交通补助金额',
  `phone_std`           decimal(18,2) DEFAULT '0.00' COMMENT '通讯标准',
  `phone_amount`        decimal(18,2) DEFAULT '0.00' COMMENT '通讯补助金额',
  `is_selected`         tinyint(1)    NOT NULL DEFAULT '1' COMMENT '是否选中：0否 1是',
  PRIMARY KEY (`id`),
  KEY `idx_subsidy_id` (`subsidy_id`),
  KEY `idx_detail_date` (`detail_date`),
  KEY `idx_city_no` (`city_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='补助日历明细表';

-- --------------------------------------------------------
-- 11. 费用分摊表 fk_reim_apportion
-- --------------------------------------------------------
DROP TABLE IF EXISTS `fk_reim_apportion`;
CREATE TABLE `fk_reim_apportion` (
  `id`               varchar(32)   NOT NULL COMMENT '主键ID',
  `main_id`          varchar(32)   NOT NULL COMMENT '报销单主表ID（外键）',
  `reim_company_id`  varchar(32)   DEFAULT NULL COMMENT '费用归属公司ID（外键：fk_reim_company）',
  `project_id`       varchar(32)   DEFAULT NULL COMMENT '项目ID（外键：fk_project）',
  `apportion_ratio`  decimal(5,2)  DEFAULT '0.00' COMMENT '分摊比例（存储0-1，页面展示为百分比）',
  `apportion_amount` decimal(18,2) DEFAULT '0.00' COMMENT '分摊金额',
  `sort_no`          int(11)       NOT NULL DEFAULT '0' COMMENT '行号（第1行不可编辑，用于差值处理）',
  PRIMARY KEY (`id`),
  KEY `idx_main_id` (`main_id`),
  KEY `idx_company_id` (`reim_company_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用分摊表';

-- ========================================================
-- 外键约束（可选，根据实际项目需要开启或注释）
-- ========================================================

ALTER TABLE `fk_reim_main`
  ADD CONSTRAINT `fk_main_reimburser` FOREIGN KEY (`reimburser_id`) REFERENCES `fk_reim_employee` (`reimburser_id`),
  ADD CONSTRAINT `fk_main_department` FOREIGN KEY (`reim_department_id`) REFERENCES `fk_reim_department` (`reim_department_id`),
  ADD CONSTRAINT `fk_main_company` FOREIGN KEY (`reim_company_id`) REFERENCES `fk_reim_company` (`reim_company_id`),
  ADD CONSTRAINT `fk_main_business_type` FOREIGN KEY (`business_type_id`) REFERENCES `fk_business_type` (`business_type_id`);

ALTER TABLE `fk_reim_trip`
  ADD CONSTRAINT `fk_trip_main` FOREIGN KEY (`main_id`) REFERENCES `fk_reim_main` (`id`),
  ADD CONSTRAINT `fk_trip_traveler` FOREIGN KEY (`traveler_id`) REFERENCES `fk_reim_employee` (`reimburser_id`),
  ADD CONSTRAINT `fk_trip_departure_city` FOREIGN KEY (`departure_city_no`) REFERENCES `fk_city` (`city_no`),
  ADD CONSTRAINT `fk_trip_arrival_city` FOREIGN KEY (`arrival_city_no`) REFERENCES `fk_city` (`city_no`);

ALTER TABLE `fk_reim_subsidy`
  ADD CONSTRAINT `fk_subsidy_main` FOREIGN KEY (`main_id`) REFERENCES `fk_reim_main` (`id`),
  ADD CONSTRAINT `fk_subsidy_trip` FOREIGN KEY (`trip_id`) REFERENCES `fk_reim_trip` (`id`),
  ADD CONSTRAINT `fk_subsidy_traveler` FOREIGN KEY (`traveler_id`) REFERENCES `fk_reim_employee` (`reimburser_id`),
  ADD CONSTRAINT `fk_subsidy_city` FOREIGN KEY (`subsidy_city_no`) REFERENCES `fk_city` (`city_no`);

ALTER TABLE `fk_reim_subsidy_detail`
  ADD CONSTRAINT `fk_detail_subsidy` FOREIGN KEY (`subsidy_id`) REFERENCES `fk_reim_subsidy` (`id`),
  ADD CONSTRAINT `fk_detail_city` FOREIGN KEY (`city_no`) REFERENCES `fk_city` (`city_no`);

ALTER TABLE `fk_reim_apportion`
  ADD CONSTRAINT `fk_apportion_main` FOREIGN KEY (`main_id`) REFERENCES `fk_reim_main` (`id`),
  ADD CONSTRAINT `fk_apportion_company` FOREIGN KEY (`reim_company_id`) REFERENCES `fk_reim_company` (`reim_company_id`),
  ADD CONSTRAINT `fk_apportion_project` FOREIGN KEY (`project_id`) REFERENCES `fk_project` (`project_id`);
