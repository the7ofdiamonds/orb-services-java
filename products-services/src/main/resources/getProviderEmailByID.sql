CREATE DEFINER=`root`@`%` PROCEDURE `getEmailByProviderID`(IN p_id INT)
BEGIN
SELECT * FROM provider_email WHERE provider_id = p_id;
END