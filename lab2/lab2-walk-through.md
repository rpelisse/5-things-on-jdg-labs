1) Set up

* unzip JBoss Data Grid into ${HOME}/lab2/jdg-server
* cd into ${HOME}/lab2/jdg-server and run the server

2) Replication set up

* look at the server configuration, and modify it to ensure that:
* all instance have a full copy of all the data of the grid (replication)
** refer to the provided replication.xml, if needed
* run your server
* wait for the other instances to join, and follow the instruction
** shutdown if requested your instance
** implements all the FIXME

3) Distributed set up

* Stop the server, and modify its configuration to ensure that data is now distributed
* Set the number of owner to 1 to ensure each instance keeps a specific part of the data.
* Starts your instance and waits for the other instances (other attendees) to join
* Once they have join, run the client,  let it import the data,

4) Mixed

* change the number of owners to ensure any key is duplicated 2, 4 and 6 times. For each run a test and look at the impact on the performance
