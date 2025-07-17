package medilabo.patientsapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Security Filter Chain that sets up the authentication policy, and implements HTTP Basic authentication. It also disables CSRF protection since this service will only receive calls from the gateway service.
     * @param http HttpSecurity object
     * @return the filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth-> {
                    auth.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    /**
     * The in-memory user, ONLY for development phase. Should absolutely be replaced by proper user service before production.
     * @return the UserDetailsService used for authentication
     */
    @Bean
    public UserDetailsService userDetailsService() {
        String username = "${medilabo.user.username}";
        String password = "${medilabo.user.password}";
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder().encode(password))
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
