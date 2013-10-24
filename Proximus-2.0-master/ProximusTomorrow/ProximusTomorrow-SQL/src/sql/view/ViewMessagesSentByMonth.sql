DROP VIEW view_messages_sent_by_month;
CREATE VIEW view_messages_sent_by_month AS

SELECT
CONCAT_WS('-',
    CAST(p.id AS CHAR(10) ), 
    CAST(IFNULL(YEAR(mosl.event_date),'ye') AS CHAR),
    CAST(IFNULL(MONTH(mosl.event_date),'mo') AS CHAR)
)  as 'id', 
p.id as property_id, 
p.company_id AS company_id, 
count(mosl.id) AS messages_sent, 
IFNULL(YEAR(mosl.event_date),-1) AS `the_year`, 
IFNULL(MONTH(mosl.event_date),-1) AS `the_month`

    FROM mobile_offer_send_log mosl
    RIGHT JOIN
    property p
    ON p.id = mosl.property_id AND mosl.`status` = 'Delivered'
    GROUP BY p.id , YEAR(mosl.event_date), MONTH(mosl.event_date);
    
    
    
    select * from view_messages_sent_by_month;
    
    
    select * from view_messages_sent_by_month where company_id = 126 and month = MONTH(NOW());
    
    
    #EXAMPLE QUERY
    select * from view_messages_sent_by_month;
    
    
    #QUERY TO GET CURRENT MONTH AND PREVIOUS MONTH
    SELECT * FROM view_messages_sent_by_month WHERE
    (   MONTH(NOW()) = 1 AND
            (`month` = MONTH(NOW()) or `month` = 12)
    )
    OR
    
    (
        MONTH(now()) > 1 AND 
            (`month` = MONTH(NOW()) OR `month` = MONTH(NOW())-1) 
    );