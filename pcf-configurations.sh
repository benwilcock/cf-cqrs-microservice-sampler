#!/bin/bash
cf create-service p-config-server standard config -c ./config-server-setup.json
cf create-service p-service-registry standard registry
cf cups logdrain -l syslog-tls://logs4.papertrailapp.com:24938