class Authentication:
    def __init__(self):
        self.token = None
    
    def setToken(self, token):
        self.token = token
    
    def getToken(self):
        return self.token
