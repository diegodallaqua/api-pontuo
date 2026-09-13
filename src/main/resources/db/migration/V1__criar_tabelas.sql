CREATE TABLE `knowledge_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `state` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `subject` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `knowledge_area_id` bigint(20) NOT NULL,
  `description` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_subject_knowledge_area` (`knowledge_area_id`),
  CONSTRAINT `fk_subject_knowledge_area` FOREIGN KEY (`knowledge_area_id`) REFERENCES `knowledge_area` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `subject_id` bigint(20) NOT NULL,
  `description` varchar(180) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_subject` (`subject_id`),
  CONSTRAINT `fk_topic_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_role_id` bigint(20) NOT NULL,
  `username` varchar(60) NOT NULL,
  `email` varchar(160) NOT NULL,
  `birth_date` date NOT NULL,
  `password` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_users_email` (`email`),
  UNIQUE KEY `uq_users_username` (`username`),
  KEY `idx_users_user_role` (`user_role_id`),
  CONSTRAINT `fk_users_user_role` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `city` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `state_id` bigint(20) DEFAULT NULL,
  `name` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_city_state` (`state_id`),
  CONSTRAINT `fk_city_state` FOREIGN KEY (`state_id`) REFERENCES `state` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `mock_exam` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `num_questions` smallint(6) NOT NULL,
  `max_time` smallint(6) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `started_at` datetime DEFAULT NULL,
  `finished_at` datetime DEFAULT NULL,
  `correct_count` smallint(6) DEFAULT NULL,
  `accuracy` decimal(5,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_mock_exam_user` (`user_id`),
  KEY `idx_mock_exam_user_finished` (`user_id`,`finished_at`),
  CONSTRAINT `fk_mock_exam_users` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city_id` bigint(20) NOT NULL,
  `street` varchar(180) NOT NULL,
  `neighborhood` varchar(120) NOT NULL,
  `number` int(11) NOT NULL,
  `complement` varchar(120) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_address_city` (`city_id`),
  CONSTRAINT `fk_address_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `institution` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address_id` bigint(20) NOT NULL,
  `name` varchar(160) NOT NULL,
  `acronym` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_institution_acronym` (`acronym`),
  KEY `idx_institution_address` (`address_id`),
  CONSTRAINT `fk_institution_address` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `entrance_exam` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) NOT NULL,
  `name` varchar(160) NOT NULL,
  `year` smallint(6) NOT NULL,
  `stage` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_entrance_exam` (`institution_id`,`year`,`name`),
  KEY `idx_entrance_exam_year` (`year`),
  CONSTRAINT `fk_entrance_exam_institution` FOREIGN KEY (`institution_id`) REFERENCES `institution` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `entrance_exam_id` bigint(20) NOT NULL,
  `topic_id` bigint(20) DEFAULT NULL,
  `subject_id` bigint(20) DEFAULT NULL,
  `statement` text NOT NULL,
  `explanation` text DEFAULT NULL,
  `difficulty` smallint(6) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_question_entrance_exam` (`entrance_exam_id`),
  KEY `idx_question_topic` (`topic_id`),
  KEY `idx_question_subject` (`subject_id`),
  CONSTRAINT `fk_question_entrance_exam` FOREIGN KEY (`entrance_exam_id`) REFERENCES `entrance_exam` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_question_subject` FOREIGN KEY (`subject_id`) REFERENCES `subject` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_question_topic` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `question_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NOT NULL,
  `url` varchar(500) NOT NULL,
  `subtitle` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_question_image_question` (`question_id`),
  CONSTRAINT `fk_question_image_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `answer_option` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question_id` bigint(20) NOT NULL,
  `letter` varchar(2) NOT NULL,
  `text` text NOT NULL,
  `right_answer` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_answer_option` (`question_id`,`letter`),
  KEY `idx_answer_option_question` (`question_id`),
  CONSTRAINT `fk_answer_option_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `mock_exam_has_question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `mock_exam_id` bigint(20) NOT NULL,
  `question_id` bigint(20) NOT NULL,
  `answer_option_id` bigint(20) DEFAULT NULL,
  `answered_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_mehq` (`mock_exam_id`,`question_id`),
  KEY `idx_mehq_question` (`question_id`),
  KEY `idx_mehq_answer_option` (`answer_option_id`),
  CONSTRAINT `fk_mehq_answer_option` FOREIGN KEY (`answer_option_id`) REFERENCES `answer_option` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_mehq_mock_exam` FOREIGN KEY (`mock_exam_id`) REFERENCES `mock_exam` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mehq_question` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
