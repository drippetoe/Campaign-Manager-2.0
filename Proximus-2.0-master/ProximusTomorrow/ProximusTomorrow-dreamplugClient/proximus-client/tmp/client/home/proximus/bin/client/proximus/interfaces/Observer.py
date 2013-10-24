class Observer:
    """
    Implements the Observer pattern.
    This class should be inherited by anyone wanting to 
    observe an 'Observable' class
    """
    
    def notify(self, obj):
        """
        Called when notified that 'object' changed
        """
        pass #overriede