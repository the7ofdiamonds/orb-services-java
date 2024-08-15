CREATE DEFINER=`root`@`%` PROCEDURE `getServiceByID`(IN p_id VARCHAR(45))
BEGIN
	SELECT * FROM services WHERE id = p_id;
END