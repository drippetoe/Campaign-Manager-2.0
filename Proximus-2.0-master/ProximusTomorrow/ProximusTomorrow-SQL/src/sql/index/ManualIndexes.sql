
# location_data
alter table location_data add index loc_data_idx1 ( msisdn, event_date );
alter table location_data add index loc_data_idx2 ( COMPANY_id, event_date, status );
alter table location_data add index loc_data_idx3 ( COMPANY_id, msisdn, status );
alter table location_data ADD INDEX loc_data_idx4 ( BRAND_id, event_date, status );

# license
alter table license add index license_idx1 ( license_text );
alter table company add index company_idx1 ( LICENSE_id );

# mobile_offer_send_log
alter table mobile_offer_send_log add index mobile_offer_send_log_idx1 ( COMPANY_id, event_date );

# roles and privileges
alter table privilege_role add index privilege_role_idx1 ( privilege_id, role_id );

# Geofence SMS keywords
alter table keyword ADD UNIQUE INDEX keyword_shortcode_idx (keyword, SHORTCODE_id);

# keyword summary
alter table keyword_registration_summary ADD INDEX keyword_registration_summary_idx (COMPANY_id, KEYWORD_id);

# shell command action
alter table shell_command_action ADD INDEX shell_command_action_idx (completed, device_id);

# subscriber
alter table subscriber ADD INDEX subscriber_idx (BRAND_id, status);

# geopoint / geofence
alter table geo_point ADD INDEX geo_point_idx (lat, lng);
alter table geo_fence ADD INDEX geo_fence_company_idx (COMPANY_id);

# locale
alter table locale ADD UNIQUE INDEX locale_lc_idx (language_code);
