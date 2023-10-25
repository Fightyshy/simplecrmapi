USE `testcrmdb`;
drop table if exists role;
drop table if exists user;
drop table if exists user_roles;
create table role (id int not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
-- create table user (id int not null auto_increment, username varchar(20), password varchar(68), enabled boolean, employeeID int(11), primary key(id)) engine=InnoDB;
CREATE TABLE `user`(
	`id` int(11) NOT NULL AUTO_INCREMENT,
    `username` varchar(20),
    `password` varchar(68),
    `enabled` boolean,
    `employee_id` int(11),
    PRIMARY KEY(`id`),
    CONSTRAINT `FK_user_employee` FOREIGN KEY(`employee_id`)
	REFERENCES `employee`(`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

create table user_roles (user_id int not null, role_id int not null, primary key (user_id, role_id)) engine=InnoDB;


INSERT INTO role (id, name) VALUES (1, 'MANAGER');
INSERT INTO role (id, name) VALUES (2, 'EMPLOYEE');
INSERT INTO role (id, name) VALUES (3, 'ADMIN');

INSERT INTO user(id, username, password, enabled, employee_id) values(1,'employee1','$2a$12$W7BXRvm2jW4gXQZmz7ZHnuSPejHR8ucMknM5Gcg5FbfAFC9hRtAM6',true,1);
INSERT INTO user(id, username, password, enabled, employee_id) values(2,'employee2','$2a$12$W7BXRvm2jW4gXQZmz7ZHnuSPejHR8ucMknM5Gcg5FbfAFC9hRtAM6',true,2);
INSERT INTO user(id, username, password, enabled, employee_id) values(3, 'employee3','$2a$12$W7BXRvm2jW4gXQZmz7ZHnuSPejHR8ucMknM5Gcg5FbfAFC9hRtAM6', true,3);

INSERT INTO user_roles(user_id,role_id) values(1,1);
INSERT INTO user_roles(user_id,role_id) values(2,2);
INSERT INTO user_roles(user_id,role_id) values(3,3);