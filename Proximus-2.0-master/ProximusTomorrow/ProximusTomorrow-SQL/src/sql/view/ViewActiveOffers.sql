#DROPPING IF IT EXISTS
DROP VIEW IF EXISTS view_active_offers;

#CREATING VIEW FOR ACTIVE OFFERS IN ALL PROPERTIES
CREATE VIEW view_active_offers AS
SELECT concat(p.web_hash,'-', cast(w.id AS CHAR(10) ) )  as 'id', w.id as web_offer_id, w.name as 'name', w.clean_offer_text, w.locale, w.start_date, w.end_date, p.web_hash, p.id as 'property_id', w.retailer_id

FROM property_retailer AS pr
RIGHT JOIN
property AS p
ON
p.id = pr.property_id
RIGHT JOIN
web_offer AS w
ON
w.retailer_id = pr.retailer_id
WHERE w.deleted = 0 AND w.status = 'Approved' and retail_only = 1 and date(now()) BETWEEN date(w.start_date) AND date(w.end_date)


UNION

SELECT concat(p.web_hash,'-', cast(w1.id AS CHAR(10) ) )  as 'id', w1.id as web_offer_id, w1.name as 'name', w1.clean_offer_text, w1.locale, w1.start_date, w1.end_date, p.web_hash, p.id as 'property_id', w1.retailer_id
FROM web_offer AS w1 
RIGHT JOIN
property_web_offer pw
ON
w1.id = pw.web_offer_id
RIGHT JOIN
property p
ON
p.id = pw.property_id
WHERE 
w1.status='Approved' AND w1.deleted=0 AND DATE(NOW()) BETWEEN DATE(w1.start_date) AND DATE(w1.end_date);


#SAMPLE EXAMPLES
/*
SELECT * from view_active_offers limit 1;
SELECT * from view_active_offers where property_id = 20;
*/

select * from view_active_offers;




