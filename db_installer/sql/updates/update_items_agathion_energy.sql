-- Add agathion_energy column to items table for agathion energy system
ALTER TABLE `items` ADD COLUMN `agathion_energy` decimal(13) NOT NULL DEFAULT -1 AFTER `visual_item_id`;
