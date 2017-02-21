package filter;

import com.google.common.net.HttpHeaders;
import interfaces.JWTRequired;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.crypto.KeyGenerator;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;

/**
 * Created by giovannilenguito on 21/02/2017.
 */
@Provider
@JWTRequired
@Priority(Priorities.AUTHENTICATION)
public class JWTFilter implements ContainerRequestFilter {

    private KeyGenerator keyGenerator;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        System.out.println("Validating token...");
        //Validate the token, if token cannot be validated then request will abort
        try {
            //Get Authorization header in request
            String authorization = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
            //Extract token from Authorization header
            String jwt = authorization.substring("Bearer".length()).trim();

            Key key = keyGenerator.generateKey();
            Jwts.parser().setSigningKey(key).parseClaimsJws(jwt);

            System.out.println("Valid: " + jwt);
        } catch (Exception e) {
            System.out.println("Invalid token");
            containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
