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

alter table gl_log
add column dept_id int not null after mac_id,
drop primary key,
add primary key (log_gl_code, gl_code, dept_id);
;

alter table gl
add column deleted bit(1) not null default 0 after dept_id;
alter table chart_of_account
add column credit bit(1) not null default 0 after intg_upd_status;


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

drop table if exists tmp_tri;
create table tmp_tri (
  coa_code varchar(25) not null,
  curr_id varchar(15) not null,
  mac_id int(11) not null,
  dr_amt double default null,
  cr_amt double default null,
  dept_code varchar(15) not null,
  comp_code varchar(15) not null,
  primary key (coa_code,curr_id,mac_id,dept_code,comp_code)
) engine=innodb default charset=utf8mb3;

alter table gl
drop column old_dept_code,
drop column exchange_id,
add column from_des varchar(255) null after deleted,
add column for_des varchar(255) null after from_des;
alter table gl
change column naration narration varchar(500) null default null ;

alter table gl
add column batch_no varchar(15) null after deleted,
add column project_no varchar(15) null after batch_no;


alter table trader
add column deleted bit(1) not null default 0 after group_code;

alter table chart_of_account
change column comp_code comp_code varchar(15) not null after coa_code,
drop primary key,
add primary key (coa_code, comp_code);
alter table trader
change column comp_code comp_code varchar(15) not null after code,
drop primary key,
add primary key (code, comp_code);
alter table department
change column comp_code comp_code varchar(15) not null after dept_code,
drop primary key,
add primary key (dept_code, comp_code);

alter table gl
change column comp_code comp_code varchar(15) not null after gl_code,
drop primary key,
add primary key (gl_code, comp_code);

alter table department
add column deleted bit(1) not null default 0 after map_dept_id;

alter table trader
add column deleted bit(1) not null default 0 after mac_id;

alter table coa_opening
add column project_no varchar(15) null after deleted;

alter table stock_op_value
add column project_no varchar(15) null after deleted;

create table coa_template (
  coa_code varchar(15) not null,
  bus_id varchar(15) not null,
  coa_code_usr varchar(15) default null,
  coa_name_eng varchar(255) default null,
  coa_name_mya varchar(255) default null,
  active bit(1) default null,
  coa_parent varchar(15) default null,
  coa_level int(11) default null,
  cur_code varchar(15) default null,
  dept_code varchar(15) default null,
  credit bit(1) not null default b'0',
  primary key (coa_code,bus_id)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table coa_opening
add column deleted bit(1) not null default 0 after trader_code;

alter table gl
add column patient_no varchar(15) null after project_no,
add column doctor_id varchar(15) null after patient_no,
add column service_id varchar (15) after doctor_id,
change column gl_date gl_date timestamp not null ;

alter table chart_of_account
change column modify_date modify_date timestamp not null default current_timestamp() on update current_timestamp() ;


alter table coa_opening
change column created_date created_date timestamp not null default current_timestamp() on update current_timestamp();

alter table stock_op_value
change column created_date created_date timestamp not null default current_timestamp() on update current_timestamp() ,
change column updated_date updated_date timestamp not null default current_timestamp() on update current_timestamp() ;

alter table trader
add column nrc varchar(255) null after deleted;

alter table trader
change column user_code user_code varchar(255) null default null;

alter table trader
add column group_code varchar(15) null default null after user_code;

alter table gl
add column order_id int null after service_id,
add column ex_code varchar(15) null after order_id;

create table trader_group (
  group_code varchar(15) not null,
  comp_code varchar(15) not null,
  user_code varchar(15) default null,
  group_name varchar(255) default null,
  primary key (group_code,comp_code)
) engine=innodb default charset=utf8mb3 collate=utf8mb3_general_ci;

alter table department
change column updated_dt updated_dt timestamp not null default current_timestamp() ;

alter table gl
add column qty double(20,3) null after for_des,
add column price double(20,3) null after qty;

alter table chart_of_account
add column bank_no varchar(20) null after credit;

#optional
set sql_safe_updates =0;
update gl
set gl_date = concat(date(gl_date), ' ', current_time)
where time(gl_date)='00:00:00';


set sql_safe_updates =0;
#cleaning deleted data
#delete from gl where deleted = true and mac_id =99;

