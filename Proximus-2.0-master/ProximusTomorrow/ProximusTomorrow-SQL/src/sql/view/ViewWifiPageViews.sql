#THIS VIEW IS FOR SUCCESFULL PAGE VIEWS COUNT PER PAGE DAILY
#ALTER TABLE wifi_log ADD INDEX `wifi_log_view_indexing` (`http_status` ASC, `request_url` ASC);

DROP VIEW IF EXISTS view_wifi_page_views;

CREATE VIEW view_wifi_page_views AS
SELECT id, DATE(event_date) AS 'event_date', request_url, count(request_url) AS 'successful_page_views', company_id, campaign_id, device_id FROM wifi_log WHERE 
(request_url LIKE '%.html' OR request_url LIKE '%.php' OR request_url LIKE '%/') AND http_status = 200
AND os_group != 'Unknown' AND browser_group != 'CFNetwork' GROUP BY request_url, device_id, campaign_id,  company_id, DATE(event_date);


#Examples

/*
SELECT * FROM view_wifi_page_views order by company_id;
SELECT event_date, sum(successful_page_views) FROM view_wifi_page_views WHERE company_id = 1 group by event_date;
SELECT * FROM view_wifi_page_views WHERE company_id = 1;
SELECT * FROM view_wifi_page_views WHERE campaign_id = 4 AND company_id = 1 AND DATE(event_date) BETWEEN '2012-03-14' AND '2012-03-15';

#Example Successful Page Views per Request URL
SELECT request_url, sum(successful_page_views) FROM view_wifi_page_views WHERE company_id = 1 GROUP BY request_url;
SELECT request_url, sum(successful_page_views) FROM view_wifi_page_views WHERE campaign_id = 4 AND company_id = 1 AND DATE(event_date) BETWEEN '2012-03-14' AND '2012-03-15' GROUP BY request_url;

#Example Get Distinct Users (MAC ADDRESSES)
SELECT DISTINCT mac_address FROM wifi_log WHERE company_id = 1;
SELECT DISTINCT mac_address FROM wifi_log WHERE company_id = 1 AND campaign_id = 4 AND DATE(event_date) BETWEEN '2012-03-14' AND '2012-03-15'; 


select * from view_active_offers;

select * from wifi_log limit 2;
*/