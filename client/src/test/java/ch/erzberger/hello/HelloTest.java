package ch.erzberger.hello;

import ch.erzi.home.soap.services.HelloPort;
import ch.erzi.home.soap.services.HelloPortService;
import ch.erzi.home.soap.types.hello.HelloRequest;
import ch.erzi.home.soap.types.hello.ObjectFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloTest {
    HelloPortService service;
    HelloPort port;

    ObjectFactory factory;

    @BeforeEach
    void setUp() {
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
        assertEquals("Hello Adalbert Aal", port.hello(request).getGreeting());
    }
}
