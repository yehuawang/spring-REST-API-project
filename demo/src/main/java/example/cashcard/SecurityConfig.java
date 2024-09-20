package example.cashcard;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests( request -> request
                    .requestMatchers("/cashcards/**")
                    // .authenticated()) enable RBAC: replaced .authenticated() calls with the hasRole(...) call.
                    .hasRole("CARD-OWNER"))
            .httpBasic(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable());

        return http.build();

        /* 
        * In human language:
        * All HTTP requestes to `cashcards/` endpoints are required to be authenticated using
        * HTTP Basic Authentication security (username + password)
        * Also, do not require CSRF security
        */
            
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder users = User.builder();
        UserDetails sarah = users
            .username("sarah1")
            .password(passwordEncoder.encode("abc123"))
            .roles("CARD-OWNER") // new role
            .build();
        UserDetails hankOwnsNoCards = users
            .username("hank-owns-no-cards")
            .password(passwordEncoder.encode("qrs456"))
            .roles("NON-OWNER") // new role
            .build();

        return new InMemoryUserDetailsManager(sarah, hankOwnsNoCards);
    }

}