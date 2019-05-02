
CREATE TABLE `game` (
  `id` int(11) NOT NULL,
  `gametime` varchar(255) DEFAULT NULL
);

CREATE TABLE `game_score` (
  `game` int(11) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `player` int(11) NOT NULL
);

CREATE TABLE `kits` (
  `id` int(11) NOT NULL,
  `ipaddress` varchar(255) NOT NULL
);

CREATE TABLE `player` (
  `id` int(11) NOT NULL,
  `username` varchar(255) DEFAULT NULL,
  `kit` int(11) DEFAULT NULL,
  `game` int(11) DEFAULT NULL
);


ALTER TABLE `game`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `game_score`
  ADD PRIMARY KEY (`player`),
  ADD KEY `game` (`game`);

ALTER TABLE `kits`
  ADD PRIMARY KEY (`id`);

ALTER TABLE `player`
  ADD PRIMARY KEY (`id`),
  ADD KEY `kit` (`kit`),
  ADD KEY `game` (`game`);


ALTER TABLE `game`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
ALTER TABLE `player`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

ALTER TABLE `game_score`
  ADD CONSTRAINT `game_score_ibfk_1` FOREIGN KEY (`game`) REFERENCES `game` (`id`),
  ADD CONSTRAINT `game_score_ibfk_2` FOREIGN KEY (`player`) REFERENCES `player` (`id`);

ALTER TABLE `player`
  ADD CONSTRAINT `player_ibfk_1` FOREIGN KEY (`kit`) REFERENCES `kits` (`id`),
  ADD CONSTRAINT `player_ibfk_2` FOREIGN KEY (`game`) REFERENCES `game` (`id`);
