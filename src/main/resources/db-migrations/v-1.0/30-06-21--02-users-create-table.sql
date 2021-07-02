CREATE TABLE `users`
(
    `id`           int          NOT NULL AUTO_INCREMENT,
    `code`         varchar(255) DEFAULT NULL,
    `email`        varchar(255) NOT NULL,
    `is_moderator` tinyint      NOT NULL,
    `name`         varchar(255) NOT NULL,
    `password`     varchar(255) NOT NULL,
    `photo`        text,
    `reg_time`     datetime(6)  NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3
