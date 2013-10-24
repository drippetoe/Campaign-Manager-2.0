#!/bin/sh
################

killAllLED ()
{ 
    echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
    echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
    blinkbtled 0xf1010148 w 0x000 > /dev/null
    echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
}


locateOne ()
{
    for i in 1 2 3 4 5 6 7 8 9 10
    do
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .3
    done
   
}

locateTwo ()
{
    for i in 1 2 3 4 5 6 7 8 9 10
    do
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "default-on" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
        echo "default-on" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
        echo "none" > /sys/class/leds/guruplug\:green\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:red\:wmode/trigger
        echo "none" > /sys/class/leds/guruplug\:green\:health/trigger
        sleep .2
    done
   
}


if [ "$1" = "killAll" ]; then
    killAllLED
elif [ "$1" = "locate" ]; then
    locateOne
    killAllLED
elif [ "$1" = "locate2" ]; then
    locateTwo
    killAllLED
elif [ "$1" = "status" ]; then
    cat /sys/class/leds/guruplug\:green\:wmode/trigger | egrep '\[none\]'
    cat /sys/class/leds/guruplug\:red\:wmode/trigger
    cat /sys/class/leds/guruplug\:green\:health/trigger
else
    echo "Usage: killAll | locate | status"
fi