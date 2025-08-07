--
-- Test Database initialization
--
CREATE DATABASE IF NOT EXISTS `medilabo_test`;
USE `medilabo_test`;

--
-- Table structure for table `patients`
--
DROP TABLE IF EXISTS `patients`;
CREATE TABLE `patients`
(
    `id`           int         NOT NULL AUTO_INCREMENT,
    `lastname`     varchar(45) NOT NULL,
    `firstname`    varchar(45) NOT NULL,
    `birthdate`    date        NOT NULL,
    `gender`       varchar(1)  NOT NULL,
    `address`      varchar(255) DEFAULT NULL,
    `phone_number` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
);

--
-- Data insertion
--
INSERT INTO `patients` (`firstname`, `lastname`, `birthdate`, `gender`, `address`, `phone_number`)
VALUES ('Test', 'TestNone', '1966-12-31', 'F', '1 Brookside St', '100-222-3333'),
       ('Test', 'TestBorderline', '1945-06-24', 'M', '2 High St', '200-333-4444'),
       ('Test', 'TestInDanger', '2004-06-18', 'M', '3 Club Road', '300-444-5555'),
       ('Test', 'TestEarlyOnset', '2002-06-28', 'F', '4 Valley Dr', '400-555-6666');