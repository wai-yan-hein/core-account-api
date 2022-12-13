ALTER TABLE `department` 
ADD COLUMN `map_dept_id` INT NULL AFTER `mac_id`;
ALTER TABLE `chart_of_account` 
ADD COLUMN `intg_upd_status` VARCHAR(15) NULL AFTER `deleted`;
