USE `qsystem`;

-- -----------------------------------------------------
-- Table `qsystem`.`services`
-- -----------------------------------------------------

ALTER TABLE `services` ADD `point` INT NOT NULL DEFAULT 0 COMMENT 'указание для какого пункта регистрации услуга, 0-для всех, х-для киоска х.';

COMMIT;
-- -----------------------------------------------------
-- Table `qsystem`.`net`
-- -----------------------------------------------------
UPDATE net SET version = '1.6' where id<>-1;

COMMIT;
