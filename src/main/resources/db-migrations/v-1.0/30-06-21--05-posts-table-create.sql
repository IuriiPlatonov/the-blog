CREATE TABLE `posts`
(
    `id`                int                                NOT NULL AUTO_INCREMENT,
    `is_active`         tinyint                            NOT NULL,
    `moderation_status` enum ('NEW','ACCEPTED','DECLINED') NOT NULL DEFAULT 'NEW',
    `text`              text                               NOT NULL,
    `time`              datetime(6)                        NOT NULL,
    `title`             varchar(255)                       NOT NULL,
    `view_count`        int                                NOT NULL,
    `moderator_id`      int                                         DEFAULT NULL,
    `user_id`           int                                NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK6m7nr3iwh1auer2hk7rd05riw` (`moderator_id`),
    KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
    CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `FK6m7nr3iwh1auer2hk7rd05riw` FOREIGN KEY (`moderator_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci