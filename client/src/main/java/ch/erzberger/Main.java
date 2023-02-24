package ch.erzberger;

import ch.erzi.home.soap.services.HelloPort;
import ch.erzi.home.soap.services.HelloPortService;
import ch.erzi.home.soap.types.hello.HelloRequest;
import ch.erzi.home.soap.types.hello.HelloResponse;
import ch.erzi.home.soap.types.ping_1_0.ObjectFactory;
import ch.erzi.home.soap.types.ping_1_0.PingRequest;
import ch.erzi.home.soap.types.ping_1_0.PingResponse;
import com.sun.xml.ws.developer.SchemaValidationFeature;

public class Main {
    public static void main(String[] args) {
        HelloPortService service = new HelloPortService();
        // Enable schema validation
        SchemaValidationFeature feature = new SchemaValidationFeature();
        HelloPort port = service.getHelloPortSoap11(feature);
        // Ping
        PingRequest request = new ObjectFactory().createPingRequest();
        PingResponse response = port.ping10(request);
        System.out.println("Hostname: " + response.getHostName());
        // Hello
        HelloRequest helloRequest = new HelloRequest();
        helloRequest.setFirstName("Adalbert");
        helloRequest.setLastName("Aal");
        HelloResponse helloResponse = port.hello(helloRequest);
        System.out.println("Hello reply: " + helloResponse.getGreeting());
    }
}