SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `server_variables` (
	`key` varchar(30) NOT NULL,
	`value` varchar(30) NOT NULL,
	PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for player_appearance
-- ----------------------------
CREATE TABLE `player_appearance` (
	`player_id` int(11) NOT NULL,
	`face` int(11) NOT NULL,
	`hair` int(11) NOT NULL,
	`deco` int(11) NOT NULL,
	`tattoo` int(11) NOT NULL,
	`skin_rgb` int(11) NOT NULL,
	`hair_rgb` int(11) NOT NULL,
	`lip_rgb` int(11) NOT NULL,
	`face_shape` int(11) NOT NULL,
	`forehead` int(11) NOT NULL,
	`eye_height` int(11) NOT NULL,
	`eye_space` int(11) NOT NULL,
	`eye_width` int(11) NOT NULL,
	`eye_size` int(11) NOT NULL,
	`eye_shape` int(11) NOT NULL,
	`eye_angle` int(11) NOT NULL,
	`brow_height` int(11) NOT NULL,
	`brow_angle` int(11) NOT NULL,
	`brow_shape` int(11) NOT NULL,
	`nose` int(11) NOT NULL,
	`nose_bridge` int(11) NOT NULL,
	`nose_width` int(11) NOT NULL,
	`nose_tip` int(11) NOT NULL,
	`cheek` int(11) NOT NULL,
	`lip_height` int(11) NOT NULL,
	`mouth_size` int(11) NOT NULL,
	`lip_size` int(11) NOT NULL,
	`smile` int(11) NOT NULL,
	`lip_shape` int(11) NOT NULL,
	`jaw_height` int(11) NOT NULL,
	`chin_jut` int(11) NOT NULL,
	`ear_shape` int(11) NOT NULL,
	`head_size` int(11) NOT NULL,
	`neck` int(11) NOT NULL,
	`neck_length` int(11) NOT NULL,
	`shoulders` int(11) NOT NULL,
	`torso` int(11) NOT NULL,
	`chest` int(11) NOT NULL,
	`waist` int(11) NOT NULL,
	`hips` int(11) NOT NULL,
	`arm_thickness` int(11) NOT NULL,
	`hand_size` int(11) NOT NULL,
	`leg_thickness` int(11) NOT NULL,
	`foot_size` int(11) NOT NULL,
	`facial_rate` int(11) NOT NULL,
	`voice` int(11) NOT NULL,
	`height` float NOT NULL,
	PRIMARY KEY  (`player_id`),
	CONSTRAINT `player_id_fk` FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_macrosses` (
	`player_id` int(8) NOT NULL,
	`order` int(3) NOT NULL,
	`macro` text NOT NULL,
	UNIQUE KEY `main` (`player_id`,`order`),
	FOREIGN KEY (`player_id`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for players
-- ----------------------------
CREATE TABLE `players` (
	`id` int(11) NOT NULL,
	`name` varchar(50) NOT NULL,
	`account_id` int(11) NOT NULL, 
	`account_name` varchar(50) NOT NULL,
	`exp` bigint(20) NOT NULL default 0,
	`hp` int(11) NOT NULL default 0,
	`mp` int(11) NOT NULL default 0,
	`dp` int(11) NOT NULL default 0,
	`kinah` int(11) NOT NULL DEFAULT 0,
	`x` float NOT NULL,
	`y` float NOT NULL,
	`z` float NOT NULL,
	`heading` int(11) NOT NULL,
	`world_id` int(11) NOT NULL,
	`gender` enum('MALE','FEMALE') NOT NULL,
	`race` enum('ASMODIANS','ELYOS') NOT NULL,
	`player_class` enum('WARRIOR','GLADIATOR','TEMPLAR','SCOUT','ASSASSIN','RANGER','MAGE','SORCERER','SPIRIT_MASTER','PRIEST','CLERIC','CHANTER') NOT NULL,
	`creation_date` timestamp NOT NULL default '0000-00-00 00:00:00',
	`deletion_date` timestamp NULL DEFAULT NULL,
	`last_online` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
	`admin` boolean NOT NULL DEFAULT FALSE,
	`note` text,
	PRIMARY KEY  (`id`),
	UNIQUE KEY `name_unique` (`name`),
	INDEX (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for players
-- ----------------------------

CREATE TABLE `friends` (
	`player` int(11) NOT NULL,
	`friend` int(11) NOT NULL,
	PRIMARY KEY (`player`,`friend`),
	FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (`friend`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for blocks
-- ----------------------------

CREATE TABLE `blocks` (
	`player` int(11) NOT NULL,
	`blocked_player` int(11) NOT NULL,
	`reason` varchar(100) NOT NULL DEFAULT '',
	PRIMARY KEY (`player`,`blocked_player`),
	FOREIGN KEY (`player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (`blocked_player`) REFERENCES `players` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Structure de la table `player_skills`
--

CREATE TABLE IF NOT EXISTS `player_skills` (
	`player_id` int(11) NOT NULL default '0',
	`skillId` int(11) NOT NULL default '0',
	`skillLevel` int(11) NOT NULL default '0',
	PRIMARY KEY  (`player_id`,`skillId`,`skillLevel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `skill_trees`
--

CREATE TABLE IF NOT EXISTS `skill_trees` (
	`class_id` int(10) unsigned NOT NULL default '0',
	`skillId` int(10) unsigned NOT NULL default '0',
	`skillLevel` int(10) unsigned NOT NULL default '0',
	`name` varchar(40) NOT NULL default '',
	`type` int(10) unsigned NOT NULL default '0',
	`min_level` int(10) unsigned NOT NULL default '0',
	PRIMARY KEY  (`class_id`,`skillId`,`skillLevel`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `skill_trees` (`class_id`, `skillId`, `skillLevel`, `name`, `type`, `min_level`) VALUES
(0, 1, 1, 'Basic Sword Training', 0, 1),
(0, 3, 1, 'Basic Mace Training', 0, 1),
(0, 4, 1, 'Basic Clothing Proficiency', 0, 1),
(0, 5, 1, 'Basic Leather Armor Proficiency', 0, 1),
(0, 6, 1, 'Basic Chain Armor Proficiency', 0, 1),
(0, 7, 1, 'Basic Shield Training', 0, 1),
(0, 8, 1, 'Advanced Sword Training I', 0, 9),
(0, 9, 1, 'Advanced Dagger Training I', 0, 9),
(0, 10, 1, 'Advanced Mace Training I', 0, 9),
(0, 12, 1, 'Quality Leather Armor Proficiency I', 0, 9),
(0, 13, 1, 'Quality Chain Armor Proficiency I', 0, 9),
(0, 14, 1, 'Advanced Shield Training I', 0, 9),
(0, 15, 1, 'Advanced Great Sword Training I', 0, 9),
(0, 16, 1, 'Advanced Polearm Training I', 0, 9),
(0, 17, 1, 'Advanced Archery Training I', 0, 9),
(0, 18, 1, 'Quality Plate Armor Proficiency I', 0, 9),
(0, 67, 1, 'Basic Cloth Armor Proficiency', 0, 1),
(0, 112, 1, 'Boost Physical Attack I', 0, 1),
(0, 120, 1, 'Boost Parry I', 1, 9),
(0, 121, 1, 'Boost HP I', 1, 5),
(0, 151, 1, 'Weakening Severe Blow I', 1, 5),
(0, 154, 1, 'Shield Counterattack I', 1, 9),
(0, 161, 1, 'Shouting I', 1, 7),
(0, 165, 1, 'Resolute Strike I', 1, 3),
(0, 169, 1, 'Severe Strike I', 0, 1),
(0, 173, 1, 'Shield Defense I', 1, 3),
(0, 1801, 1, 'Return', 0, 1),
(0, 1803, 1, 'Bandage Heal', 0, 1),
(3, 1, 1, 'Basic Sword Training', 0, 1),
(3, 2, 1, 'Basic Dagger Training', 0, 1),
(3, 4, 1, 'Basic Clothing Proficiency', 0, 1),
(3, 5, 1, 'Basic Leather Armor Proficiency', 0, 1),
(3, 8, 1, 'Advanced Sword Training I', 0, 9),
(3, 9, 1, 'Advanced Dagger Training I', 0, 9),
(3, 12, 1, 'Quality Leather Armor Proficiency I', 0, 9),
(3, 17, 1, 'Advanced Archery Training I', 0, 9),
(3, 19, 1, 'Advanced Dual-Wielding', 1, 5),
(3, 67, 1, 'Basic Cloth Armor Proficiency', 0, 1),
(3, 112, 1, 'Evasion Rate increase I', 1, 9),
(3, 113, 1, 'Boost Accuracy I', 1, 5),
(3, 360, 2, 'Equip Off Hand Weapon II', 1, 9),
(3, 551, 1, 'Surprice Attack I', 1, 3),
(3, 555, 1, 'Counterslash I', 1, 5),
(3, 559, 1, 'Hide I', 1, 5),
(3, 564, 1, 'Swift Edge I', 0, 1),
(3, 568, 1, 'Soul Slash I', 1, 7),
(3, 572, 1, 'Focused Evasion I', 0, 1),
(3, 577, 1, 'Devotion I', 1, 9),
(3, 1801, 1, 'Return', 0, 1),
(3, 1803, 1, 'Bandage Heal', 0, 1),
(6, 4, 1, 'Basic Clothing Proficiency', 0, 1),
(6, 64, 1, 'Basic Spellbook Training', 0, 1),
(6, 67, 1, 'Basic Cloth Armor Proficiency', 0, 1),
(6, 68, 1, 'Advanced Orb Training I', 0, 9),
(6, 70, 1, 'Advanced Cloth Armor Proficiency I', 0, 9),
(6, 71, 1, 'Advanced Spellbook Training I', 0, 9),
(6, 105, 1, 'Concentration I', 1, 3),
(6, 1099, 1, 'Restraint I', 0, 1),
(6, 1351, 1, 'Flame Arrow I', 0, 1),
(6, 1355, 1, 'Cold Wave', 1, 7),
(6, 1358, 1, 'Erosion I', 1, 3),
(6, 1362, 1, 'Ice Chain I', 1, 3),
(6, 1366, 1, 'Blaze', 1, 5),
(6, 1370, 1, 'Energy Absorption I', 1, 9),
(6, 1377, 1, 'Stone Skin I', 1, 7),
(6, 1801, 1, 'Return', 0, 1),
(6, 1803, 1, 'Bandage Heal', 0, 1),
(9, 3, 1, 'Basic Mace Training', 0, 1),
(9, 4, 1, 'Basic Clothing Proficiency', 0, 1),
(9, 5, 1, 'Basic Leather Armor Proficiency', 0, 1),
(9, 10, 1, 'Advanced Mace Training I', 0, 9),
(9, 12, 1, 'Quality Leather Armor Proficiency I', 0, 9),
(9, 13, 1, 'Quality Chain Armor Proficiency I', 0, 9),
(9, 14, 1, 'Advanced Shield Training I', 0, 9),
(9, 53, 1, 'Advanced Staff Training I', 0, 9),
(9, 67, 1, 'Basic Cloth Armor Proficiency', 0, 1),
(9, 70, 1, 'Advanced Cloth Armor Proficiency I', 0, 9),
(9, 408, 1, 'Blessing of Life I', 1, 3),
(9, 955, 1, 'Blessing of Protection I', 1, 5),
(9, 958, 1, 'Light of Renewal I', 1, 5),
(9, 962, 1, 'Condemnation I', 1, 3),
(9, 965, 1, 'Healing Light I', 0, 1),
(9, 969, 1, 'Promise of Wind I', 1, 9),
(9, 972, 1, 'Light of Resurrection I', 1, 9),
(9, 975, 1, 'Retribution of Earth I', 0, 1),
(9, 979, 1, 'Heavens Judgement I', 1, 7),
(9, 1801, 1, 'Return', 0, 1),
(9, 1803, 1, 'Bandage Heal', 0, 1);

