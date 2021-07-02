CREATE TABLE `tag2post`
(
    `id`      int NOT NULL AUTO_INCREMENT,
    `post_id` int NOT NULL,
    `tag_id`  int NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FKpjoedhh4h917xf25el3odq20i` (`post_id`),
    KEY `FKjou6suf2w810t2u3l96uasw3r` (`tag_id`),
    CONSTRAINT `FKjou6suf2w810t2u3l96uasw3r` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`),
    CONSTRAINT `FKpjoedhh4h917xf25el3odq20i` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb3