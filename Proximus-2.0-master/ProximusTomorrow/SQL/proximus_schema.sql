DROP TABLE IF EXISTS device;
create table device
(
	id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
	nickname VARCHAR(255),
	serial_number VARCHAR(255) NOT NULL,
	mac_address1 VARCHAR(12) NOT NULL,
	company_id BIGINT(20) DEFAULT NULL,
	wifi_channel INT DEFAULT 9,
	last_ip_address VARCHAR(15) NULL,
		secret VARCHAR(255) NULL,
		dtype VARCHAR(255) NULL
);

DROP TABLE IF EXISTS device_dreamplug;
create table device_dreamplug
(
	id BIGINT(20) PRIMARY KEY NOT NULL,
	mac_address2 VARCHAR(255) NOT NULL
);

DROP TABLE IF EXISTS company;
create table company
(
	id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	primary_contact VARCHAR(255),
	address1 VARCHAR(255),
	address2 VARCHAR(255),
	city VARCHAR(255),
	state VARCHAR(255), 
	postal_code VARCHAR(11),
	country VARCHAR(255),
	logo_path VARCHAR(1024),
	billing_frequency_id INT,
	billing_type_id INT
);

DROP  VIEW IF EXISTS device_dreamplug_view;
create view device_dreamplug_view
AS
   SELECT d.id, d.nickname, d.serial_number, d.mac_address1, dp.mac_address2, c.id as company_id, c.name as company_name
   FROM device d, device_dreamplug dp, company c
   WHERE d.id = dp.device_id
   AND d.company_id = c.id;

DROP TABLE IF EXISTS campaign_type;
create table campaign_type
(
	id BIGINT(20) PRIMARY KEY NOT NULL,
	description VARCHAR(255) NOT NULL
);

INSERT INTO campaign_type ( id, description ) VALUES (1, 'Wi-Fi');
INSERT INTO campaign_type ( id, description ) VALUES (2, 'Bluetooth');
INSERT INTO campaign_type ( id, description ) VALUES (3, 'Video');

DROP TABLE IF EXISTS campaign;
create table campaign
(
	id BIGINT(20) PRIMARY KEY NOT NULL AUTO_INCREMENT,
        campaign_name VARCHAR(255) NOT NULL,
        company_id BIGINT(20) NOT NULL,
	start_date DATE NOT NULL,
	end_date DATE NOT NULL,
	days_of_week VARCHAR(7),
	start_time VARCHAR(255),
	stop_time VARCHAR(255),
	file_name VARCHAR(255),
	file_path VARCHAR(1024),
	dtype VARCHAR(255) NULL
);

DROP TABLE IF EXISTS campaign_bluetooth;
create table campaign_bluetooth
(
	id BIGINT(20) PRIMARY KEY NOT NULL,
	bt_campaign_prompt VARCHAR(255),
	mime_type VARCHAR(255)
);

DROP TABLE IF EXISTS campaign_wifi;
create table campaign_wifi
( 
	id BIGINT(20) PRIMARY KEY NOT NULL,
	wifi_ssid VARCHAR(255) NOT NULL
);
