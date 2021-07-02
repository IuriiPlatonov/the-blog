CREATE TABLE `post_votes`
(
    `id`      int         NOT NULL AUTO_INCREMENT,
    `post_id` int         NOT NULL,
    `time`    datetime(6) NOT NULL,
    `value`   tinyint     NOT NULL,
    `user_id` int         NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK9q09ho9p8fmo6rcysnci8rocc` (`user_id`),
    CONSTRAINT `FK9q09ho9p8fmo6rcysnci8rocc` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci