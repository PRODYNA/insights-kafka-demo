# Kafka demo

Main purpose of this application is to show how to setup 
- local configuration for kafka infrastructure (broker, zookeeper, schema registry) 
- with a topic, producer and consumer

By using /api/kafka/publish/messages endpoint we will publish messages that will be consumed by our listener.  

### Config

- openjdk 17
- spring boot 3.0.6

### How to

In order to upload schema and/or generate AVRO sources after that do next:

- go to folder root/kafka and run ```docker-compose up``` make sure that all services are up and running
- and run ```mvn validate```
- go to http://localhost:8081/schemas and check if schema for ```UserCreated``` is registered
- in case that you want to generate local AVRO sources (the one that we are using is already part of the repo) run ```mvn generate-sources```
- run the app and produce some messages via ```curl -X GET http://localhost:8080/demo/api/kafka/publish/messages```
- observe the logs :)
- use control center to access broker http://localhost:9021/clusters

### Swagger UI

```
http://localhost:8080/demo/swagger-ui/index.html
```

### Activity diagram

![activity_diagram.png](src%2Fmain%2Fresources%2Fstatic%2Factivity_diagram.png)

### Useful links

- https://kafka.apache.org/documentation/
- https://docs.spring.io/spring-kafka/reference/html/
- https://avro.apache.org/docs/1.11.1/
- https://github.com/confluentinc/examples
- https://developer.confluent.io/get-started/spring-boot/#client-examples-java-springboot
