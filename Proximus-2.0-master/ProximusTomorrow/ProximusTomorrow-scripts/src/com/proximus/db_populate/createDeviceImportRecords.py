#!/usr/bin/env python
import random


NUM_DEVICES=10000
FIRST_SN=1000000

def getRandomHEX():
    allowed = "0123456789ABCDEF"
    return allowed[int(round(random.random()*len(allowed)-1))]

def getRandomMAC():
    addr = ""
    while len(addr) < 12:
        addr = addr + getRandomHEX()
    return addr

def getRandomPlatform():
    platforms = ['dreamplug', 'bluegiga']
    return platforms[int(round(random.random()*len(platforms)-1))]

if __name__ == "__main__":
    outf = open("device_import_generated.csv", "w")
    serial = FIRST_SN
    for i in range(0, NUM_DEVICES):
        #334345,0024236F7DCE,bluegiga,Yet another Device
        outf.write("%s,%s,%s\n" % ( serial, getRandomMAC(), getRandomPlatform() ))
        serial = serial + 1

    outf.close()

