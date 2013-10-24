DROP VIEW IF EXISTS view_active_users;
CREATE VIEW view_active_users AS
SELECT  
CONCAT_WS('-',
    CAST(DATE(sms_opt_in_date) AS CHAR),
    CAST(brand_id AS CHAR(10) ) 
)  
AS 'id', DATE(sms_opt_in_date) AS event_date, count(*) AS active_users, brand_id 
FROM subscriber WHERE STATUS = 'OPT_IN_COMPLETE' GROUP BY DATE(sms_opt_in_date), BRAND_id ORDER BY sms_opt_in_date;



SELECT * FROM view_active_users;
