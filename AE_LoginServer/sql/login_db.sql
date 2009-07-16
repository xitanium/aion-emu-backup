  -- ----------------------------
-- Table structure for account_data
-- ----------------------------
CREATE TABLE `account_data`(
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  `access_level` tinyint(3) NOT NULL default 0,
  `last_server` tinyint(3) NOT NULL default -1,
  `last_ip` varchar(20),
  `ip_force` varchar(20),
  PRIMARY KEY (`id`),
  UNIQUE (`name`)
);

  -- ----------------------------
-- Table structure for account_time
-- ----------------------------

CREATE TABLE `account_time`(
  `id` int NOT NULL,
  `last_active` timestamp,
  `expiration_time` timestamp NULL DEFAULT NULL,
  `session_duration` int(10) DEFAULT 0,
  `accumulated_online` int(10) DEFAULT 0,
  `accumulated_rest` int(10) DEFAULT 0,
  `penalty_end` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
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

-- ----------------------------
-- Table structure for gameservers
-- ----------------------------
CREATE TABLE `gameservers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `mask` varchar(45) NOT NULL,
  `password` varchar(65) NOT NULL,
  PRIMARY KEY (`id`)
);