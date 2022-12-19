alter table department 
add column map_dept_id int null after mac_id;
alter table chart_of_account 
add column intg_upd_status varchar(15) null after deleted;


alter table gl 
drop column split_id,
add column dept_id int not null default 1;

drop table acc_setting, machine_info, menu, menu_type, privilege, prj_coa_mapping, prj_cus_mapping, prj_usr_mapping, project, report, role_setting, role_status, sys_prop, sys_prop_template, tmp_balance_sheet, tmp_balance_sheet_detail, tmp_cash_io, tmp_cf, tmp_closing, tmp_closing_detail, tmp_coa, tmp_conversion, tmp_gl_filter, tmp_in_ex, tmp_in_ex_detail, tmp_op_cl, tmp_op_cl_apar, tmp_op_filter, tmp_profit_lost, tmp_profit_lost_detail, tmp_trader_balance, tmp_tri_detail, trader_type, usr_comp_role, user_role;
