import os, logging

logger = logging.root

class FileSystemChecker:
    @staticmethod
    def initDir(dir):
        if not os.path.exists(dir):
            try:
                os.makedirs(dir)
                logger.debug("Created directory: %s" % dir)
            except Exception as err:
                logger.error(err)
        else:
            logger.debug("Directory exists: %s" % dir)
    @staticmethod
    def initDirs(dirs=[]):
        for dir in dirs:
            FileSystemChecker.initDir(dir)
    
    @staticmethod
    def getParentDir(somePath):
        return os.path.abspath(os.path.join(somePath))