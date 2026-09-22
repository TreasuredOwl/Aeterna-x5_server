-- Character Achievements Table
-- Stores achievement completion status to prevent duplicate rewards

CREATE TABLE IF NOT EXISTS `character_achievements` (
  `charId` INT UNSIGNED NOT NULL,
  `achievementId` INT UNSIGNED NOT NULL,
  `completed` BOOLEAN NOT NULL DEFAULT FALSE,
  `claimed` BOOLEAN NOT NULL DEFAULT FALSE,
  `dateCompleted` timestamp NULL DEFAULT NULL,
  `dateClaimed` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`charId`, `achievementId`),
  FOREIGN KEY (`charId`) REFERENCES `characters`(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;