package com.tnc.gateway.filter;

import com.tnc.gateway.util.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// Role: Intercept Incoming HTTP Requests => Check for the token, validate it and then let it pass.
// the @Component tells Springboot to automatically detect this class and manage it as a reusable bean wherever needed.
// AbstractGatewayFilterFactory -> A Special class by Spring cloud Gateway -> extending it, just a way to tell that am gonna define a custom filter to my routes.
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    
    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            //1. Catch the incoming request that came for routing => extract the header details for checking the jwt token.
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            
            //2. If token is missing -> send 401 UnAuthorized back
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            //3. Token exists => validate it.

            String token = authHeader.substring(7);    // trims out the intial Bearer string in the token.

            if (!JwtUtil.validateToken(token)) {
                exchange.getResponse()
                        .setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            return chain.filter(exchange);  // This line reached means the token is successful !
        };
    }

    public static class Config {
    }
}
