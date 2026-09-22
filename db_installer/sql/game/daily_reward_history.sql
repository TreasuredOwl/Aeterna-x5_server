DROP TABLE IF EXISTS `daily_reward_history`;
CREATE TABLE `daily_reward_history` (
  `account_name` VARCHAR(45) NOT NULL DEFAULT '',
  `last_reward_time` BIGINT(20) NOT NULL DEFAULT 0,
  `current_day` INT(3) NOT NULL DEFAULT 1,
  PRIMARY KEY (`account_name`)
) DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
