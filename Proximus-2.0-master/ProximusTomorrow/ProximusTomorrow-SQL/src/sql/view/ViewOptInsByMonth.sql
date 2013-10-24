#GET Opt Ins per Property by Month
DROP VIEW IF EXISTS view_opt_ins_by_month;
CREATE VIEW view_opt_ins_by_month AS

SELECT
CONCAT_WS('-',
    CAST(p.id AS CHAR(10) ), 
    CAST(IFNULL(YEAR(s.sms_opt_in_date),'ye') AS CHAR),
    CAST(IFNULL(MONTH(s.sms_opt_in_date),'mo') AS CHAR)
)  as 'id', 
p.id AS PROPERTY_id,
p.company_id AS company_id, count(s.id) AS opt_ins, IFNULL(YEAR(s.sms_opt_in_date),-1) AS the_year, IFNULL(MONTH(s.sms_opt_in_date),-1) AS the_month
            FROM subscriber s
            RIGHT JOIN
            property p
            ON p.id = s.opt_in_property
            GROUP BY p.id , YEAR(s.sms_opt_in_date), MONTH(s.sms_opt_in_date);


select * from view_opt_ins_by_month;