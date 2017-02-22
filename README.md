# Track Web Service
##Quick Summary##
Track web service is a collection of endpoints to deal with the Track applications requests. All the logic happens on the Java API for RESTful Web Service (JAX-RS) rather than the users device. The device (client) acts like a window allowing the user just to see the information they requested. The JAX-RS connects to the [National Rails web service (OpenLDBWS)](https://lite.realtime.nationalrail.co.uk/OpenLDBWS/) making SOAP requests. All the user and app information is stored on a couch database ([Apache CouchDB](http://couchdb.apache.org/)).


##Dependencies and Frameworks##
[Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)<br>
[Apache Maven](https://maven.apache.org/)<br>
[ASM Core 3.3.1](https://mvnrepository.com/artifact/asm/asm)<br>
[LightCouch 0.1.8](https://mvnrepository.com/artifact/org.lightcouch/lightcouch)<br>
[Json 20140107](https://mvnrepository.com/artifact/org.json/json)<br>
[Guava: Google Core Libraries For Java 11.0.2](https://mvnrepository.com/artifact/com.google.guava/guava)<br>
[Jersey Container Jdk Http 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-jdk-http)<br>
[Jersey Container Servlet Core 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-servlet-core)<br>
[Jersey Core Server 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.core/jersey-server)<br>
[JSON Web Token Support For The JVM 0.6.0](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt)<br>

##Developers##
Giovanni Lenguito - giovanni16.gl@gmail.com

##License##
Copyright 2017 Giovanni Lenguito
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
