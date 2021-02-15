-- Clean out the data first
DELETE FROM reservation;
DELETE FROM site;
DELETE FROM campground;
DELETE FROM park;


-- test parks
INSERT INTO park (name, location, establish_date, area, visitors, description)
VALUES ('Park 1', 'Pennsylvania', '1/1/1970', 1024, 512, 'Test description 1')
DECLARE @parkId1 int = (SELECT @@IDENTITY);

INSERT INTO park (name, location, establish_date, area, visitors, description)
VALUES ('Park 2', 'Ohio', '1/1/1970', 2048, 1024, 'Test description 2')


-- test campgrounds
INSERT INTO campground(park_id, name, open_from_mm, open_to_mm, daily_fee)
VALUES (@parkId1, 'Test Campground', '1', '12', 35);
DECLARE @campgroundId int = (SELECT @@IDENTITY);

INSERT INTO campground(park_id, name, open_from_mm, open_to_mm, daily_fee)
VALUES (@parkId1, 'Test Campground', '1', '12', 35);


-- test sites
---- accepts RVs
INSERT INTO site(campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities)
VALUES (@campgroundId, 1, 10, 1, 33, 1);

INSERT INTO site(campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities)
VALUES (@campgroundId, 2, 10, 1, 30, 1);

---- doesn't accept RVs
INSERT INTO site(campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities)
VALUES (@campgroundId, 3, 10, 1, 0, 1);
DECLARE @siteId int = (SELECT @@IDENTITY);


-- test reservations
---- future
INSERT INTO reservation(site_id, name, from_date, to_date, create_date)
VALUES (@siteId, 'Test Testerson', GETDATE() + 1, GETDATE() + 5, GETDATE() - 23);

INSERT INTO reservation(site_id, name, from_date, to_date, create_date)
VALUES (@siteId, 'Bob Robertson', GETDATE() + 11, GETDATE() + 18, GETDATE() - 23);

---- present
INSERT INTO reservation(site_id, name, from_date, to_date, create_date)
VALUES (@siteId, 'Manager Managerson', GETDATE() - 5, GETDATE() + 2, GETDATE() - 23);

---- past
INSERT INTO reservation(site_id, name, from_date, to_date, create_date)
VALUES (@siteId, 'Leonard Leonardson', GETDATE() - 11, GETDATE() - 18, GETDATE() - 23);
DECLARE @reservationId int = (SELECT @@IDENTITY);


-- Return the Ids if needed
SELECT
	@parkId1 as parkId,
	--@campgroundId as campgroundId,
	@siteId as siteId,
	@reservationId as reservationId;
