1) Disable the authentification on the rest connector:

<rest-connector virtual-server="default-host" cache-container="local" security-domain="other" auth-method="BASIC"/>

=>

<rest-connector virtual-server="default-host" cache-container="local"/>

2) Start the server

3) Fix the provided code in RestClient and run it

4) Fix the provided code in HotrodClient and run it
