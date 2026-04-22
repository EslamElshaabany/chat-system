CREATE TABLE `applications`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `token` VARCHAR(255) NOT NULL,
    `name` VARCHAR(255) NOT NULL,
    `chats_count` INT NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE `applications`
	ADD UNIQUE `applications_token_unique`(`token`);

CREATE TABLE `messages`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `chat_id` BIGINT NOT NULL,
    `number` INT NOT NULL,
    `body` TEXT NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL,
);
ALTER TABLE `messages` 
	ADD UNIQUE `messages_chat_id_number_unique`(`chat_id`, `number`);

CREATE TABLE `chats`(
    `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `application_id` BIGINT NOT NULL,
    `number` INT NOT NULL,
    `messages_count` INT NOT NULL,
    `created_at` TIMESTAMP NOT NULL,
    `updated_at` TIMESTAMP NOT NULL
);
ALTER TABLE `chats`
	ADD UNIQUE `chats_application_id_number_unique`(`application_id`, `number`);
ALTER TABLE `chats`
	ADD CONSTRAINT `chats_application_id_foreign` FOREIGN KEY(`application_id`) REFERENCES `applications`(`id`);
ALTER TABLE `messages`
	ADD CONSTRAINT `messages_chat_id_foreign` FOREIGN KEY(`chat_id`) REFERENCES `chats`(`id`);