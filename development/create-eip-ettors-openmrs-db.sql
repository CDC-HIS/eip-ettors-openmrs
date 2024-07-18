CREATE DATABASE IF NOT EXISTS `eip_ettors_openmrs_mgt` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `eip_ettors_openmrs_mgt`;

CREATE USER 'eip_ettors_openmrs'@'localhost' IDENTIFIED BY 'password';
CREATE USER 'eip_ettors_openmrs'@'%' IDENTIFIED BY 'password';

GRANT ALL PRIVILEGES ON eip_ettors_openmrs_mgt.* TO 'eip_ettors_openmrs'@'localhost';
GRANT ALL PRIVILEGES ON eip_ettors_openmrs_mgt.* TO 'eip_ettors_openmrs'@'%';
FLUSH PRIVILEGES;
