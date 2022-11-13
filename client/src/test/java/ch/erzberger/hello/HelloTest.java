package ch.erzberger.hello;

import https.soap_home_erzi_ch.services.HelloPort;
import https.soap_home_erzi_ch.services.HelloPortService;
import https.soap_home_erzi_ch.services.HelloRequest;
import https.soap_home_erzi_ch.services.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloTest {
    HelloPortService service;
    HelloPort port;

    ObjectFactory factory;

    @BeforeEach
    void setUp() throws Exception {
        //service = new HelloPortService(new URL("https://localhost/services/HelloService.wsdl"));
        service = new HelloPortService();
        port = service.getHelloPortSoap11();
        factory = new ObjectFactory();
    }

    @Test
    void helloService() {
        HelloRequest request = factory.createHelloRequest();
        request.setFirstName("Adalbert");
        request.setLastName("Aal");
        assertEquals("Hello Adalbert Aal", port.hello(request));
    }
}
