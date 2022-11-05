package ch.erzberger.hello;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * This class provides the plumbing for the server
 */
@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    /**
     * Dispatch incoming http requests to the proper endpoint.
     */
    @Bean()
    ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        // Responsible for all requests starting with /services/
        return new ServletRegistrationBean<>(servlet, "/services/*");
    }

    /**
     * Creates and returns a wsdl definition out of the xsd and detected service methods.
     */
    @Bean(name = "hello")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema schema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("HelloPort");
        definition.setLocationUri("/services");
        definition.setTargetNamespace("https://soap.home.erzi.ch/services");
        definition.setSchema(schema);
        return definition;
    }

    /**
     * Loads the xsd file from the classpath (jar file, after everything is built)
     */
	@Bean
	public XsdSchema helloSchema() {
        return new SimpleXsdSchema(new ClassPathResource("wsdl/HelloService.xsd"));
    }
}