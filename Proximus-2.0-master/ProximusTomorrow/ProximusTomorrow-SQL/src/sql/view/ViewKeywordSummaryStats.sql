DROP VIEW IF EXISTS view_keyword_total_opt_ins;
CREATE VIEW view_keyword_total_opt_ins AS
SELECT
CONCAT_WS('-',
    CAST(keyword_id AS CHAR(10)),
    CAST(DATE(sms_opt_in_date) AS CHAR)
    
)  as 'id', 
k.id AS keyword_id, 
count(s.keyword_id) AS 'total_opt_ins', 
DATE(registration_date) AS event_date, 
k.sourcetype_id, 
k.company_id FROM subscriber AS s 
RIGHT JOIN
keyword k
ON k.id = s.keyword_id
WHERE s.`status` = 'OPT_IN_COMPLETE' GROUP BY DATE(registration_date), s.keyword_id;


DROP VIEW IF EXISTS view_keyword_total_opt_ins_pending;
CREATE VIEW view_keyword_total_opt_ins_pending AS
SELECT
CONCAT_WS('-',
    CAST(keyword_id AS CHAR(10)),
    CAST(DATE(sms_opt_in_date) AS CHAR)
    
)  as 'id', 
k.id AS keyword_id, 
count(s.keyword_id) AS 'total_opt_ins_pending', 
DATE(registration_date) AS event_date, 
k.sourcetype_id, 
k.company_id FROM subscriber AS s 
RIGHT JOIN
keyword k
ON k.id = s.keyword_id
WHERE s.`status` = 'SMS_OPT_IN_PENDING' GROUP BY DATE(registration_date), s.keyword_id;



DROP VIEW IF EXISTS view_keyword_total_opt_outs;
CREATE VIEW view_keyword_total_opt_outs AS
SELECT
CONCAT_WS('-',
    CAST(keyword_id AS CHAR(10)),
    CAST(DATE(sms_opt_in_date) AS CHAR)
    
)  as 'id', 
k.id AS keyword_id, 
count(s.keyword_id) AS 'total_opt_outs', 
DATE(registration_date) AS event_date, 
k.sourcetype_id, 
k.company_id FROM subscriber AS s 
RIGHT JOIN
keyword k
ON k.id = s.keyword_id
WHERE s.`status` = 'OPT_OUT' GROUP BY DATE(registration_date), s.keyword_id;



/*

DROP VIEW IF EXISTS view_keyword_summary_stats;
CREATE VIEW view_keyword_summary_stats AS

#RUNNING THE KEYWORD SUMMARY
SELECT `one`.id, `one`.event_date, `one`.keyword_id, `one`.company_id, `one`.sourcetype_id, 
COALESCE(`one`.total_opt_ins, 0) AS total_opt_ins, 
COALESCE(`two`.total_opt_ins_pending,0) AS total_opt_ins_pending, 
COALESCE(`three`.total_opt_outs,0) AS total_opt_outs

FROM view_keyword_total_opt_ins_pending `two`
RIGHT OUTER JOIN
view_keyword_total_opt_outs `three`
ON `three`.id = `two`.id
RIGHT OUTER JOIN
view_keyword_total_opt_ins `one`
ON `one`.id = `two`.id;



select * from view_keyword_summary_stats;*/

select * from view_keyword_total_opt_ins;
select * from view_keyword_total_opt_ins_pending;
select * from view_keyword_total_opt_outs;

