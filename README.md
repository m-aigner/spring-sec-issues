# Sample repo for some Spring Security issues

You almost certainly got linked here from an issue in the Spring Security
repository; if you didn't, there's likely nothing of interest to you here.

## Prerequisites: OpenLDAP

Everything I say in those issues has been verified to work the way I say it
works *with openldap*. So you need openldap. I am assuming that you are on
Linux; openldap is basically guaranteed to be in your package manager, though
the package names differ from distro to distro. (If you're not on Linux, I'm
sorry but you'll have to work out yourself how to get a configuration like
mine. Or ask me for a Docker image or something.) 

Once you've installed openldap, copy the following into sample.ldif...
```
dn: dc=example,dc=com
dc: example
o: Example Organization
objectClass: dcObject
objectClass: organization
structuralObjectClass: organization
entryUUID: cb097bea-a32c-4808-a488-1eb9346b8879
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200519144323Z
entryCSN: 20200519144323.332840Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200519144323Z

dn: cn=admin,dc=example,dc=com
cn: admin
description: LDAP admin
objectClass: organizationalRole
objectClass: top
roleOccupant: dc=example,dc=com
structuralObjectClass: organizationalRole
entryUUID: b73df6f3-77b2-4e2b-a3f3-cb1ac5dd3661
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200519144437Z
entryCSN: 20200519144437.868619Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200519144437Z

dn: ou=people,dc=example,dc=com
ou: people
objectClass: top
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit
entryUUID: 37e5b7a5-0d49-454a-ada7-70c7c827f006
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200519144437Z
entryCSN: 20200519144437.878977Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200519144437Z

dn: uid=user1,ou=people,dc=example,dc=com
objectClass: inetOrgPerson
sn: Doe
cn: John Doe
structuralObjectClass: inetOrgPerson
uid: user1
entryUUID: 2daa5e0b-95d4-4c53-b7c9-1e5d77c21647
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200519145422Z
userPassword:: dXNlcjE=
entryCSN: 20200617171208.092548Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200617171208Z

dn: ou=groups,dc=example,dc=com
ou: groups
objectClass: top
objectClass: organizationalUnit
structuralObjectClass: organizationalUnit
entryUUID: dce7ae7b-fe3f-489d-acce-dacd0b544296
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200617180504Z
entryCSN: 20200617180504.471196Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200617180504Z

dn: cn=samplepeople,ou=groups,dc=example,dc=com
cn: samplepeople
objectClass: groupOfUniqueNames
uniqueMember: uid=user1,ou=people,dc=example,dc=com
structuralObjectClass: groupOfUniqueNames
entryUUID: 528281e9-6801-4565-b866-d7ddbfc0cbf7
creatorsName: cn=admin,dc=example,dc=com
createTimestamp: 20200617180803Z
entryCSN: 20200617180803.489618Z#000000#000#000000
modifiersName: cn=admin,dc=example,dc=com
modifyTimestamp: 20200617180803Z
```
...and add the following to your slapd.conf: 
```
access to * attrs=sn
	by self write
	by * none

access to * attrs=userPassword
	by anonymous auth
	by * none

access to *
	by * read
```
Also, make sure that your slapd.conf contains
```
include		/etc/openldap/schema/core.schema
include 	/etc/openldap/schema/cosine.schema
include 	/etc/openldap/schema/inetorgperson.schema
include 	/etc/openldap/schema/nis.schema
```
Note that slapd.conf is actually deprecated, and you will (or, at least, might) need to run
slaptest so your configuration changes are seen by openldap at all. See the
third note in
[this](https://wiki.archlinux.org/index.php/OpenLDAP#The_server).

Finally, run `slapadd -l /path/to/sample.ldif`.

## Prerequisites: Java

None, except for Java 11 (it *should* work with Java 8, but I haven't tried
it) and Maven.

## Running

In the issue containing the link that got you here, I say which branch in this
repository you should look at. Check that branch out, start slapd, run the Java
application in this repository (`mvn spring-boot:run` and starting it from an
IDE both work), go to `localhost:8080/login`, and log in with the username
`user1` and the password `user1`. Then look at the console logs. (You can't log
out explicitly; going back to /login works well enough, though.)

## Modifying

A lot of stuff is hardcoded; in particular, you'll need to change Java code if
you change the LDAP schema in any way. All the interesting configuration is in
SecurityConfiguration.java, regardless of which branch you're on. The logging
is handled in LoggingUserDetailsMapper.java; the things that that class's
`mapUserFromContext` method has access to are the things an application
developer using Spring's LDAP classes will have (easy) access to, so that
seemed to be the best way to handle things.
