class BluetoothConfig:
    def __init__(self):
        self.resendInterval = None
        self.id = None
    
    def getResendInterval(self):
        return self.resendInterval
        
    def setResendInterval(self, resendInterval):
        self.resendInterval = resendInterval
    
    def getId(self):
        return self.id
        
    def setId(self, newId):
        self.id = newId
