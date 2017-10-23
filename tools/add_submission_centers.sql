-- this script adds submission centers to the database
SET @center_name = 'Johns Hopkins University';
SELECT MAX(id)+1 into @id FROM dashboard_entity;
INSERT INTO dashboard_entity (id, displayName) VALUES ( @id, @center_name );
INSERT INTO submission_center (id) VALUES (@id);

SET @center_name = 'Oregon Health and Science University';
SELECT MAX(id)+1 into @id FROM dashboard_entity;
INSERT INTO dashboard_entity (id, displayName) VALUES ( @id, @center_name );
INSERT INTO submission_center (id) VALUES (@id);

SET @center_name = 'University of California San Diego';
SELECT MAX(id)+1 into @id FROM dashboard_entity;
INSERT INTO dashboard_entity (id, displayName) VALUES ( @id, @center_name );
INSERT INTO submission_center (id) VALUES (@id);
