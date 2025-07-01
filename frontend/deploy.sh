#!/bin/bash
SERVER_IP=$1
FRONTEND_VERSION=$2

# copy latest 
scp compose.yaml deployuser@$SERVER_IP:/home/deployuser

# ssh 
ssh deployuser@$SERVER_IP "
	cd /home/deployuser

	# stop docker containers 
	docker-compose down 
	
	# update FRONTEND_VERSION in .env file 
	sed -i 's/^FRONTEND_VERSION=.*/FRONTEND_VERSION=$FRONTEND_VERSION/' .env

	# start docker containers 
	docker-compose up -d 
"


