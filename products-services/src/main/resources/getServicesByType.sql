CREATE DEFINER=`root`@`%` PROCEDURE `getServicesByType`(IN p_type VARCHAR(45))
BEGIN
	SELECT * FROM services WHERE type = p_type;
END