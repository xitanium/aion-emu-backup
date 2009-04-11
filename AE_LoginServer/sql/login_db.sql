-- ----------------------------
-- Table structure for account_data
-- ----------------------------
CREATE TABLE `account_data`(
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `last_active` timestamp,
  `expiration_time` timestamp NULL DEFAULT NULL,
  `penalty_end` timestamp NULL DEFAULT NULL,
  `access_level` tinyint(3) NOT NULL default 0,
  `last_server` tinyint(3) NOT NULL default -1,
  `last_ip` varchar(20),
  `ip_force` varchar(20),
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
);

-- ----------------------------
-- Table structure for ban_ip
-- ----------------------------
CREATE TABLE `banned_ip` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mask` varchar(45) NOT NULL,
  `time_end` timestamp NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`mask`)
);