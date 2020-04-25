DROP TABLE IF EXISTS employee;

CREATE TABLE employee
(
    id       INT NOT NULL AUTO_INCREMENT,
    name     VARCHAR(512) NOT NULL,
    login    VARCHAR(100) NOT NULL,
    password VARCHAR(200) NOT NULL,
    born     DATETIME NOT NULL,
    position VARCHAR(200) NOT NULL,
    role     VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY login_idx (login)
);