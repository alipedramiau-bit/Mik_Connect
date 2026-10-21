# MikroTik L2TP Connect

An Android (Kotlin) client UI for connecting to a MikroTik L2TP/IPsec VPN server.

## Build
Open in Android Studio, or run `./gradlew assembleDebug`.

## Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit"
gh repo create MikroTikL2TPConnect --public --source=. --push
```

## MikroTik RouterOS configuration
```
/interface l2tp server set enabled=yes use-ipsec=yes ipsec-secret=YOUR_SECRET
/ppp secret add name=pedram password=YOUR_PASS service=l2tp profile=default
/ip pool add name=vpn-pool ranges=10.10.10.10-10.10.10.100
/ppp profile set default local-address=10.10.10.1 remote-address=vpn-pool
/ip firewall filter add chain=input protocol=udp dst-port=1701,500,4500 action=accept
/ip firewall nat add chain=srcnat src-address=10.10.10.0/24 action=masquerade
```

Port 1701 is the L2TP tunnel; 500/4500 are used by IPsec when `use-ipsec=yes`.
