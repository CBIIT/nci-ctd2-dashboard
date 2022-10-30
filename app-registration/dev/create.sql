-- create test data
DROP TABLE IF EXISTS registration;
CREATE TABLE registration
(
    app_code CHAR(30) PRIMARY KEY,
    title CHAR(30) NOT NULL,
    url CHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    developers CHAR(100) NOT NULL,
    email CHAR(50) NOT NULL,
    institution CHAR(50) NOT NULL,
    lab CHAR(100),
    image MEDIUMBLOB,
    status CHAR(30)
);

INSERT INTO registration
VALUES
    ('CTD20220809010203', 'CTD² API Demo Application', 'http://34.74.93.164:3000/',
    'This demo application uses MolePro Translator to find gene-compound connections, and then uses the CTD² Dashboard API to construct a listing of relevant Dashboard results.',
    'Floratos, Ji, et al', 'ctd2@cumc.columbia.edu', 'Columbia University', 'Floratos Lab', null, 'published')