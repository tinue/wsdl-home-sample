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

After I finally managed to develop a working example, I decided to document it and put it on Github.

## Server
### Caveat
First a warning: It might be possible to start with a WSDL file and create the necessary operations. I did
not attempt this, though. I have only the datatypes in the form of an `xsd` file. The operations are defined
by implementing them. The `wsdl` file for the client can then be generated / retrieved from the server
implementation.

### Make Java classes from an XSD file
Java classes are produced from XSD with the tool `xjc`. Because the entire JAXB implementation is no longer part of
the jdk as of Java 11, the tool needs to be acquired from somewhere else. The best way is to use the
Jakarta EE Reference implementation.  
* Jakarta EE 8 was 100% compatible to the old Oracle implementation
* Jakarta EE 9 got the new namespace `jakarta.*`, and no new features
* Starting with Jakarta EE 10, new features are being introduced.

We would like to use the latest reference implementation. In Maven terms, we ideally would 
want `jaxb-xjc` in version 4.0.1.  
However, we will have to do with `xjc` from Jakarta EE 9, see next paragraph.

### Automate XJC compilation
The tool that gets recommended the most is the `jaxb2-maven-plugin`. Its latest available version
3.1.0 uses Jakarta EE 9: This means that the namespace is correct (`jakarta.*`), but it's
not using the latest available `xjc` tool yet.
There is a [pull request](https://github.com/mojohaus/jaxb2-maven-plugin/pull/226) present,
but I don't know if and when this gets accepted. It appears to break Java 8 compatibility, so who knows
if the pull request will be considered.  
On the other hand, I am not sure if there are real improvements yet for `xjc` in Jakarta EE 10. We have
two options:
* Patch the plugin definition for `jaxb2-maven-plugin` (replace `xjc` 3 with `xjc` 4).
* Use `xjc` v3.

This project uses the second option.

### Servlet Runtime
For the servlet runtime environment, we use Spring Boot v3 (to be released November 24). Version
3 is used, because it supports the `jakarta.*` namespace.  
This is not a Spring Boot tutorial, though. If you need more information there are plenty of resources
available.

### Server runtime
The application can simply be run locally with `mvn spring-boot:run` (you may need to change
the port in `application.properties`, though). For a more permanent server, I added a `Dockerfile`. 
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
### Fetch the wsdl file
The wsdl file can be retrieved with `curl`:  
`curl https://localhost/services/hello.wsdl --output hello.wsdl --insecure`  
The `--insecure` part is only required if the certificate from the server is invalid (e.g. self signed,
or it does not contain `localhost` in the list of subject alternate names).

To do

## Links
* [jaxb2-maven-plugin, Maven central](https://mvnrepository.com/artifact/org.codehaus.mojo/jaxb2-maven-plugin)
* [jaxb2-maven-plugin, source](https://github.com/mojohaus/jaxb2-maven-plugin)
* [xjc, Maven central](https://mvnrepository.com/artifact/org.glassfish.jaxb/jaxb-xjc)
* [xjc, source](https://github.com/eclipse-ee4j/jaxb-ri)
