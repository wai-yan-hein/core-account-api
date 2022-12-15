ALTER TABLE `department` 
ADD COLUMN `map_dept_id` INT NULL AFTER `mac_id`;
ALTER TABLE `chart_of_account` 
ADD COLUMN `intg_upd_status` VARCHAR(15) NULL AFTER `deleted`;


ALTER TABLE `gl` 
DROP COLUMN `split_id`,
ADD COLUMN `dept_id` INT NOT NULL DEFAULT 1 AFTER `exchange_id`;
