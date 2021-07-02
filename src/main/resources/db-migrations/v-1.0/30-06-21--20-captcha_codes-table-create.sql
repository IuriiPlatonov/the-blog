CREATE TABLE `global_settings`
(
    `id`    int          NOT NULL AUTO_INCREMENT,
    `code`  varchar(255) NOT NULL,
    `name`  varchar(255) NOT NULL,
    `value` varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3