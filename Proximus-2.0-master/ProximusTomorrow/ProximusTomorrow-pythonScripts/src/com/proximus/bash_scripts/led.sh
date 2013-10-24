#!/bin/bash
################

# A is BELL
# B is 4
# C is 3
# D is 2
# E is 1

killAllLED ()
{
    echo "abcde" > /dev/led 
}


blinkAll ()
{
    for i in 1 2 3 4 5 6 7 8 9 10
    do
        echo "ABCDE" > /dev/led
        sleep 1
        echo "abcde" > /dev/led
        sleep 1
    done
   
}

if [ "$1" = "killAll" ]; then
    killAllLED
elif [ "$1" = "locate" ]; then
    blinkAll
    killAllLED
else
    echo "Usage: killAll | locate | status"
fi