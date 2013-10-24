import os

class Platform:
    
    PLATFORM_LINUX_BLUEGIGA = "Bluegiga"
    PLATFORM_LINUX_DREAMPLUG = "Dreamplug"
    PLATFORM_LINUX_DDWRT = "DDWRT"
    PLATFORM_MAC = "MacOSX"
    PLATFORM_WIN = "Windows"
    PLATFORM_UNKNOWN = "Unknown"
      
    @staticmethod
    def getPlatform():
        if ( os.path.exists("/etc/bluegiga-release") ):
            return Platform.PLATFORM_LINUX_BLUEGIGA # Bluegiga
        elif ( os.path.exists("/jffs") ):
            return Platform.PLATFORM_LINUX_DDWRT
        elif ( os.path.exists("/etc/rc.local") ):
            return Platform.PLATFORM_LINUX_DREAMPLUG # Dreamplug, etc
        elif ( os.path.exists("/Library") ):
            return Platform.PLATFORM_MAC
        elif ( os.path.exists("C:\\") ):
            return Platform.PLATFORM_WIN
        else:
            return Platform.PLATFORM_UNKNOWN