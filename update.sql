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
ALTER TABLE `gl`
CHANGE COLUMN `naration` `narration` VARCHAR(500) NULL DEFAULT NULL ;

alter table gl
add column batch_no varchar(15) null after deleted,
add column project_no varchar(15) null after batch_no;


