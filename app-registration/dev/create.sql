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
    ('ABC20220809010203', 'test title', 'test url', 'test description',
        'test developer name', 'test email', 'test instituion', 'test lab', null, 'published')