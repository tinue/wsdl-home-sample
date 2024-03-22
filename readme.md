# Hello World SOAP server and client, using JAXB

## Goal
I needed to call an external SOAP service, for which I only had a WSDL and some XSD files. The service
was protected with SSL client authentication. The service could not yet be tested, so I wanted to
create my own small sample server and test against this one.  
This proved to be more difficult that I anticipated. All of the examples that I found online had
at least one flaw. These flaws included:
* Only work with Java 8, and not 11 or 17.
* Generate `javax.*` classes, and not `jakarta.*`
* Work with plain http, instead of https
* Generate artefacts that only work on the machine where they were generated (i.e. contained machine specific path elements).
* The server part was always "code first" or "bottom up", instead of starting with the WSDL

After I finally managed to develop a working example, I decided to document it and put it on Github.

## Structure
There are three modules in this project:
* Library: WSDL/XSD files and Maven definitions to create a Java library out of this source material.
  The result of the build is a JAR artefact that can be used to implement a server or a client for the
  service defined in the WSDL.
* Server: A server implementation, running with Spring Boot as its container.
* Client: A client implementation, calling the server.

## Library
### Make Java classes from a WSDL file
Java classes are produced from WSDL with the tool `wsimport` (through the Maven `jaxws-maven-plugin`).
Because the entire JAXB implementation is no longer part of the jdk as of Java 11, the tool needs to be acquired 
from somewhere else. The best (only?) source for this tool and the Maven plugin is the
[Eclipse Implementation of Jakarta XML Web Services](https://github.com/eclipse-ee4j/metro-jax-ws), which
contains everything required. The later versions of the plugin have the strange group id `com.sun.xml.ws`, but they
generate `jakarta.*` classes nevertheless.  
Also required is the [Jakarta EE API](https://github.com/jakartaee/jax-ws-api) for compiling the generated
source stubs.
### Build
The build will generate a jar file which can be installed to the local `.m2` repository (`mvn install`) or
uploaded to the configured artifact repository, if there is one (`mvn deploy`).  
Hint: If you have a [Synology](https://www.synology.com), and want to operate a
[Nexus](https://www.sonatype.com/products/nexus-repository) repository, then check my project
[Synology Development Server](https://github.com/tinue/synology-docker-services).

## Server
### Servlet Runtime
For the servlet runtime environment, we use Spring Boot v3. Version 3 is used, because it supports
the `jakarta.*` namespace.  
This is not a Spring Boot tutorial, though. If you need more information there are plenty of resources
available.

### Server runtime

The best way is to use the
Jakarta EE Reference implementation.
* Jakarta EE 8 was 100% compatible to the old Oracle implementation
* Jakarta EE 9 got the new namespace `jakarta.*`, and no new features
* Starting with Jakarta EE 10, new features are being introduced.

The application can simply be run locally with `mvn spring-boot:run` (you may need to change
the port in `application.properties`, though). A quick test shows if it is working:
`curl -k https://localhost:8443/services/helloService.wsdl` should return the wsdl file (without the XSDs).

For a more permanent server, I added a `Dockerfile`. 
Use your preferred docker environment to start it up. Very brief instructions:  
* Build the jar: `mvn clean package`
* Copy the jar file (`hello-1.0-SNAPSHOT.jar`) to the docker build directory
* Build the docker image: `docker build -t hello/hello:v1`
* Run the application: `docker run -d -p 8443:443 --rm --name hello hello/hello:v1`

Notes:
* `--rm` removes the template when the process is stopped. This prevents clutter from building
  up on the docker host.
* Parameter `-d` runs the process in the background and frees up the command
  prompt. If you want to see the output, skip this parameter. In this case you should use a terminal muxer
  such as `tmux`, so that you can reconnect to the session later on.
* Port `8443` is just an example; Use any port you wish.
* The ping service initially had the signature `public PingResponse ping10(@RequestPayload PingRequest pingRequest)`,
  just like the hello service. This worked for curl-based ping requests, but the Java client failed with
  an error `No adapter for endpoint...`. I never quite understood why this happens. To solve, check the
  generated ObjectFactory for the service: If it contains a factory for `JAXBElement<YourResponse>`, then
  you have to wrap the reply, as it is done in the ping service. If this factory is missing, then
  the wrapper is not necessary, and you can return the reply unwrapped.

### SSL and certificate handling
Enabling SSL on the server side is very easy: It simply needs to be defined in the application
configuration `application.properties`.  
I am using certificates generated by my own home-use certificate authority. See project
[home-ca](https://github.com/tinue/home-ca) for a detailed explanation.  
Obviously the certificates in the repository are just dummy placeholders. You need to provide
your own files for the sample to work.  
To simplify development, I have created a directory `real-certs` under `resources-overwrite`. This
directory is not getting committed to the repo (it is in `.gitignore`). During the build, the content
of this directory is copied to the `target`, and replaces the dummy certificates that are present.

## Client
### Dependencies
The client needs the generated jax-ws classes and the JAX-WS runtime library.
### Client implementation
The client is very simple,
and mostly explains itself. Some remarks:
* SSL Server validation will just work, if the certificate that the server uses is valid. It must be
issued by an issuer that is known by the Java runtime. This means that the issuer needs to be in
the `cacerts` file of the Java installation.
* Just for fun, schema validation is enabled. To test, use e.g. a blank last name, or a `null` first name.
### SSL client authentication
This part is TODO.

## Links
* [jaxb2-maven-plugin, Maven central](https://mvnrepository.com/artifact/org.codehaus.mojo/jaxb2-maven-plugin)
* [jaxb2-maven-plugin, source](https://github.com/mojohaus/jaxb2-maven-plugin)
* [xjc, Maven central](https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-xjc)
* [xjc, source](https://github.com/eclipse-ee4j/jaxb-ri)
* [jaxws-maven-plugin, Maven central](https://mvnrepository.com/artifact/com.sun.xml.ws/jaxws-maven-plugin)
* [jaxws-maven-plugin, Github](https://github.com/eclipse-ee4j/metro-jax-ws/tree/master/jaxws-ri/extras/jaxws-maven-plugin)
* [jaxws-maven-plugin, Documentation](https://eclipse-ee4j.github.io/metro-jax-ws/jaxws-maven-plugin/)
