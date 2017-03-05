package uk.co.giovannilenguito.filter;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

/**
 * Created by giovannilenguito on 19/02/2017.
 */
@Provider
public class ResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext containerResponseContext) {
        /*
        Sets headers in response to the OPTIONS request:
        "For browser to know that it's allowed to talk from js to another server it first need to do pre-flight
        request (options request) and see if server supports it."
         */

        containerResponseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
        containerResponseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
