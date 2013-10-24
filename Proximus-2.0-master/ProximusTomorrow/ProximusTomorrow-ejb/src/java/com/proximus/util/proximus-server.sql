#drop schema proximusdenvercoder;
#create schema proximusdenvercoder;
#use proximusdenvercoder;
#Populate company, campaign, device and device_campaigns
INSERT INTO `company` VALUES (1,'THE EMPIRE');
insert into device (`mac_address`, `rotation`,`reconnect_interval`, `serial_number`, `keep_alive`, `wifi_channel`, `COMPANY_id`)
values
('AABBCCDDEEFF', 450000, 15000, '123123', 30000, 1,1), 
('F0AD4E00C7A5', 450000, 15000, '123123', 30000, 6,1),
('F0AD4E00CB4F', 450000, 15000, '123123', 30000, 1,1),
('000780016009', 450000, 30000, '123123', 30000, 9,1),
('B88D1200CFE2', 450000, 30000, '123123', 30000, 9,1); 
#If you want to put a token uncomment next line
#update device set token = 'e1db2df9-ac7f-4e4c-955f-245541401f74' where id = 1;
insert into campaign(`active`, `days_of_week`, `end_date`, `end_time`, `last_modified`, `name`, `start_date`, `start_time`)
values(1, 'M,T,W,R,F,S,U', '2012-05-30', '23:59', now(), 'MGM','2012-01-01', '00:00'),
      (2, 'M,T,W,R,F,S,U', '2012-04-30', '23:59', now(), 'Macys','2012-01-01', '00:00'),
      (3, 'M,T,W,R,F,S,U', '2012-03-30', '23:59', now(), 'Star Wars', '2012-01-01', '00:00');
      
      
insert into `campaign_type`(`TYPE`, `checksum`, `CAMPAIGN_id`, `bluetooth_mode`, `friendly_name`, `hot_spot_mode`, `network_name`)
values
('wifi', '123wifi', 1, null, null, 1, 'MGM-wifi'),
('bluetooth', '123456', 1, 1, 'MGM-bt-images', null, null),
('wifi', '456123', 2, null, null, 3, 'MACYS-wifi'),
('wifi', '7777', 3, null, null, 2, 'Macy Open wifi'),
('bluetooth', '778899', 3, 1, 'DarthVader', null, null);

INSERT INTO `campaign_file` VALUES
(1, '/home/proximus/server/campaigns/1/wifi/mgm.zip', 1),
(2, '/home/proximus/server/campaigns/1/bluetooth/vader.zip', 2),
(3, '/home/proximus/server/campaigns/2/wifi/macys.zip', 3),
(4, '/home/proximus/server/campaigns/3/wifi/Caesars.zip', 4),
(5, '/home/proximus/server/campaigns/3/bluetooth/linux-logo.zip', 5);



INSERT INTO `campaign_device` VALUES (1,1),(2,1),(3,1),(1,2),(2,2),(3,2),(1,3),(1,4),(1,5),(2,4),(2,5),(3,4),(3,5);

INSERT INTO `hot_spot_domain` (`domain_name`, `CAMPAIGNTYPE_id`)
values
('www.xkcd.com', 4),
('www.espn.com', 4);

