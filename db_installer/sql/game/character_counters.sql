CREATE TABLE IF NOT EXISTS `character_counters` (
  `charId` INT UNSIGNED NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `value` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`, `name`),
  FOREIGN KEY (`charId`) REFERENCES `characters`(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
