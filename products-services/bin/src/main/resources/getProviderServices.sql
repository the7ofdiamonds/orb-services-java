CREATE DEFINER=`root`@`%` PROCEDURE `getProviderServices`(IN p_id INT)
BEGIN
	SELECT services FROM providers WHERE id = p_id;
END