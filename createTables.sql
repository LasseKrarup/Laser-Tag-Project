CREATE TABLE IF NOT EXISTS `game`(
    `id` INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `gametime` VARCHAR(255) DEFAULT NULL
);
CREATE TABLE IF NOT EXISTS `kits`(
    `id` INT(11) PRIMARY KEY NOT NULL,
    `ipaddress` VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS `player`(
    `id` INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    `username` VARCHAR(255) NOT NULL,
    `kit` INT(11) NOT NULL,
    `game` INT(11) NOT NULL,
    CONSTRAINT `player_kit_id` FOREIGN KEY(`kit`) REFERENCES `kits`(`id`),
    CONSTRAINT `player_game_id` FOREIGN KEY(`game`) REFERENCES `game`(`id`)
);
CREATE TABLE IF NOT EXISTS `game_score`(
    `game` INT(11) NOT NULL,
    `score` INT(11) DEFAULT NULL,
    `player` INT(11) PRIMARY KEY NOT NULL,
    CONSTRAINT `score_game_id` FOREIGN KEY(`game`) REFERENCES `game`(`id`),
    CONSTRAINT `score_player_id` FOREIGN KEY(`player`) REFERENCES `player`(`id`)
    
);