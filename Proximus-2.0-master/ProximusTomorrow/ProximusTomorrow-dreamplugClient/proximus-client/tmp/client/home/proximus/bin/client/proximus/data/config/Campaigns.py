class Campaigns:
    def __init__(self):
        self.campaigns = []
    
    def addCampaign(self, campaign):
        self.campaigns.append(campaign)
    
    def clearCampaigns(self):
        self.campaigns = []

    def getCampaigns(self):
        return self.campaigns