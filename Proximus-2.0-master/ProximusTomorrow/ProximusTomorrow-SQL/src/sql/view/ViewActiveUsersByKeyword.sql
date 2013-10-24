DROP VIEW IF EXISTS view_active_users_by_keyword;
CREATE VIEW view_active_users_by_keyword AS
SELECT  
CONCAT_WS('-',
    CAST(DATE(sms_opt_in_date) AS CHAR),
    CAST(brand_id AS CHAR(10) ), 
    CAST(keyword_id AS CHAR(10))
)  as 'id', 
DATE(sms_opt_in_date) AS event_date, count(*) AS active_users, brand_id, keyword_id
FROM subscriber WHERE STATUS = 'OPT_IN_COMPLETE' GROUP BY keyword_id, DATE(sms_opt_in_date), BRAND_id ORDER BY sms_opt_in_date;


SELECT * FROM view_active_users_by_keyword;