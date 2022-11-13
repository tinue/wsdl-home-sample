package ch.erzberger.hello;

import ch.erzberger.hello.jaxb.HelloRequest;
import ch.erzberger.hello.jaxb.ObjectFactory;
import jakarta.xml.bind.JAXBElement;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * The endpoint defines the operations of the service.
 */
@Endpoint
public class HelloEndpoint {
    // This variable contains the namespace of the XML. While it looks like a URL, it is just a convention to do this.
    // The service would still work on any machine with any URL as its server address.
    // On the other hand, tools like SoapUI use the namespace to construct a default endpoint address.
    // This is why it's a good idea to use the real URL of the server and not just some random namespace root.
    private static final String NAMESPACE_URI = "https://soap.home.erzi.ch/services";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "helloRequest")
    @ResponsePayload
    // One might be tempted to use a simple String as the response type. If this is done, the method is not found
    // by the mapper. The XML response on the client will contain a fault similar to
    // "No adapter for endpoint [public java.lang.String ch.erzberger.hello.HelloEndpoint.hello"
    // So: Use the ObjectFactory to construct the reply, and set the reply type accordingly.
    public JAXBElement<String> hello(@RequestPayload HelloRequest request) {
        ObjectFactory factory = new ObjectFactory();
        return factory.createHelloResponse(String.format("Hello %s %s", request.getFirstName(), request.getLastName()));
    }
}
