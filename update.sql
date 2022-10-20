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
