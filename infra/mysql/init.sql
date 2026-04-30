CREATE TABLE `applications` (
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `token`       VARCHAR(36) NOT NULL,
    `name`        VARCHAR(255) NOT NULL,
    `chats_count` INT UNSIGNED NOT NULL DEFAULT 0,
    `created_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
ALTER TABLE `applications`
    ADD UNIQUE `applications_token_unique` (`token`);

CREATE TABLE `chats` (
    `id`             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `application_id` BIGINT UNSIGNED NOT NULL,
    `number`         INT UNSIGNED NOT NULL,
    `messages_count` INT UNSIGNED NOT NULL DEFAULT 0,
    `created_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
ALTER TABLE `chats`
    ADD UNIQUE `chats_application_id_number_unique` (`application_id`, `number`);
ALTER TABLE `chats`
    ADD CONSTRAINT `chats_application_id_foreign`
    FOREIGN KEY (`application_id`) REFERENCES `applications` (`id`);

CREATE TABLE `messages` (
    `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chat_id`    BIGINT UNSIGNED NOT NULL,
    `number`     INT UNSIGNED NOT NULL,
    `body`       TEXT NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
ALTER TABLE `messages`
    ADD UNIQUE `messages_chat_id_number_unique` (`chat_id`, `number`);
ALTER TABLE `messages`
    ADD CONSTRAINT `messages_chat_id_foreign`
    FOREIGN KEY (`chat_id`) REFERENCES `chats` (`id`);