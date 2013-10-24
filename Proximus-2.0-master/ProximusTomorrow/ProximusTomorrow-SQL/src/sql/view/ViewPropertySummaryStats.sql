#Subquery to get Messages Sent by Property
DROP VIEW IF EXISTS view_property_messages_sent;
CREATE VIEW view_property_messages_sent AS
    SELECT p.id as PROPERTY_id, p.`name` AS `name`, count(mosl.id) AS messages_sent
    FROM mobile_offer_send_log mosl
    RIGHT JOIN
    property p
    ON p.id = mosl.property_id AND mosl.`status` = 'Delivered'
    GROUP BY p.id;





#Subquery to get Opt Ins per Property
DROP VIEW IF EXISTS view_property_opt_ins;
CREATE VIEW view_property_opt_ins AS
SELECT p.id AS PROPERTY_id, p.`name`AS `name`, count(s.id) AS opt_ins
            FROM subscriber s
            RIGHT JOIN
            property p
            ON p.id = s.opt_in_property
            WHERE s.status = 'OPT_IN_COMPLETE'
            GROUP BY p.id;



#Subquery to get Number of Retailers per Property
DROP VIEW IF EXISTS view_property_total_retailers;
CREATE VIEW view_property_total_retailers AS
    SELECT p.id AS PROPERTY_id, p.name AS `name`, count(r.id) AS retailers
    FROM property p
    LEFT JOIN
    property_retailer pr
    ON pr.property_id = p.id
    LEFT JOIN
    retailer r
    ON pr.retailer_id = r.id
    GROUP BY p.id;



#Subquery to Number of Active Offers per Property
DROP VIEW IF EXISTS view_property_total_active_offers;
CREATE VIEW view_property_total_active_offers AS
    SELECT p.id AS PROPERTY_ID, p.`name` AS `name`, count(m.id) AS active_offers, count(DISTINCT m.retailer_id) AS retailers_with_offers
    FROM property p
    LEFT JOIN
    property_retailer pr
    ON pr.property_id = p.id
    LEFT JOIN
    retailer r
    ON pr.retailer_id = r.id
    LEFT JOIN
    mobile_offer m
    ON m.retailer_id = pr.retailer_id AND m.brand_id = 1 AND DATE(now()) BETWEEN DATE(m.start_date) AND DATE(m.end_date) AND m.expired = 0 AND m.`status` = 'Approved'
    GROUP BY p.id;
    
    

DROP VIEW IF EXISTS view_property_summary_stats;
CREATE VIEW view_property_summary_stats AS
#RUNNING THE PROPERTY SUMMARY
SELECT `one`.property_id,`one`.messages_sent, IFNULL(`two`.opt_ins,0) as opt_ins, `three`.`retailers`, `four`.active_offers, `four`.retailers_with_offers
FROM view_property_messages_sent `one`
LEFT OUTER JOIN
view_property_opt_ins `two`
ON `one`.property_id = `two`.property_id
RIGHT JOIN
view_property_total_retailers `three`
ON `one`.property_id = `three`.property_id
RIGHT JOIN
view_property_total_active_offers `four`
ON `one`.property_id = `four`.property_id;




/*
select * from view_property_messages_sent;
select * from view_property_opt_ins;
select * from view_property_total_retailers;
select * from view_property_total_active_offers;
*/