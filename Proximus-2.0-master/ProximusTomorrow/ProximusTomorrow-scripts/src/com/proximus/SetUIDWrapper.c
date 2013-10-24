#ifndef __DEBUG__
#define DEBUG 0
#endif
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

/* PATHS */
const char * lighttpdPath 	= "/home/proximus/bin/bash_scripts/lighttpd.sh";
const char * bluetoothPath 	= "/home/proximus/bin/bash_scripts/bluetooth.sh";
const char * ethernetPath 	= "/home/proximus/bin/bash_scripts/ethernet_use.sh";
const char * donglePath 	= "/home/proximus/bin/bash_scripts/dongle.sh";
const char * permitPath 	= "/home/proximus/bin/bash_scripts/chmod.sh \"www-data\" \"/home/proximus/logs\"";
const char * channelPath 	= "home/proximus/bin/bash_scripts/channel.sh";
const char * ssidPath 		= "/home/proximus/bin/bash_scripts/ssid.sh";
const char * sshPath 		= "/home/proximus/bin/bash_scripts/reverse_ssh.sh";
const char * portalPath 	= "/home/proximus/bin/portal.py";
const char * wifiPath 		= "/home/proximus/bin/bash_scripts/startWifiAcessPoint.sh";
const char * ledPath        = "/home/proximus/bin/bash_scripts/led.sh";
const char * dnsPath        = "/home/proximus/bin/bash_scripts/dns.sh";
const char * ledOnPath[4]   = {"echo \"default-on\" > /sys/class/leds/guruplug\\:green\\:wmode/trigger","echo \"default-on\" > /sys/class/leds/guruplug\\:red\\:wmode/trigger","echo \"default-on\" > /sys/class/leds/guruplug\\:green\\:health/trigger","blinkled > /dev/null"};
const char * ledOffPath[4]  = {"echo \"none\" > /sys/class/leds/guruplug\\:green\\:wmode/trigger","echo \"none\" > /sys/class/leds/guruplug\\:red\\:wmode/trigger","echo \"none\" > /sys/class/leds/guruplug\\:green\\:health/trigger","blinkbtled 0xf1010148 w 0x000 > /dev/null"};

/* General Helper Functions */
int print_usage();

/* Custom Command Functions */
int lighttpd(char * command);
int bluetooth(char * command, char * opt);
int ethernet(char * command);
int dongle(char * command);
int permit_logs(char * command);
int channel(char * command);
int ssid(char * command);
int ssh(char * devicePort, char * hostPort);
int portal(char * command, char * argument, char * optional);
int ledSwitch(int led, int on);
int ledLocate(char * command);
int dns(char * command);

/*
 * Main
 */
int
main( int argc, char *argv[], char **envp )
{
	if(argc > 2) {
		setreuid(0,0);
		if(strcmp(argv[1], "lighttpd") == 0) {
			return lighttpd(argv[2]);
		}else if(strcmp(argv[1], "bluetooth") == 0){
			if(argc>3){
				return bluetooth(argv[2],argv[3]);
			}
			return bluetooth(argv[2],NULL);
		}else if(strcmp(argv[1], "ethernet") == 0){
			return ethernet(argv[2]);
		}else if(strcmp(argv[1], "dongle") == 0){
			return dongle(argv[2]);
		}else if(strcmp(argv[1], "permit") == 0){
			return permit_logs(argv[2]);
		}else if(strcmp(argv[1], "channel") == 0){
			return channel(argv[2]);
        }else if(strcmp(argv[1], "ledshell") == 0) {
            return ledLocate(argv[2]);
		}else if(strcmp(argv[1], "reversessh") == 0) {
			if(argc>3) {
				return ssh(argv[2], argv[3]);
			}
			return ssh(argv[2], NULL);
		}else if(strcmp(argv[1], "ssid") == 0){
			return ssid(argv[2]);
		}else if(strcmp(argv[1], "dns") == 0){
			return dns(argv[2]);
		}else if(strcmp(argv[1], "portal") == 0){
			if(argc>3){
				return portal(argv[2], argv[3], argv[4]);
			}
			return portal(argv[2], argv[3], NULL);
		}else if(strcmp(argv[1], "led") == 0){
			int led = atoi (argv[2]);
                        int on = atoi (argv[3]);
                        return ledSwitch(led,on);
		}
	}

	return print_usage(argv[1]);
}

