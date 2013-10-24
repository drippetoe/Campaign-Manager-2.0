class Connection:
    def __init__(self):
        self.reconnect_interval = 30000
        self.keep_alive = 20000