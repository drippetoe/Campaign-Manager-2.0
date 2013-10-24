class Observable:
    """
    Implements the Observer pattern.
    This object can be observed by any object inheriting "Observer" by
    calling 'someobservableclass.addObserver(self)'
    """
    def __init__(self):
        self.observers = []
        self.changed = False

    def addObserver(self, o):
        if not o in self.observers:
            self.observers.append(o)
    
    def clearChanged(self):
        self.changed = False
    
    def countObservers(self):
        return len(self.observers)

    def getObservers(self):
        return self.observers
    
    def deleteObserver(self, o):
        if o in self.observers:
            self.observers.remove(o)
    
    def deleteObservers(self):
        self.observers = []
        
    def hasChanged(self):
        return self.changed
    
    def setChanged(self):
        self.changed = True
        
    def notifyObservers(self, obj=None):
        if ( not self.changed ):
            return
        for o in self.observers:
            o.notify(obj)
        self.clearChanged()
    
    def initObservers(self):
        for o in self.observers:
            o.init()
