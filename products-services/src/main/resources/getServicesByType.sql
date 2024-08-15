CREATE DEFINER=`root`@`%` PROCEDURE `getServicesByType`(IN p_type VARCHAR(45))
BEGIN
	SELECT * FROM services WHERE name = p_type;
END