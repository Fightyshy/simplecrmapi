USE `testcrmdb`;

SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE `employee_cases`;
TRUNCATE `employee`;
TRUNCATE `customer`;
TRUNCATE `address`;
TRUNCATE `social_media`;
TRUNCATE `cases`;
TRUNCATE `products`;
SET FOREIGN_KEY_CHECKS = 1;

-- social media insert
INSERT INTO `social_media` (pref_sm,facebook,twitter,instagram,line,whatsapp)
VALUES('TWITTER','NONE','@Tester','NONE','NONE','NONE'),
('NONE','NONE','NONE','NONE','NONE','NONE');

-- customer insert
INSERT INTO `customer` (first_name, middle_name, last_name, date_of_birth, phone_number, email_address, pref_comms, occupation, industry, social_media_id)
VALUES('Test','ing','ton','1994-04-13','123456789','test@tester.com','EMAIL','wageslave','retail','1');

-- employee insert
INSERT INTO `employee` (first_name, middle_name, last_name, date_of_birth, phone_number, email_address, cases_active, cases_pending, cases_resolved, cases_closed, social_media_id)
VALUES('employee','','erer','1995-12-19','987654321','tharonn@gmail.com','1','0','0','0','2'),
('employee2','two','ererer','1990-12-12','123987456','employee2@company.com','1','0','0','0',null),
('employee3','three','erest','1989-01-01','123456789','employee3@company.com','1','0','0','0',null);
-- Address insert
INSERT INTO `address` (type_of_address,line1,line2,line3,postcode,country,province,city,phone_number,fax, customer_id, employee_id)
VALUES('BUSINESS','test line 1','lorem ipsum yada','more street names','436914','United Testlands','teststate','testcity','123456789','123456789','1',null)
,('PERSONAL','different street','different bloc','different house number','419634','United Testlands','teststate','testcity','123456789','123456789','1',null)
,('BUSINESS','employee1 street','housing block 123','#12-12','123456','UnitedTestlands','teststate','testcity','987654321','987654321',null,'1')
,('BUSINESS','employee2 road','housing block 321','#32-12','654321','UnitedTestlands','teststate','testcity','93898159','93898159',null,'2');

-- Products insert
INSERT INTO `products` (`name`, `summary`)
VALUES('Test product','Test product description here');

-- Case insert
INSERT INTO `cases` (cases_status, start_date, end_date, summary, products_id, customer_id)
VALUES('ACTIVE',CURDATE(),null, 'test desc pls ignore 1','1','1');

-- join table insert

INSERT INTO `employee_cases` (employee_id, cases_id)
VALUES('1','1'),
('2','1');