-- ----------------------------
-- Table structure for `user_variables`
-- ----------------------------
DROP TABLE IF EXISTS `user_variables`;
CREATE TABLE `user_variables` (
  `obj_id` int(11) NOT NULL DEFAULT '0',
  `name` varchar(86) NOT NULL DEFAULT '0',
  `value` text NOT NULL,
  UNIQUE KEY `prim` (`obj_id`,`name`),
  KEY `obj_id` (`obj_id`),
  KEY `name` (`name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
