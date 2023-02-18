alter table department 
add column map_dept_id int null after mac_id;
alter table chart_of_account 
add column intg_upd_status varchar(15) null after deleted;


alter table gl 
drop column split_id,
add column dept_id int not null default 1;

drop table if exists acc_setting, machine_info, menu, menu_type, privilege, prj_coa_mapping, prj_cus_mapping, prj_usr_mapping, project, report, role_setting, role_status, sys_prop, sys_prop_template, tmp_balance_sheet, tmp_balance_sheet_detail, tmp_cash_io, tmp_cf, tmp_closing_detail, tmp_coa, tmp_conversion, tmp_gl_filter, tmp_in_ex, tmp_in_ex_detail, tmp_op_cl_apar, tmp_op_filter, tmp_profit_lost, tmp_profit_lost_detail, tmp_trader_balance, tmp_tri_detail, trader_type, usr_comp_role, user_role;


alter table stock_op_value 
add column dept_id int not null default 1 after comp_code,
add column tran_code varchar(15) not null first,
add column deleted bit(1) not null default 0 after updated_date,
change column comp_code comp_code varchar(15) not null after tran_code,
change column tran_date tran_date date not null;


alter table stock_op_value
drop primary key,
add primary key (tran_code, comp_code, dept_id);

alter table gl_log 
drop column exchange_id,
drop column intg_upd_status,
drop column split_id;

ALTER TABLE `gl_log` 
ADD COLUMN `dept_id` INT NOT NULL AFTER `mac_id`,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`log_gl_code`, `gl_code`, `dept_id`);
;

ALTER TABLE `gl`
ADD COLUMN `deleted` BIT(1) NOT NULL DEFAULT 0 AFTER `dept_id`;
ALTER TABLE `chart_of_account`
ADD COLUMN `credit` BIT(1) NOT NULL DEFAULT 0 AFTER `intg_upd_status`;


drop table if exists tmp_tri,tmp_closing,tmp_ex_rate;

create table tmp_tri (
  coa_code varchar(25) not null,
  curr_id varchar(15) not null,
  mac_id int(11) not null,
  dr_amt double(20,3) default null,
  cr_amt double(20,3) default null,
  dept_code varchar(15) not null,
  comp_code varchar(15) default null,
  primary key (coa_code,curr_id,mac_id,dept_code)
) engine=innodb default charset=utf8mb3;

create table tmp_closing (
  coa_code varchar(15) not null,
  cur_code varchar(15) not null,
  dr_amt double default null,
  cr_amt double default null,
  dept_code varchar(15) not null,
  mac_id int(11) not null,
  comp_code varchar(15) default null,
  primary key (coa_code,mac_id,cur_code,dept_code)
) engine=innodb default charset=utf8mb3;

create table tmp_ex_rate (
  home_cur varchar(15) not null,
  ex_cur varchar(15) not null,
  home_rate double(10,3) default null,
  ex_rate double(10,3) default null,
  mac_id int(11) not null,
  comp_code varchar(15) default null,
  primary key (home_cur,mac_id,ex_cur)
) engine=innodb default charset=utf8mb3;





