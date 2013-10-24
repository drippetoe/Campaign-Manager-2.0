import logging

from apscheduler.scheduler import Scheduler
from proximus.interfaces.Observable import Observable
from proximus.cron.WifiActiveCampaign import WifiActiveCampaignCron
from proximus.cron.BluetoothActiveCampaign import BluetoothActiveCampaignCron
from proximus.cron.LogRotation import LogRotation
from proximus.cron.StatusAPI import StatusAPI

logger = logging.root

class ClientScheduler(Observable):
    def __init__(self):
        global DeviceClientInstance
        
        self.scheduler = Scheduler()
        self.scheduler.start()
        
        self.wifi_job = WifiActiveCampaignCron.run
        self.bt_job = BluetoothActiveCampaignCron.run
        self.log_job = LogRotation.run
        self.status_job = StatusAPI.run
        
        # Schedule Status API
        self.addSecondSchedule(self.status_job, newschedule_secs=15)
        
        # Schedule Wi-Fi Job
        self.addMinuteSchedule(self.wifi_job, newschedule_mins=1)
        
        # Schedule BT Job
        self.addMinuteSchedule(self.bt_job, newschedule_mins=1)
        
        # Schedule Log Rotation Job, rotation doesn't always happen when this job runs
        self.addMinuteSchedule(self.log_job, newschedule_mins=1)
    
    def addMinuteSchedule(self, job, newschedule_mins):
        self.scheduler.add_interval_job(job, minutes=newschedule_mins)
    
    def addSecondSchedule(self, job, newschedule_secs):
        self.scheduler.add_interval_job(job, seconds=newschedule_secs)