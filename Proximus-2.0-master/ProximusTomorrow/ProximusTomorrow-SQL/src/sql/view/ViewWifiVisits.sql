DROP VIEW IF EXISTS view_wifi_visits;
CREATE VIEW view_wifi_visits AS
SELECT id, date(event_date) AS event_date, mac_address, company_id, campaign_id, device_id FROM wifi_log
GROUP by date(event_date), mac_address, device_id, campaign_id;

select * from view_wifi_visits;

