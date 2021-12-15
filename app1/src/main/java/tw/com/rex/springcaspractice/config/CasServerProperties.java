package tw.com.rex.springcaspractice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cas.server")
public class CasServerProperties {

    private String prefix;
    private String login;
    private String logout;

}
