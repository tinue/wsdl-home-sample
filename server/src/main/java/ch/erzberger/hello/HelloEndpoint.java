package ch.erzberger.hello;

import ch.erzi.home.soap.services.HelloPort;
import ch.erzi.home.soap.types.hello.HelloRequest;
import ch.erzi.home.soap.types.ping_1_0.PingRequest;
import ch.erzi.home.soap.types.ping_1_0.PingResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The endpoint defines the operations of the service.
 */
@Endpoint
public class HelloEndpoint implements HelloPort {
    @Override
    public PingResponse ping10(PingRequest parameters) {
        PingResponse response = new PingResponse();
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
    public String hello(HelloRequest helloRequest) {
        return String.format("Hello %s %s", helloRequest.getFirstName(), helloRequest.getLastName());
    }
}
