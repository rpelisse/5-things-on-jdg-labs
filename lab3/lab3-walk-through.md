Ex1 - ReST Authentification

1) add back basic Auth in standalone/configuration/standalone.sh (if removed in lab1)

<rest-connector virtual-server="default-host" cache-container="local"/>

=>

<rest-connector virtual-server="default-host" cache-container="local" security-domain="other" auth-method="BASIC"/>

2) Using ./bin/add-user.sh add a ApplicationRealm user (choice b) at the beginning) with the role 'REST'

3) Start the server

4) Validate the changes are working using 'curl':

$ curl -X PUT --user demo:demo http://localhost:8080/rest/namedCache/first-key -d "FIRST_VALUE"
$ curl -i -X GET --user demo
http://localhost:8080/rest/namedCache/first-key
Enter host password for user 'demo':
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Pragma: No-cache
Cache-Control: no-cache
Expires: Thu, 01 Jan 1970 01:00:00 CET
Last-Modified: Thu, 01 Jan 1970 00:00:00 GMT
ETag: application/x-www-form-urlencoded2106881009
Content-Type: application/x-www-form-urlencoded
Content-Length: 5
Date: Thu, 01 Oct 2015 12:25:10 GMT

FIRST_VALUE

5) Fix the provided SecuredRestClient to use authentification

6) Stop the server and modify its configuration to use LDAP, using the following documentation[1] and this demo online LDAP service[2]

[1] https://access.redhat.com/documentation/en-US/Red_Hat_JBoss_Data_Grid/6.5/html/Developer_Guide/chap-Red_Hat_JBoss_Data_Grid_Security_Authorization_and_Authentication.html
[2] http://www.forumsys.com/tutorials/integration-how-to/ldap/online-ldap-test-server/ - if service is online....
