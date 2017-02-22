# Track Web Service
##Quick Summary##
Track web service is a collection of endpoints to deal with the Track applications requests. All the logic happens on the JAX-RS rather than the users device.The device (client) acts like a window allowing the user just to see the information they requested. The JAX-RS connects to the [National Rails web service (OpenLDBWS)](https://lite.realtime.nationalrail.co.uk/OpenLDBWS/) making SOAP requests.  


##Dependencies and Frameworks##
[Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
[Apache Maven](https://maven.apache.org/)
[ASM Core 3.3.1](https://mvnrepository.com/artifact/asm/asm)
[LightCouch 0.1.8](https://mvnrepository.com/artifact/org.lightcouch/lightcouch)
[Json 20140107](https://mvnrepository.com/artifact/org.json/json)
[Guava: Google Core Libraries For Java 11.0.2](https://mvnrepository.com/artifact/com.google.guava/guava)
[Jersey Container Jdk Http 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-jdk-http)
[Jersey Container Servlet Core 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.containers/jersey-container-servlet-core)
[Jersey Core Server 2.7](https://mvnrepository.com/artifact/org.glassfish.jersey.core/jersey-server)
[JSON Web Token Support For The JVM 0.6.0](https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt)

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
