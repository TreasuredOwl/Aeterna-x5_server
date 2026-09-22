-- ----------------------------
-- Table structure for hitman_list
-- Stores active assassination contracts
-- ----------------------------
DROP TABLE IF EXISTS `hitman_list`;
CREATE TABLE `hitman_list` (
  `target_id` INT NOT NULL DEFAULT 0,
  `client_id` INT NOT NULL DEFAULT 0,
  `target_name` VARCHAR(35) NOT NULL DEFAULT '',
  `item_id` INT NOT NULL DEFAULT 57,
  `bounty` BIGINT NOT NULL DEFAULT 0,
  `pending_delete` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`target_id`),
  KEY `idx_client` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
