package tw.com.rex.springcaspractice.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Profile("app1")
@RequiredArgsConstructor
@Configuration
public class App1SecurityConfig extends WebSecurityConfigurerAdapter {

    @NonNull
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsService;
    @NonNull
    private CasServerProperties casServerProperties;
    @NonNull
    private CasClientProperties casClientProperties;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().configurationSource(corsConfigurationSource()).and()
            .authorizeRequests().anyRequest().authenticated().and()
            .exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint()).and()
            .addFilter(casAuthenticationFilter())
            .addFilterBefore(logoutFilter(), LogoutFilter.class)
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/js/**", "webjars/**");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允許跨域請求的 client url
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080",
                                                      "http://localhost:8888",
                                                      "http://localhost:9999"));
        // 允許跨域請求的 method
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // 允許跨域請求的 header
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // 是否允許請求帶有驗證訊息
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties properties = new ServiceProperties();
        properties.setService(casClientProperties.getLogin());
        properties.setSendRenew(false);
        return properties;
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casServerProperties.getLogin());
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider authenticationProvider = new CasAuthenticationProvider();
        authenticationProvider.setAuthenticationUserDetailsService(userDetailsService);
        authenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        authenticationProvider.setKey("TEST_KEY");
        authenticationProvider.setServiceProperties(serviceProperties());
        return authenticationProvider;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casServerProperties.getPrefix());
    }

    @Bean
    public LogoutFilter logoutFilter() {
        String logoutSuccessUrl = casServerProperties.getLogout() + "?service=" + casClientProperties.getPrefix();
        LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessUrl, new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

}
