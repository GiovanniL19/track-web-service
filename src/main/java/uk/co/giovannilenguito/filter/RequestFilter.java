package uk.co.giovannilenguito.filter;

import com.google.common.net.HttpHeaders;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.apache.log4j.Logger;
import uk.co.giovannilenguito.annotation.JWTRequired;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by giovannilenguito on 21/02/2017.
 */
@Provider
@JWTRequired
@Priority(Priorities.AUTHENTICATION)
public class RequestFilter implements ContainerRequestFilter {
    final private Logger LOGGER = Logger.getLogger(RequestFilter.class.getName());

    @Override
    public void filter(final ContainerRequestContext containerRequestContext) throws IOException {
        //Get Authorization header in request
        final String authorization = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        //Extract token from Authorization header
        final String jwt = authorization.substring("Bearer".length()).trim();

        LOGGER.info("Validating token...");
        //Validate the token, if token cannot be validated then request will abort
        try {
            Jwts.parser().setSigningKey("track").parseClaimsJws(jwt);
            LOGGER.info("Valid " + jwt);
        } catch (Exception ex) {
            LOGGER.warn(ex);
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
