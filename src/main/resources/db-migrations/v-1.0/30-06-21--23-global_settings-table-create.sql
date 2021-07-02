CREATE TABLE `captcha_codes`
(
    `id`          int         NOT NULL AUTO_INCREMENT,
    `code`        tinytext    NOT NULL,
    `secret_code` tinytext    NOT NULL,
    `time`        datetime(6) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3

