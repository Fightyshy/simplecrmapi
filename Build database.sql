USE `testcrmdb`;
SET foreign_key_checks = 0;
DROP TABLE IF EXISTS `employee_cases`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `employee`;
DROP TABLE IF EXISTS `cases`;
DROP TABLE IF EXISTS `address`;
DROP TABLE IF EXISTS `social_media`;
DROP TABLE IF EXISTS `products`;
SET foreign_key_checks = 1;

 CREATE TABLE `social_media`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `pref_sm` char(9) DEFAULT NULL,
    `facebook` varchar(20) DEFAULT NULL,
    `twitter` varchar(20) DEFAULT NULL,
    `instagram` varchar(20) DEFAULT NULL,
    `line` varchar(20) DEFAULT NULL,
    `whatsapp` varchar(20) DEFAULT NULL,
    PRIMARY KEY(`id`)
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
 
 CREATE TABLE `products`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(20) DEFAULT NULL,
    `summary` varchar(500) DEFAULT NULL,
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
    /*
CREATE TABLE `address`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `type_of_address` char(8) DEFAULT NULL,
    `line1` varchar(50) DEFAULT NULL,
    `line2` varchar(50) DEFAULT NULL,
    `line3` varchar(50) DEFAULT NULL,
    `postcode` varchar(10) DEFAULT NULL,
    `country` varchar(30) DEFAULT NULL,
    `province` varchar(30) DEFAULT NULL,
    `city` varchar(30) DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `fax` int(14) DEFAULT NULL, 
    PRIMARY KEY(`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
    
CREATE TABLE `customer`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `first_name` char(45) DEFAULT NULL,
    `middle_name` char(45) DEFAULT NULL,
    `last_name` char(45) DEFAULT NULL,
    `date_of_birth` date DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `email_address` varchar (30) DEFAULT NULL,
    `pref_comms` char(11) DEFAULT NULL,
    `occupation` char(20) DEFAULT NULL,
    `industry` char(20) DEFAULT NULL,
    `social_media_id` int(11) DEFAULT NULL,
    `address_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_detail_social_media` FOREIGN KEY (`social_media_id`)
    REFERENCES `social_media`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_detail_address` FOREIGN KEY(`address_id`)
    REFERENCES `address`(`id`) ON DELETE  NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

 CREATE TABLE `employee`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `first_name` char(45) DEFAULT NULL,
    `middle_name` char(45) DEFAULT NULL,
    `last_name` char(45) DEFAULT NULL,
    `date_of_birth` date DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `email_address` varchar (30) DEFAULT NULL,
    `cases_active` int(5) DEFAULT NULL,
    `cases_pending` int(5) DEFAULT NULL,
    `cases_resolved` int(5) DEFAULT NULL,
    `cases_closed` int(5) DEFAULT NULL,
    `social_media_id` int(11) DEFAULT NULL,
    `address_id` int(11) DEFAULT NULL,
    PRIMARY KEY(`id`),
    CONSTRAINT `FK_employee_social_media_id` FOREIGN KEY (`social_media_id`)
    REFERENCES `social_media`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT `FK_employee_address_id` FOREIGN KEY(`address_id`)
    REFERENCES `address`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
    */

CREATE TABLE `customer`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `first_name` char(45) DEFAULT NULL,
    `middle_name` char(45) DEFAULT NULL,
    `last_name` char(45) DEFAULT NULL,
    `date_of_birth` date DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `email_address` varchar (30) DEFAULT NULL,
    `pref_comms` char(11) DEFAULT NULL,
    `occupation` char(20) DEFAULT NULL,
    `industry` char(20) DEFAULT NULL,
    `social_media_id` int(11) DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `FK_detail_social_media` FOREIGN KEY (`social_media_id`)
    REFERENCES `social_media`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

 CREATE TABLE `employee`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `first_name` char(45) DEFAULT NULL,
    `middle_name` char(45) DEFAULT NULL,
    `last_name` char(45) DEFAULT NULL,
    `date_of_birth` date DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `email_address` varchar (30) DEFAULT NULL,
    `cases_active` int(5) DEFAULT NULL,
    `cases_pending` int(5) DEFAULT NULL,
    `cases_resolved` int(5) DEFAULT NULL,
    `cases_closed` int(5) DEFAULT NULL,
    `social_media_id` int(11) DEFAULT NULL,
    PRIMARY KEY(`id`),
    CONSTRAINT `FK_employee_social_media_id` FOREIGN KEY (`social_media_id`)
    REFERENCES `social_media`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

CREATE TABLE `address`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `type_of_address` char(8) DEFAULT NULL,
    `line1` varchar(50) DEFAULT NULL,
    `line2` varchar(50) DEFAULT NULL,
    `line3` varchar(50) DEFAULT NULL,
    `postcode` varchar(10) DEFAULT NULL,
    `country` varchar(30) DEFAULT NULL,
    `province` varchar(30) DEFAULT NULL,
    `city` varchar(30) DEFAULT NULL,
    `phone_number` varchar(14) DEFAULT NULL,
    `fax` int(14) DEFAULT NULL,
    `customer_id` int(11) DEFAULT NULL,
    `employee_id`int(11) DEFAULT NULL,
    PRIMARY KEY(`id`),
    CONSTRAINT `FK_address_customer` FOREIGN KEY (`customer_id`)
    REFERENCES `customer`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_address_employee` FOREIGN KEY (`employee_id`)
    REFERENCES `employee`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

 CREATE TABLE `cases`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `cases_status` varchar(8) DEFAULT NULL,
    `start_date` timestamp DEFAULT NULL,
    `end_date` timestamp DEFAULT NULL,
    `summary` varchar(500) DEFAULT NULL,
	`products_id` int(11) DEFAULT NULL,
    `customer_id` int(11) DEFAULT NULL,
    PRIMARY KEY(`id`),
    
	CONSTRAINT `FK_cases_customer` FOREIGN KEY (`customer_id`)
    REFERENCES `customer`(`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `FK_cases_products` FOREIGN KEY (`products_id`)
    REFERENCES `products`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
    /*
    CONSTRAINT `FK_cases_employee` FOREIGN KEY (`employee_id`)
    REFERENCES `employee`(`id`) ON DELETE NO ACTION ON UPDATE NO ACTION*/
 ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
 
 CREATE TABLE `employee_cases`(
	`employee_id` int(11) NOT NULL,
    `cases_id` int(11) NOT NULL,
    
      -- KEY `FK_ECC_idx` (`cases_id`),
  
	  -- CONSTRAINT `FK_EMPLOYEE_ECC` FOREIGN KEY (`employee_id`) 
	  -- REFERENCES `employee` (`id`) 
	 -- ON DELETE NO ACTION ON UPDATE NO ACTION
    
     PRIMARY KEY(`employee_id`, `cases_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;