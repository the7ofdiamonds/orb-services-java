CREATE DEFINER=`root`@`%` PROCEDURE `getService`(IN p_id VARCHAR(255))
BEGIN
SELECT * FROM services WHERE id = p_id;
END