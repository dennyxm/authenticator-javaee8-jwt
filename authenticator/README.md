# Overview
This application was built as an experiment after watching a youtube video by Adam Bien about developing an application using Java EE. 

After several hours of experimenting with airhacks java EE 8 maven archetype, here it is, a simple authenticator system, using JWT as a token generator , AES-GCM as its encryption scheme, and SLF4J+Log4J as its Logging Library

# Howto
1. You need to have an application server installed ( i personally use wildfly )
2. You would need to install a postgresql driver and make a datasource that connects to it
3. Make sure the datasource defined in META-INF/persistence.xml is exactly the same as the one you made at step 2
4. Run the wildfly and do the step below, please adjust accordingly

# Build
```
mvn clean package && cp ./target/authenticator.war ~/Your-Wildfly-Folder/standalone/deployments
```
  

# References
* [Adam Bien Youtube Channel](https://www.youtube.com/user/bienadam)
* [Auth-0 Java JWT Library](https://github.com/auth0/java-jwt)
* [https://mkyong.com/java/java-aes-encryption-and-decryption/](https://mkyong.com/java/java-aes-encryption-and-decryption/)
* [Wildfly-PostgreSQL-Datasource Config Tutorial](http://www.mastertheboss.com/jboss-server/jboss-datasource/configuring-a-datasource-with-postgresql-and-jboss-wildfly)

