package ch.erzberger.hello;

import ch.erzi.home.soap.services.HelloPort;
import ch.erzi.home.soap.types.hello.HelloRequest;
import ch.erzi.home.soap.types.hello.HelloResponse;
import ch.erzi.home.soap.types.hello.ObjectFactory;
import ch.erzi.home.soap.types.ping_1_0.PingRequest;
import ch.erzi.home.soap.types.ping_1_0.PingResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.addressing.server.annotation.Action;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The endpoint defines the operations of the service.
 */
@Endpoint
public class HelloEndpoint implements HelloPort {
    @Override
    @ResponsePayload
    @PayloadRoot(namespace = "http://soap.home.erzi.ch/types/ping_1_0", localPart = "pingRequest")
    public PingResponse ping10(@RequestPayload PingRequest pingRequest) {
        PingResponse response = new ch.erzi.home.soap.types.ping_1_0.ObjectFactory().createPingResponse();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            response.setHostAddress(ip.getHostAddress());
            response.setHostName(ip.getHostName());
            return response;
        } catch (UnknownHostException e) {
            response.setHostName("localhost");
            response.setHostAddress("127.0.0.1");
            return response;
        }
    }

    @Override
    @ResponsePayload
    @PayloadRoot(namespace = "http://soap.home.erzi.ch/types/hello", localPart = "helloRequest")
    public HelloResponse hello(@RequestPayload HelloRequest helloRequest) {
        HelloResponse response = new ObjectFactory().createHelloResponse();
        response.setGreeting(String.format("Hello %s %s", helloRequest.getFirstName(), helloRequest.getLastName()));
        return response;
    }
}
