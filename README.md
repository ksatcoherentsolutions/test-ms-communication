This is a test project to play around different communication options between microservices.

MS2 (port 8082) goes to MS1 (port 8081, simple echo service with 3 sec delay)

test endpoints:  
http://localhost:8082/test1  over HTTP (spring webclient)  
http://localhost:8082/test2  over GRPC  
http://localhost:8082/test3  over WebSocket  
http://localhost:8082/test4  over RSocket  
http://localhost:8082/test5  over HTTP (apache client)  

JMeter plan is in the root.

###Issue #1:  
When I simulate 501 concurrent users to request http://localhost:8082/test1 endpoint, first 500 users get response in 3 seconds and the last one gets response in 6 seconds.
Means spring webclient supports only 500 concurrent connections by default. Corresponding value is configurable in apache client. But I can't find how to configure it in spring webclient.  
Similar situation with WebSocket (http://localhost:8082/test2), it supports only 256 concurrent connections. How to configure it?

###Issue #2:
When I run MS2 it establishes connection with MS1. But when MS1 goes down, connection is dropped and I can't find the way to catch corresponding exception.
Please refer to `RSocketClientServiceImpl1`  where I establish connection.

###Issue #3:
It's described here: https://stackoverflow.com/questions/58857163/spring-rsocketrequester-issue-when-created-with-wrap-function/58873094

Already got an answer.

###Question:
Does spring provide something similar to `LoadBalancedRSocketMono` to automatically restore dropped RSocket connection with the client?
How is it supposed to be restored in spring?