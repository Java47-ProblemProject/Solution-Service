package telran.solution.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final JwtRequestFilter jwtRequestFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeRequests(authorize -> authorize
//                       //User section//
                        .requestMatchers(HttpMethod.PUT, "/solution/addsolution/{problemId}")
                        .access("@customSecurity.checkProblemId(#problemId)")
                        .requestMatchers(HttpMethod.PUT, "/solution/editsolution/{profileId}/{problemId}/{solutionId}")
                        .access("@customSecurity.checkSolutionAuthorAndProblemId(#problemId, #solutionId, #profileId)")
                        .requestMatchers(HttpMethod.DELETE,"/solution/deletesolution/{profileId}/{problemId}/{solutionId}")
                        .access("@customSecurity.checkSolutionAuthorAndProblemId(#problemId, #solutionId, #profileId)")
                        .anyRequest()
                        .authenticated()
        );
        return http.build();
    }
}
