CREATE TABLE `post_comments`
(
    `id`        int         NOT NULL AUTO_INCREMENT,
    `post_id`   int         NOT NULL,
    `text`      text        NOT NULL,
    `time`      datetime(6) NOT NULL,
    `parent_id` int DEFAULT NULL,
    `user_id`   int         NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKc3b7s6wypcsvua2ycn4o1lv2c` (`parent_id`),
    KEY `FKsnxoecngu89u3fh4wdrgf0f2g` (`user_id`),
    CONSTRAINT `FKc3b7s6wypcsvua2ycn4o1lv2c` FOREIGN KEY (`parent_id`) REFERENCES `post_comments` (`id`),
    CONSTRAINT `FKsnxoecngu89u3fh4wdrgf0f2g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci