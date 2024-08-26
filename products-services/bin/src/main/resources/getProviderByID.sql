CREATE DEFINER=`root`@`%` PROCEDURE `getProviderByID`(IN p_id VARCHAR(45))
BEGIN
	SELECT * FROM providers WHERE id = p_id;
END