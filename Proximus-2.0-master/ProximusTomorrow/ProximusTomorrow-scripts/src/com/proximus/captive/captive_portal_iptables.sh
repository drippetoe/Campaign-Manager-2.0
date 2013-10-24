echo 1 > /proc/sys/net/ipv4/ip_forward
iptables -A POSTROUTING -t nat -o eth1 -j MASQUERADE

iptables -t mangle -N internet 
iptables -t mangle -A PREROUTING -j internet
iptables -t mangle -A internet -i uap0 -j MARK --set-mark 99

iptables -t nat -A PREROUTING -m mark --mark 99 -p tcp --dport 80 -j DNAT --to-destination 192.168.3.1

iptables -t filter -A FORWARD -m mark --mark 99 -j DROP

# Do the same for the INPUT chain to stop people accessing the web through Squid
iptables -t filter -A INPUT -i uap0 -p tcp --dport 80 -j ACCEPT
iptables -t filter -A INPUT -i uap0 -p udp --dport 53 -j ACCEPT
iptables -t filter -A INPUT -i uap0 -p udp --dport 67:68 --sport 67:68 -j ACCEPT
iptables -t filter -A INPUT -m mark --mark 99 -j DROP

# set up eth0
iptables -t filter -A INPUT -i eth0 -m state --state RELATED,ESTABLISHED -j ACCEPT
iptables -t filter -A INPUT -i eth0 -p tcp -m tcp --dport 16222 -j ACCEPT
iptables -t filter -A INPUT -i eth0 -j DROP
# set up ppp0
#iptables -t filter -A INPUT -i ppp0 -m state --state RELATED,ESTABLISHED -j ACCEPT
#iptables -t filter -A INPUT -i ppp0 -p tcp -m tcp --dport 22 -j ACCEPT
#iptables -t filter -A INPUT -i ppp0 -p tcp -m tcp --dport 16222 -j ACCEPT
#iptables -t filter -A INPUT -i ppp0 -j DROP

# woojoo
#iptables -t mangle -I internet 1 -m mac --mac-source 00:1D:FE:BE:95:DF -j RETURN
#iptables -t mangle -D internet 1 -m mac --mac-source 00:1D:FE:BE:95:DF
