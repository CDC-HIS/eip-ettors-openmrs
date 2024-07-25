package org.ephi.eip.routes;

import org.apache.camel.Endpoint;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.ephi.eip.config.OpenmrsConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@UseAdviceWith
@TestPropertySource(
        properties = {
                "eip.openmrs.serverUrl=http://localhost:8080/openmrs", "eip.openmrs.username=admin", "eip.openmrs.password=Admin123"
        }
)
class FetchOrderNumberRouteTest extends CamelSpringTestSupport {

    private static final String MOCK_OPENMRS = "mock:openmrs";

    private static AutoCloseable mocksCloser;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new StaticApplicationContext();
    }

    @Override
    protected RoutesBuilder createRouteBuilder() {
        FetchOrderNumberRoute fetchOrderNumberRoute = new FetchOrderNumberRoute();
        OpenmrsConfig openmrsConfig = mock(OpenmrsConfig.class);
        when(openmrsConfig.getServerUrl()).thenReturn("http://localhost:8080/openmrs");
        when(openmrsConfig.getUsername()).thenReturn("admin");
        when(openmrsConfig.getPassword()).thenReturn("password");
        when(openmrsConfig.basicAuthHeader()).thenReturn("basic auth");

        fetchOrderNumberRoute.setOpenmrsConfig(openmrsConfig);
        return fetchOrderNumberRoute;
    }

    @BeforeEach
    void setup() throws Exception {
        mocksCloser = openMocks(this);
        AdviceWith.adviceWith(context, "fetch-order-number", a -> {
            a.weaveByToUri("http://localhost:8080/openmrs/ws/rest/v1/order/${header.orderNumber}").replace().to("mock:openmrs");
        });

        Endpoint defaultEndpoint = context.getEndpoint("direct:fetch-order-number");
        template.setDefaultEndpoint(defaultEndpoint);
    }

    @AfterAll
    static void closeMocks() throws Exception {
        mocksCloser.close();
    }

    @Test
    void shouldFetchOrderNumber() throws InterruptedException {
        // setup expectations
        MockEndpoint openmrs = getMockEndpoint("mock:openmrs");
        openmrs.expectedMessageCount(1);
        openmrs.expectedHeaderReceived("Authorization", "basic auth");
        openmrs.expectedHeaderReceived("CamelHttpMethod", "GET");
        openmrs.expectedHeaderReceived("Content-Type", "application/json");

        // Act
        template.sendBodyAndHeader("direct:fetch-order-number", "{\"orderNumber\": \"123\"}", "orderNumber", "123");

        // assert
        openmrs.assertIsSatisfied();
    }
}
