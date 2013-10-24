DROP VIEW IF EXISTS view_total_messages_sent;
CREATE VIEW view_total_messages_sent AS
SELECT 
CONCAT(CAST(DATE(event_date) AS CHAR),'-', CAST(company_id AS CHAR(10) ) )  AS 'id',  DATE(event_date) as event_date, count(*) as total_messages, company_id FROM mobile_offer_send_log WHERE STATUS = 'Delivered' GROUP BY DATE(event_date), company_id;

select * from view_total_messages_sent;
