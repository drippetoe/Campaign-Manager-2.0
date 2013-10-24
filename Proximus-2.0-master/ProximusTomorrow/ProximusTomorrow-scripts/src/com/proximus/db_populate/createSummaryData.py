#!/usr/bin/env python

import sys, os, random, math, datetime

COMPANY_ID = 1
CAMPAIGN_ID = 2
DAYS = 120

outf = open("sample_summary_data.sql", "w")

def getRandomVal(min, max):
    real_max = max - min
    randomNum = (random.random() * real_max) + min
    return int(round(randomNum))

def getDates(days):
    today = datetime.date.today()
    one_day = datetime.timedelta(days=1)
    current = today - ( days * one_day )
    dateRange = []
    dateRange.append(current)
    while ( current != today):
        current = current + one_day
        dateRange.append(current)

    return dateRange

def formatDate(date):
    return date.strftime("%Y-%m-%d")

def generateWifiSummaryRecord(event_date, campaign_id, company_id):
    unique_users = getRandomVal(1, 5)
    total_requests = getRandomVal(5*unique_users, 20*unique_users)
    total_page_views = getRandomVal(total_requests/10, total_requests/3)
    successful_page_views = getRandomVal(total_page_views/10, total_page_views/3)

    outf.write("""insert into summary_wifi_daily (event_date, successful_page_views, total_page_views, total_requests, unique_users, CAMPAIGN_id, COMPANY_id) 
        values ( '%s', %d, %d, %d, %d, %d, %d);\n""" % ( formatDate(event_date), successful_page_views, total_page_views, total_requests, unique_users, campaign_id, company_id )) 
        
def generateBluetoothSummaryRecord(event_date, campaign_id, company_id):
    total_devices_seen = getRandomVal(3, 10)
    unique_devices_seen = getRandomVal(1, 7)
    unique_devices_supporting_bluetooth = getRandomVal(unique_devices_seen/10, unique_devices_seen/2)
    unique_devices_accepting_push = getRandomVal(unique_devices_supporting_bluetooth/8, unique_devices_supporting_bluetooth/2)
    unique_devices_downloading_content = getRandomVal(unique_devices_accepting_push/6, unique_devices_accepting_push)
    total_content_downloads = unique_devices_downloading_content * getRandomVal(1, 5)

    outf.write("""insert into summary_bluetooth_daily (total_devices_seen, unique_devices_seen, unique_devices_accepting_push, unique_devices_downloading_content, unique_devices_supporting_bluetooth, total_content_downloads, event_date, CAMPAIGN_id, COMPANY_id) 
     values ( %d, %d, %d, %d, %d, %d, '%s', %d, %d);\n""" % ( total_devices_seen, unique_devices_seen, unique_devices_accepting_push, unique_devices_downloading_content, unique_devices_supporting_bluetooth, total_content_downloads, formatDate(event_date), campaign_id, company_id ))


if __name__ == "__main__":
    dates = getDates(DAYS)
    for event_date in dates:
        generateWifiSummaryRecord(event_date, CAMPAIGN_ID, COMPANY_ID)
    for event_date in dates:
        generateBluetoothSummaryRecord(event_date, CAMPAIGN_ID, COMPANY_ID)
