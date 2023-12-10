# simplecrmapi
Simple CRM api server component, Spring Boot/JPA

Personal project done applying skills learned regarding Spring and Spring boot. This is the APi server component, which uses JPA as a database driver to perform CRUD actions on database entries. As a demonstration/portfolio project, it has the following features:
- User handling, including login, logout, and password resetting via email
- CRUD functionality on customers and their details
- CRUD functionality on products and their details
- CRUD functionality on customer "cases" (troubleshooting, orders, etc), with dates, states, and assigned employees
- Appropriate database relationship handling for
  - Users being associated with cases
  - Customers being associated with cases
  - Cases having an associated product
 
application.properties containing jwt, port and database configurations not included but available on request. SQL generator and populator scripts are available.

### Frameworks and supporting tools used
- Spring Boot
- MySQL Database
- JPA (Java Persistence API) with Spring Data
- JWT (Json Web Tokens)
- Hibernate Validator

"Comsumer" client can be found at [https://github.com/Fightyshy/simplerestapiconsumer](https://github.com/Fightyshy/simplerestapiconsumer)
