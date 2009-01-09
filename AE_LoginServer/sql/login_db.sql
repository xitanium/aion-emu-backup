-- ----------------------------
-- Table structure for account_data
-- ----------------------------
CREATE TABLE `account_data` (
  `name` varchar(45) NOT NULL DEFAULT '',
  `password` varchar(65) DEFAULT NULL,
  `last_active` decimal(20,0) NOT NULL DEFAULT '0',
  `expiration_time` decimal(20,0) NOT NULL DEFAULT '-1',
  `penalty_end` decimal(20,0) NOT NULL DEFAULT '-1',
  `access` int(3) NOT NULL DEFAULT '0',
  `last_server` int(3) NOT NULL DEFAULT '1',
  `last_ip` varchar(15) DEFAULT NULL,
  `ip_force` varchar(20) NOT NULL DEFAULT '*.*.*.*',
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for ban_ip
-- ----------------------------
CREATE TABLE `ban_ip` (
  `mask` varchar(20) NOT NULL,
  `time_end` decimal(20,0) DEFAULT NULL,
  PRIMARY KEY (`mask`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;