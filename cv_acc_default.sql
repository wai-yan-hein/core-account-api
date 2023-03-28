-- MariaDB dump 10.19  Distrib 10.11.2-MariaDB, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: cv_acc_kgswan
-- ------------------------------------------------------
-- Server version	10.11.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cf_category`
--

DROP TABLE IF EXISTS `cf_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_category` (
  `coa_code` varchar(15) NOT NULL,
  `group_code` varchar(15) NOT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `opp` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`coa_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_category`
--

LOCK TABLES `cf_category` WRITE;
/*!40000 ALTER TABLE `cf_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_group`
--

DROP TABLE IF EXISTS `cf_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_group` (
  `group_code` varchar(15) NOT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_group`
--

LOCK TABLES `cf_group` WRITE;
/*!40000 ALTER TABLE `cf_group` DISABLE KEYS */;
INSERT INTO `cf_group` VALUES
('00001','Cash Flow From Operating Activities','01'),
('00002','Cash Flow From Investing Activities','02'),
('00003','Cash Flow From Financing Activities','03');
/*!40000 ALTER TABLE `cf_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chart_of_account`
--

DROP TABLE IF EXISTS `chart_of_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chart_of_account` (
  `coa_code` varchar(15) NOT NULL,
  `coa_code_usr` varchar(15) DEFAULT NULL,
  `coa_name_eng` varchar(255) DEFAULT NULL,
  `coa_name_mya` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `modify_date` timestamp(1) NOT NULL DEFAULT current_timestamp(1) ON UPDATE current_timestamp(1),
  `sort_order_id` int(11) DEFAULT NULL,
  `created_by` varchar(15) NOT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `coa_parent` varchar(15) DEFAULT NULL,
  `coa_option` varchar(5) NOT NULL,
  `comp_code` varchar(15) NOT NULL,
  `coa_level` int(11) DEFAULT NULL,
  `parent_usr_code` varchar(15) DEFAULT NULL,
  `app_short_name` varchar(10) DEFAULT NULL,
  `mig_code` varchar(15) DEFAULT NULL,
  `mac_id` int(11) DEFAULT NULL,
  `cur_code` varchar(15) DEFAULT NULL,
  `marked` bit(1) DEFAULT NULL,
  `dept_code` varchar(15) DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `intg_upd_status` varchar(15) DEFAULT NULL,
  `credit` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`coa_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chart_of_account`
--

LOCK TABLES `chart_of_account` WRITE;
/*!40000 ALTER TABLE `chart_of_account` DISABLE KEYS */;
INSERT INTO `chart_of_account` VALUES
('01','01','FIXED ASSETS',NULL,'','2021-12-27 12:45:22','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'1',1,NULL,'',NULL,'\0',NULL,'\0'),
('02','02','CAPITAL',NULL,'','2021-12-27 12:45:22','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'2',1,NULL,'',NULL,'\0',NULL,'\0'),
('03','03','LIABILITIES',NULL,'','2021-12-27 12:45:23','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'3',1,NULL,'',NULL,'\0',NULL,'\0'),
('04','04','INCOME',NULL,'','2021-12-27 12:45:23','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'4',1,NULL,'',NULL,'\0',NULL,'\0'),
('05','05','GENERAL EXPENSE',NULL,'','2021-12-27 12:45:23','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'5',1,NULL,'',NULL,'\0',NULL,'\0'),
('06','06','CURRENT ASSETS',NULL,'','2021-12-27 12:45:23','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'6',1,NULL,'',NULL,'\0',NULL,'\0'),
('07','07','PURCHASE EXPENSE',NULL,'','2021-12-27 12:45:24','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'7',1,NULL,'',NULL,'\0',NULL,'\0'),
('08','08','OTHER INCOME',NULL,'','2021-12-27 12:45:25','2023-01-11 09:50:37.4',NULL,'1','1','#','SYS','01',1,NULL,NULL,'8',1,NULL,'',NULL,'\0',NULL,'\0'),
('100','110','Fixed Assests',NULL,'','2023-01-10 20:20:58','2023-01-10 14:02:38.0',NULL,'admin','admin','01','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('101','111','Other Assets',NULL,'','2023-01-10 20:32:33','2023-01-10 14:02:33.0',NULL,'admin','admin','01','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('200','210','Share Holder\'s Equity',NULL,'','2023-01-10 20:37:36','2023-01-10 14:07:36.0',NULL,'admin','admin','02','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('201','240','Drawing A/C',NULL,'','2023-01-10 20:37:58','2023-01-10 14:07:58.0',NULL,'admin','admin','02','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('202','220','Retained Earning',NULL,'','2023-01-10 20:38:22','2023-01-10 14:09:53.0',NULL,'admin','admin','02','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('203','230','Profit & Loss A/C',NULL,'','2023-01-10 20:38:47','2023-01-10 14:09:03.0',NULL,'admin','admin','02','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('300','310','Trade Creditor',NULL,'','2023-01-10 20:41:00','2023-01-10 14:11:00.0',NULL,'admin','admin','03','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('300001','002','Creditor',NULL,'','2023-01-11 12:20:22','2023-01-11 05:50:22.0',NULL,'admin','admin','300','USR','01',3,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('301','320','Loan A/C',NULL,'','2023-01-10 20:41:14','2023-01-10 14:11:14.0',NULL,'admin','admin','03','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('302','311','Other Payable',NULL,'','2023-01-10 20:41:31','2023-01-10 14:11:31.0',NULL,'admin','admin','03','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('400','410','Revenue (Sale)',NULL,'','2023-01-10 20:42:16','2023-01-10 14:12:16.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('400001','410101','Pharmacy Income',NULL,'','2023-01-11 14:33:45','2023-01-11 08:03:45.0',NULL,'admin','admin','400','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('401','401','Sale Return',NULL,'','2023-01-10 20:42:35','2023-01-10 14:12:35.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('401001','401001','Sale Return',NULL,'','2023-01-11 15:00:17','2023-01-11 08:30:17.0',NULL,'admin','admin','401','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('402','402','Lab (Income)',NULL,'','2023-01-10 21:08:36','2023-01-11 09:50:37.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('403','403','Investigation (Income)',NULL,'','2023-01-10 21:08:58','2023-01-11 09:50:37.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('404','404','OPD (Income)',NULL,'','2023-01-10 21:09:12','2023-01-11 09:50:37.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('404001','404001','OPD Income',NULL,'','2023-01-11 16:18:41','2023-01-11 09:48:41.0',NULL,'admin','admin','404','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('405','405','OT (Income)',NULL,'','2023-01-10 21:11:23','2023-01-11 09:50:37.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('405001','405001','OT Income',NULL,'','2023-01-11 16:19:06','2023-01-11 09:49:06.0',NULL,'admin','admin','405','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('406','406','Ward (Income)',NULL,'','2023-01-10 21:11:34','2023-01-11 09:50:37.0',NULL,'admin','admin','04','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('406001','406001','Ward Income',NULL,'','2023-01-11 16:19:34','2023-01-11 09:49:34.0',NULL,'admin','admin','406','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('500','550','General Expenses',NULL,'','2023-01-10 20:44:12','2023-01-10 14:14:12.0',NULL,'admin','admin','05','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('500001','550120','Discount Allowed',NULL,'','2023-01-11 14:32:20','2023-01-11 08:02:20.0',NULL,'admin','admin','500','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('501','501','Administractive Expenses',NULL,'','2023-01-10 20:44:34','2023-01-10 14:14:34.0',NULL,'admin','admin','05','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('600','121','Cash A/C',NULL,'','2023-01-10 20:46:04','2023-01-10 14:16:04.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('600001','121001','Daily Cash',NULL,'','2023-01-11 14:22:02','2023-01-11 07:52:02.0',NULL,'admin','admin','600','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('600002','121002','Petty Cash',NULL,'','2023-01-11 14:22:44','2023-01-11 07:52:44.0',NULL,'admin','admin','600','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('600003','121003','Counter Cash',NULL,'','2023-01-11 14:23:28','2023-01-11 07:53:28.0',NULL,'admin','admin','600','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('601','122','Bank',NULL,'','2023-01-10 20:46:47','2023-01-10 14:16:47.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('602','123','Inventory',NULL,'','2023-01-10 20:47:21','2023-01-10 14:17:34.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('603','603','Other Receivable',NULL,'','2023-01-10 20:48:36','2023-01-10 14:18:36.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('604','604','Trade Debtor',NULL,'','2023-01-10 20:48:53','2023-01-10 14:18:53.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('604001','604','Receivable Debtor',NULL,'','2023-01-11 12:15:53','2023-01-11 05:45:53.0',NULL,'admin','admin','604','USR','01',3,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('605','605','Staffs & Other',NULL,'','2023-01-10 20:49:10','2023-01-10 14:19:10.0',NULL,'admin','admin','06','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('700','540','Purchase',NULL,'','2023-01-10 20:50:36','2023-01-10 14:20:36.0',NULL,'admin','admin','07','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('700001','540101','Medicine purchase',NULL,'','2023-01-11 14:56:39','2023-01-11 08:26:39.0',NULL,'admin','admin','700','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('701','701','Purchase Return',NULL,'','2023-01-10 20:50:49','2023-01-10 14:20:49.0',NULL,'admin','admin','07','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('701001','701001','Purchase Return',NULL,'','2023-01-11 15:00:49','2023-01-11 08:30:49.0',NULL,'admin','admin','701','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0'),
('702','702','Operation Expense',NULL,'','2023-01-10 20:51:13','2023-01-10 14:21:13.0',NULL,'admin','admin','07','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('800','420','Other Income',NULL,'','2023-01-10 20:51:54','2023-01-10 14:21:54.0',NULL,'admin','admin','08','USR','01',2,NULL,NULL,NULL,1,NULL,'',NULL,'\0',NULL,'\0'),
('800001','420001','Discount Received',NULL,'','2023-01-11 14:58:43','2023-01-11 08:28:43.0',NULL,'admin','admin','800','USR','01',3,NULL,NULL,NULL,2,NULL,'',NULL,'\0',NULL,'\0');
/*!40000 ALTER TABLE `chart_of_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coa_opening`
--

DROP TABLE IF EXISTS `coa_opening`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `coa_opening` (
  `op_date` date DEFAULT NULL,
  `source_acc_id` varchar(15) DEFAULT NULL,
  `cur_code` varchar(15) DEFAULT NULL,
  `dr_amt` double DEFAULT NULL,
  `cr_amt` double DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `comp_code` varchar(15) DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `dept_code` varchar(15) DEFAULT NULL,
  `coa_op_id` int(15) NOT NULL AUTO_INCREMENT,
  `tran_source` varchar(15) DEFAULT NULL,
  `trader_code` varchar(15) DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`coa_op_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coa_opening`
--

LOCK TABLES `coa_opening` WRITE;
/*!40000 ALTER TABLE `coa_opening` DISABLE KEYS */;
/*!40000 ALTER TABLE `coa_opening` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cur_exchange`
--

DROP TABLE IF EXISTS `cur_exchange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cur_exchange` (
  `ex_code` varchar(15) NOT NULL,
  `ex_date` date NOT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `created_date` datetime NOT NULL,
  `created_by` varchar(15) NOT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `mac_id` int(11) NOT NULL,
  `comp_code` varchar(15) NOT NULL,
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`ex_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cur_exchange`
--

LOCK TABLES `cur_exchange` WRITE;
/*!40000 ALTER TABLE `cur_exchange` DISABLE KEYS */;
/*!40000 ALTER TABLE `cur_exchange` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cur_exchange_detail`
--

DROP TABLE IF EXISTS `cur_exchange_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cur_exchange_detail` (
  `home_cur` varchar(15) NOT NULL,
  `exchange_cur` varchar(15) NOT NULL,
  `ex_code` varchar(15) NOT NULL,
  `ex_rate` double NOT NULL,
  PRIMARY KEY (`home_cur`,`exchange_cur`,`ex_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cur_exchange_detail`
--

LOCK TABLES `cur_exchange_detail` WRITE;
/*!40000 ALTER TABLE `cur_exchange_detail` DISABLE KEYS */;
/*!40000 ALTER TABLE `cur_exchange_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `currency`
--

DROP TABLE IF EXISTS `currency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `currency` (
  `cur_code` varchar(15) NOT NULL,
  `cur_name` varchar(255) DEFAULT NULL,
  `cur_symbol` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `created_dt` timestamp NULL DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_dt` timestamp NULL DEFAULT NULL,
  `cur_gain_acc` varchar(15) DEFAULT NULL,
  `cur_lost_acc` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`cur_code`),
  UNIQUE KEY `cur_code` (`cur_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `currency`
--

LOCK TABLES `currency` WRITE;
/*!40000 ALTER TABLE `currency` DISABLE KEYS */;
INSERT INTO `currency` VALUES
('MMK','Kyat','MMK','',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `currency` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `department` (
  `dept_code` varchar(15) NOT NULL,
  `dept_name` varchar(255) DEFAULT NULL,
  `parent_dept` varchar(15) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `comp_code` varchar(15) DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `created_dt` timestamp NULL DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_dt` timestamp NULL DEFAULT NULL,
  `usr_code` varchar(15) DEFAULT NULL,
  `mac_id` int(11) DEFAULT NULL,
  `map_dept_id` int(11) DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES
('001','Head Office','#','','01',NULL,NULL,'admin','2023-01-10 14:23:26','H',1,NULL,'\0'),
('002','Pharmacy','#','','01',NULL,NULL,'admin','2023-01-10 14:24:04','P',1,NULL,'\0'),
('003','Lab','#','','01',NULL,NULL,'admin','2023-01-11 08:41:59','L',2,NULL,'\0'),
('004','Investigation','#','','01',NULL,NULL,'admin','2023-01-11 08:41:36','I',2,NULL,'\0'),
('005','OT','#','','01',NULL,NULL,'admin','2023-01-11 08:42:31','OT',2,NULL,'\0'),
('006','Ward','#','','01',NULL,NULL,'admin','2023-01-11 08:42:41','W',2,NULL,'\0'),
('007','OPD','#','','01',NULL,NULL,'admin','2023-01-11 08:43:21','OPD',2,NULL,'\0');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `font`
--

DROP TABLE IF EXISTS `font`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `font` (
  `winkeycode` int(11) NOT NULL,
  `zawgyikeycode` int(11) NOT NULL,
  `integrakeycode` int(11) DEFAULT NULL,
  PRIMARY KEY (`winkeycode`,`zawgyikeycode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `font`
--

LOCK TABLES `font` WRITE;
/*!40000 ALTER TABLE `font` DISABLE KEYS */;
/*!40000 ALTER TABLE `font` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gl`
--

DROP TABLE IF EXISTS `gl`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gl` (
  `gl_code` varchar(20) NOT NULL,
  `gl_date` date NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `modify_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `modify_by` varchar(15) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `source_ac_id` varchar(15) NOT NULL,
  `account_id` varchar(15) DEFAULT NULL,
  `cur_code` varchar(15) NOT NULL,
  `dr_amt` double DEFAULT NULL,
  `cr_amt` double DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `dept_code` varchar(15) NOT NULL,
  `voucher_no` varchar(25) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `trader_code` varchar(15) DEFAULT NULL,
  `comp_code` varchar(15) NOT NULL,
  `tran_source` varchar(25) NOT NULL,
  `gl_vou_no` varchar(25) DEFAULT NULL,
  `intg_upd_status` varchar(5) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `narration` varchar(500) DEFAULT NULL,
  `ref_no` varchar(50) DEFAULT NULL,
  `mac_id` int(11) NOT NULL,
  `dept_id` int(11) NOT NULL DEFAULT 1,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  `batch_no` varchar(15) DEFAULT NULL,
  `project_no` varchar(15) DEFAULT NULL,
  `from_des` varchar(255) DEFAULT NULL,
  `for_des` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`gl_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gl`
--

LOCK TABLES `gl` WRITE;
/*!40000 ALTER TABLE `gl` DISABLE KEYS */;
/*!40000 ALTER TABLE `gl` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gl_log`
--

DROP TABLE IF EXISTS `gl_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gl_log` (
  `log_status` varchar(15) NOT NULL,
  `log_user_code` varchar(15) DEFAULT NULL,
  `log_mac_id` varchar(45) NOT NULL,
  `log_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `log_gl_code` varchar(20) NOT NULL,
  `gl_code` varchar(20) NOT NULL,
  `gl_date` date NOT NULL,
  `created_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `modify_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `modify_by` varchar(15) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `source_ac_id` varchar(15) NOT NULL,
  `account_id` varchar(15) DEFAULT NULL,
  `cur_code` varchar(15) NOT NULL,
  `dr_amt` double DEFAULT NULL,
  `cr_amt` double DEFAULT NULL,
  `reference` varchar(255) DEFAULT NULL,
  `dept_code` varchar(15) NOT NULL,
  `voucher_no` varchar(25) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `trader_code` varchar(15) DEFAULT NULL,
  `comp_code` varchar(15) NOT NULL,
  `tran_source` varchar(25) NOT NULL,
  `gl_vou_no` varchar(25) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `naration` varchar(500) DEFAULT NULL,
  `ref_no` varchar(50) DEFAULT NULL,
  `mac_id` int(11) NOT NULL,
  `dept_id` int(11) NOT NULL DEFAULT 1,
  PRIMARY KEY (`log_gl_code`,`gl_code`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gl_log`
--

LOCK TABLES `gl_log` WRITE;
/*!40000 ALTER TABLE `gl_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `gl_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seq_table`
--

DROP TABLE IF EXISTS `seq_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seq_table` (
  `mac_id` int(11) NOT NULL,
  `seq_option` varchar(15) NOT NULL,
  `seq_no` int(11) DEFAULT NULL,
  `period` varchar(15) NOT NULL,
  `comp_code` varchar(15) NOT NULL,
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `created_date` datetime DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`comp_code`,`mac_id`,`seq_option`,`period`),
  KEY `index_seq_option` (`seq_option`),
  KEY `index_comp_code` (`comp_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seq_table`
--

LOCK TABLES `seq_table` WRITE;
/*!40000 ALTER TABLE `seq_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `seq_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_op_value`
--

DROP TABLE IF EXISTS `stock_op_value`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_op_value` (
  `tran_code` varchar(15) NOT NULL,
  `comp_code` varchar(15) NOT NULL,
  `tran_date` date NOT NULL,
  `coa_code` varchar(15) NOT NULL,
  `dept_code` varchar(15) NOT NULL,
  `curr_code` varchar(15) NOT NULL,
  `dept_id` int(11) NOT NULL DEFAULT 1,
  `amount` double DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `deleted` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`tran_code`,`comp_code`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_op_value`
--

LOCK TABLES `stock_op_value` WRITE;
/*!40000 ALTER TABLE `stock_op_value` DISABLE KEYS */;
/*!40000 ALTER TABLE `stock_op_value` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stock_op_value_log`
--

DROP TABLE IF EXISTS `stock_op_value_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `stock_op_value_log` (
  `tran_id` int(11) NOT NULL AUTO_INCREMENT,
  `tran_date` datetime DEFAULT NULL,
  `coa_code` varchar(15) DEFAULT NULL,
  `dept_code` varchar(15) DEFAULT NULL,
  `curr_code` varchar(15) DEFAULT NULL,
  `comp_code` varchar(15) DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `log_date` datetime DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `log_option` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`tran_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stock_op_value_log`
--

LOCK TABLES `stock_op_value_log` WRITE;
/*!40000 ALTER TABLE `stock_op_value_log` DISABLE KEYS */;
INSERT INTO `stock_op_value_log` VALUES
(1,'2022-02-28 00:00:00','002-00009','001-0001','MMK','0010010',1000000,NULL,'1','2022-03-22 12:19:49',NULL,'2022-03-22 05:49:15',NULL,NULL,'DELETE'),
(2,'2022-01-31 00:00:00','002-00009','001-0001','MMK','0010010',1000000,NULL,'1','2022-07-14 15:37:42',NULL,'2022-03-22 05:49:40',NULL,NULL,'DELETE'),
(3,'2022-03-31 00:00:00','002-00009','001-0001','MMK','0010010',5000000,NULL,'1','2022-07-14 16:08:21',NULL,'2022-07-14 09:04:57',NULL,NULL,'EDIT');
/*!40000 ALTER TABLE `stock_op_value_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tmp_closing`
--

DROP TABLE IF EXISTS `tmp_closing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tmp_closing` (
  `coa_code` varchar(15) NOT NULL,
  `cur_code` varchar(15) NOT NULL,
  `dr_amt` double DEFAULT NULL,
  `cr_amt` double DEFAULT NULL,
  `dept_code` varchar(15) NOT NULL,
  `mac_id` int(11) NOT NULL,
  `comp_code` varchar(15) NOT NULL DEFAULT '1',
  PRIMARY KEY (`coa_code`,`mac_id`,`cur_code`,`dept_code`,`comp_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tmp_closing`
--

LOCK TABLES `tmp_closing` WRITE;
/*!40000 ALTER TABLE `tmp_closing` DISABLE KEYS */;
/*!40000 ALTER TABLE `tmp_closing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tmp_dep_filter`
--

DROP TABLE IF EXISTS `tmp_dep_filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tmp_dep_filter` (
  `dept_code` varchar(15) NOT NULL,
  `mac_id` int(11) NOT NULL,
  PRIMARY KEY (`dept_code`,`mac_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tmp_dep_filter`
--

LOCK TABLES `tmp_dep_filter` WRITE;
/*!40000 ALTER TABLE `tmp_dep_filter` DISABLE KEYS */;
INSERT INTO `tmp_dep_filter` VALUES
('001',1),
('002',1),
('003',1),
('004',1),
('004-0001',1),
('005',1),
('006',1),
('007',1);
/*!40000 ALTER TABLE `tmp_dep_filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tmp_tri`
--

DROP TABLE IF EXISTS `tmp_tri`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tmp_tri` (
  `coa_code` varchar(25) NOT NULL,
  `curr_id` varchar(15) NOT NULL,
  `mac_id` int(11) NOT NULL,
  `dr_amt` double DEFAULT NULL,
  `cr_amt` double DEFAULT NULL,
  `dept_code` varchar(15) NOT NULL,
  `comp_code` varchar(15) NOT NULL DEFAULT '1',
  PRIMARY KEY (`coa_code`,`curr_id`,`mac_id`,`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tmp_tri`
--

LOCK TABLES `tmp_tri` WRITE;
/*!40000 ALTER TABLE `tmp_tri` DISABLE KEYS */;
/*!40000 ALTER TABLE `tmp_tri` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `trader`
--

DROP TABLE IF EXISTS `trader`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `trader` (
  `code` varchar(15) NOT NULL,
  `mac_id` int(11) DEFAULT NULL,
  `discriminator` varchar(2) NOT NULL,
  `account_code` varchar(15) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `created_id` varchar(15) DEFAULT NULL,
  `created_date` datetime DEFAULT current_timestamp(),
  `email` varchar(500) DEFAULT NULL,
  `phone` varchar(500) DEFAULT NULL,
  `township` varchar(15) DEFAULT NULL,
  `trader_name` varchar(255) DEFAULT NULL,
  `updated_id` varchar(15) DEFAULT NULL,
  `updated_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `credit_days` int(11) DEFAULT NULL,
  `credit_limit` int(11) DEFAULT NULL,
  `parent` varchar(25) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `comp_code` varchar(15) DEFAULT NULL,
  `app_short_name` varchar(10) DEFAULT NULL,
  `app_trader_code` varchar(25) DEFAULT NULL,
  `trader_type_id` int(11) DEFAULT NULL,
  `reg_code` varchar(15) DEFAULT NULL,
  `contact_person` varchar(100) DEFAULT NULL,
  `coa_code` varchar(15) DEFAULT NULL,
  `mig_code` varchar(15) DEFAULT NULL,
  `created_by` varchar(15) DEFAULT NULL,
  `updated_by` varchar(15) DEFAULT NULL,
  `user_code` varchar(15) DEFAULT NULL,
  `group_code` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `trader`
--

LOCK TABLES `trader` WRITE;
/*!40000 ALTER TABLE `trader` DISABLE KEYS */;
/*!40000 ALTER TABLE `trader` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-03-27 12:01:54
