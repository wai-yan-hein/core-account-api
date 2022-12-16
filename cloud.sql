ALTER TABLE `department` 
ADD COLUMN `map_dept_id` INT NULL AFTER `mac_id`;
ALTER TABLE `chart_of_account` 
ADD COLUMN `intg_upd_status` VARCHAR(15) NULL AFTER `deleted`;


ALTER TABLE `gl` 
DROP COLUMN `split_id`,
ADD COLUMN `dept_id` INT NOT NULL DEFAULT 1;


set sql_safe_updates =0;
update gl
set gl_code = concat('02-',gl_code);

CREATE  VIEW `v_gl` AS select `gl`.`gl_code` AS `gl_code`,`gl`.`gl_date` AS `gl_date`,`gl`.`created_date` AS `created_date`,`gl`.`modify_date` AS `modify_date`,`gl`.`modify_by` AS `modify_by`,`gl`.`user_code` AS `user_code`,`gl`.`description` AS `description`,`gl`.`source_ac_id` AS `source_ac_id`,`gl`.`account_id` AS `account_id`,`gl`.`cur_code` AS `cur_code`,`gl`.`dr_amt` AS `dr_amt`,`gl`.`cr_amt` AS `cr_amt`,`gl`.`reference` AS `reference`,`gl`.`dept_code` AS `dept_code`,`gl`.`voucher_no` AS `voucher_no`,`gl`.`trader_code` AS `trader_code`,`gl`.`comp_code` AS `comp_code`,`gl`.`tran_source` AS `tran_source`,`gl`.`gl_vou_no` AS `gl_vou_no`,`gl`.`remark` AS `remark`,`gl`.`naration` AS `naration`,`gl`.`mac_id` AS `mac_id`,`gl`.`ref_no` AS `ref_no`,`gl`.`dept_id` AS `dept_id`,`cur1`.`cur_name` AS `fcur_name`,`dept`.`dept_name` AS `dept_name`,`dept`.`usr_code` AS `dep_usr_code`,`t`.`user_code` AS `t_user_code`,`t`.`trader_name` AS `trader_name`,`t`.`discriminator` AS `discriminator`,`coa1`.`coa_code_usr` AS `src_usr_code`,`coa1`.`coa_name_eng` AS `src_acc_name`,`coa1`.`coa_parent` AS `src_parent_2`,`coa1`.`coa_parent_2` AS `src_parent_1`,`coa2`.`coa_code_usr` AS `acc_usr_code`,`coa2`.`coa_name_eng` AS `acc_name`,`coa2`.`coa_parent` AS `acc_parent_2`,`coa2`.`coa_parent_2` AS `acc_parent_1` from (((((`gl` left join `v_coa_lv3` `coa1` on(`gl`.`source_ac_id` = `coa1`.`coa_code` and `gl`.`comp_code` = `coa1`.`comp_code`)) left join `v_coa_lv3` `coa2` on(`gl`.`account_id` = `coa2`.`coa_code`)) join `currency` `cur1` on(`gl`.`cur_code` = `cur1`.`cur_code`)) left join `trader` `t` on(`gl`.`trader_code` = `t`.`code`)) join `department` `dept` on(`gl`.`dept_code` = `dept`.`dept_code`));
