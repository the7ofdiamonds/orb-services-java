CREATE DEFINER=`root`@`%` PROCEDURE `getServices`()
BEGIN
SELECT * FROM services;
END