/*
 * Print Usage
 */
int
print_usage(char * command){
	printf("Command not found: %s\n", command);
	printf("Valid commands:\n");
	printf("Usage: lighttpd [start | stop | restart | status]\n");
	printf("Usage: bluetooth [start \"name\"| restart | stop | name \"name\"]\n");
	printf("Usage: ethernet [use]\n");
	printf("Usage: dongle [on|off]\n");
	printf("Usage: permit [logs]\n");
	printf("Usage: channel [number]\n");
	printf("Usage: ssid [ssid_Name]\n");
	printf("Usage: dns [start|stop|restart]\n");
	printf("Usage: reversessh [devicePort] <hostPort>\n");
	printf("Usage: ledshell locate\n");
	printf("Usage: led [led_number] [1|0]\n");
	printf("portal [open|closed] [add|remove|start|default] [<IP>|<domain>]\n");
	return 1;
}

/*
 * Lighttpd
 */
int
lighttpd(char * command){
        if(strcmp(command, "start") == 0){
		int size = strlen(lighttpdPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", lighttpdPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}else if(strcmp(command, "stop") == 0){
		int size = strlen(lighttpdPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", lighttpdPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}else if(strcmp(command, "restart") == 0){
		int size = strlen(lighttpdPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", lighttpdPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}else if(strcmp(command, "status") == 0){
		int size = strlen(lighttpdPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", lighttpdPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}
	printf("Usage: lighttpd [start | stop | restart | status]\n");
	return 1;
}

/*
 * Bluetooth
 */
int
bluetooth(char * command, char * opt){	
	if(strcmp(command, "start") == 0){
		if(opt!=NULL){
			int size = strlen(bluetoothPath)+strlen(" ")+strlen(command)+strlen(" \"")+strlen(opt)+strlen("\"")+1;
			char buff[size];
			sprintf(buff, "%s %s \"%s\"", bluetoothPath, command, opt);
			DEBUG?printf("system(%s);\n",buff):0;
			return DEBUG?0:system((char *)buff);
		}
	}else if(strcmp(command, "name") == 0){
		if(opt!=NULL){
			int size = strlen(bluetoothPath)+strlen(" ")+strlen(command)+strlen(" \"")+strlen(opt)+strlen("\"")+1;
			char buff[size];
			sprintf(buff, "%s %s \"%s\"", bluetoothPath, command, opt);
			DEBUG?printf("system(%s);\n",buff):0;
			return DEBUG?0:system((char *)buff);
		}
	}else if(strcmp(command, "stop") == 0){
		int size = strlen(bluetoothPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", bluetoothPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}else if(strcmp(command, "restart") == 0){
		int size = strlen(bluetoothPath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", bluetoothPath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}
	printf("Usage: bluetooth [start \"name\"| restart | stop | name \"name\"]\n");
	return 1;
}

/*
 * Ethernet
 */
int
ethernet(char * command){
	if(strcmp(command, "use") == 0){
		DEBUG?printf("system(%s);\n",ethernetPath):0;
		return DEBUG?0:system(ethernetPath);
	}
	printf("Usage: ethernet [use]\n");
	return 1;
}
/*
 * Dongle
 */
int
dongle(char * command){
	if(strcmp(command, "on") == 0 || strcmp(command, "off") == 0){
		int size = strlen(donglePath)+strlen(command)+2;
		char buff[size];
		sprintf(buff, "%s %s", donglePath, command);
		DEBUG?printf("system(%s);\n",buff):0;
		return DEBUG?0:system((char *)buff);
	}
	printf("Usage: dongle [on|off]\n");
	return 1;
}
/*
 * Permit the use of logs
 */
int
permit_logs(char * command){
	if(strcmp(command, "logs") == 0){
		DEBUG?printf("system(%s);\n",permitPath):0;
		return DEBUG?0:system(permitPath);
	}
	printf("Usage: permit [logs]\n");
	return 1;
}
/*
 * Set WiFi Channel
 */
int
channel(char * command){
	int size = strlen(channelPath)+strlen(command)+2;
	char buff[size];
	sprintf(buff, "%s %s", channelPath, command);
	DEBUG?printf("system(%s);\n",buff):0;
	return DEBUG?0:system((char *)buff);
}
/*
 * Set WiFi SSID
 */
int
ssid(char * command){
	int size = strlen(ssidPath)+strlen(" \"")+strlen(command)+strlen("\"")+1;
	char buff[size];
	sprintf(buff, "%s \"%s\"", ssidPath, command);
	DEBUG?printf("system(%s);\n",buff):0;
	return DEBUG?0:system((char *)buff);
}

/*
 * LED LOCATE
 */
int 
ledLocate(char * command) {
     int size = strlen(ledPath)+strlen(" \"")+strlen(command)+strlen("\"")+1;
	char buff[size];
	sprintf(buff, "%s \"%s\"", ledPath, command);
	DEBUG?printf("system(%s);\n",buff):0;
	return DEBUG?0:system((char *)buff);
}

/*
 * DNS
 */
int 
dns(char * command) {
     int size = strlen(dnsPath)+strlen(" \"")+strlen(command)+strlen("\"")+1;
	char buff[size];
	sprintf(buff, "%s \"%s\"", dnsPath, command);
	DEBUG?printf("system(%s);\n",buff):0;
	return DEBUG?0:system((char *)buff);
}

/*
 * Open a SSH tunnel to the development server
 */
int
ssh(char * devicePort, char * hostPort){
	if(strlen(devicePort) > 0){
		if(hostPort!=NULL && strcmp(hostPort,"0")!=0){
			int size = strlen(sshPath)+strlen(" ")+strlen(devicePort)+strlen(" ")+strlen(hostPort)+1;
			char buff[size];
			sprintf(buff, "%s %s %s", sshPath, devicePort, hostPort);
			DEBUG?printf("system(%s);\n",buff):0;
			return DEBUG?0:system((char *)buff);
		}else{
			int size = strlen(sshPath)+strlen(devicePort)+2;
			char buff[size];
			sprintf(buff, "%s %s", sshPath, devicePort);
			DEBUG?printf("system(%s);\n",buff):0;
			return DEBUG?0:system((char *)buff);
		}
	}
	printf("Usage: reversessh [devicePort] <hostPort>\n");
	return 1;
}

/*
 * Enable captive portal etc.
 */
int
portal(char * command, char * argument, char * optional){
	if(strcmp(command, "open") == 0 || strcmp(command, "closed") == 0){
		if(strcmp(argument, "add") == 0 || strcmp(argument, "remove") == 0 || strcmp(argument, "start") == 0 || strcmp(argument, "default") == 0){		
			if (optional != NULL){
				int size = strlen(portalPath)+strlen(" ")+strlen(command)+strlen(" ")+strlen(argument)+strlen(" ")+strlen(optional)+1;
				char buff[size];
				sprintf(buff, "%s %s %s %s", portalPath, command, argument, optional);
				DEBUG?printf("system(%s);\n",buff):0;
				return DEBUG?0:system((char *)buff);
			}
			else {
				int size = strlen(portalPath)+strlen(" ")+strlen(command)+strlen(" ")+strlen(argument)+1;
				char buff[size];
				sprintf(buff, "%s %s %s", portalPath, command, argument);
				DEBUG?printf("system(%s);\n",buff):0;
				return DEBUG?0:system((char *)buff);
			}
		}
	}
	printf("portal [open|closed] [add|remove|start|default] [<IP>|<domain>]\n");
	return 1;
}
/*
 * Led
 */
int
ledSwitch(int led, int on){
    
    switch(led){
        case 1:if(on){
                system(ledOnPath[0]);                        
            }else{
                system(ledOffPath[0]);
            }            
            break;
        case 2:
            if(on){
                system(ledOnPath[1]);                      
            }else{
                system(ledOffPath[1]);
            }
            break;
        case 3:
            if(on){
                system(ledOffPath[3]);
                system(ledOnPath[2]);                        
            }else{
                system(ledOffPath[2]);
                system(ledOffPath[3]);
            }
            break;
        case 4:
            if(on){
                system(ledOffPath[2]);
                system(ledOnPath[3]);                        
            }else{
                system(ledOffPath[3]);
                system(ledOffPath[2]);                
            }
            break;
        default:
            break;
    };
    return 0;
}