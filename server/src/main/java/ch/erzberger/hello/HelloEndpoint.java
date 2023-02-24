package ch.erzberger.hello;

import ch.erzi.home.soap.types.hello.HelloRequest;
import ch.erzi.home.soap.types.hello.HelloResponse;
import ch.erzi.home.soap.types.ping_1_0.PingRequest;
import ch.erzi.home.soap.types.ping_1_0.PingResponse;
import jakarta.xml.bind.JAXBElement;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The endpoint defines the operations of the service.
 */
@Endpoint
public class HelloEndpoint {
    @ResponsePayload
    @PayloadRoot(namespace = "http://soap.home.erzi.ch/types/ping_1_0", localPart = "pingRequest")
    public JAXBElement<PingResponse> ping10(@RequestPayload PingRequest pingRequest) {
        ch.erzi.home.soap.types.ping_1_0.ObjectFactory factory = new ch.erzi.home.soap.types.ping_1_0.ObjectFactory();
        PingResponse response = factory.createPingResponse();
        try {
            InetAddress ip = InetAddress.getLocalHost();
            response.setHostAddress(ip.getHostAddress());
            response.setHostName(ip.getHostName());
            return factory.createPingResponse(response);
        } catch (UnknownHostException e) {
            response.setHostName("localhost");
            response.setHostAddress("127.0.0.1");
            return factory.createPingResponse(response);
        }
    }

    @ResponsePayload
    @PayloadRoot(namespace = "http://soap.home.erzi.ch/types/hello", localPart = "helloRequest")
    public HelloResponse hello(@RequestPayload HelloRequest helloRequest) {
        ch.erzi.home.soap.types.hello.ObjectFactory factory = new ch.erzi.home.soap.types.hello.ObjectFactory();
        HelloResponse response = factory.createHelloResponse();
        response.setGreeting(String.format("Hello %s %s", helloRequest.getFirstName(), helloRequest.getLastName()));
        return response;
    }
}
