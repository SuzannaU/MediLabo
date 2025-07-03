//package medilabo.gatewayapi.filters;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
////@Component
//public class AuthenticationTransmissionFilter  implements GlobalFilter, Ordered {
//
////    @Override
////    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
////        return exchange.getPrincipal()
////                .cast(Authentication.class)
////                .flatMap(auth -> {
////                    String username = auth.getName();
////                    ServerHttpRequest request = exchange.getRequest().mutate()
////                            .header("X-User-Name", username)
////                            .header("X-Authenticated", "true")
////                            .build();
////                    return chain.filter(exchange.mutate().request(request).build());
////                })
////                .switchIfEmpty(chain.filter(exchange));
////    }
////
////    /**
////     * Get the order value of this object.
////     * <p>Higher values are interpreted as lower priority. As a consequence,
////     * the object with the lowest value has the highest priority (somewhat
////     * analogous to Servlet {@code load-on-startup} values).
////     * <p>Same order values will result in arbitrary sort positions for the
////     * affected objects.
////     *
////     * @return the order value
////     * @see #HIGHEST_PRECEDENCE
////     * @see #LOWEST_PRECEDENCE
////     */
////    @Override
////    public int getOrder() {
////        return -1;
////    }
//}
