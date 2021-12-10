package tw.com.rex.springcaspractice.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ProxyTicketValidator;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
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
    public ServiceProperties serviceProperties() {
        ServiceProperties properties = new ServiceProperties();
        properties.setService("http://localhost:8888/login/cas");
        properties.setSendRenew(false);
        return properties;
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl("http://localhost:8080/cas/login");
        entryPoint.setServiceProperties(serviceProperties());
        return entryPoint;
    }

    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider authenticationProvider = new CasAuthenticationProvider();
        authenticationProvider.setAuthenticationUserDetailsService(userDetailsService);
        authenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        authenticationProvider.setKey("TEST_KEY");
        authenticationProvider.setServiceProperties(serviceProperties());
        return authenticationProvider;
    }

    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ProxyTicketValidator("http://localhost:8080/cas");
    }

    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(
                "http://localhost:8080/cas/logout?service=http://localhost:8888",
                new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl("/logout");
        return logoutFilter;
    }

    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

}
