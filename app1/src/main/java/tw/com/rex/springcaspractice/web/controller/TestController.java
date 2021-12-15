package tw.com.rex.springcaspractice.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.cas.authentication.CasAuthenticationToken;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/principal")
    public ResponseEntity<Principal> getPrincipal(Principal principal) {
        log.info("principal: {}", principal);
        return ResponseEntity.ok(principal);
    }

    @PostMapping("/proxy/{name}")
    public ResponseEntity<String> testApp2(@PathVariable String name, HttpServletRequest request)
            throws UnsupportedEncodingException {
        String app2Url = "http://localhost:9999/app2/test/proxy/" + name;
        final CasAuthenticationToken token = (CasAuthenticationToken) request.getUserPrincipal();
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(app2Url);
        log.info("proxyTicket: {}", proxyTicket);
        final String serviceUrl = app2Url + "?ticket=" + URLEncoder.encode(proxyTicket, "UTF-8");
        log.info("serviceUrl: {}", serviceUrl);
        String proxyResponse = CommonUtils.getResponseFromServer(serviceUrl, "UTF-8");
        log.info("proxyResponse: {}", proxyResponse);

        return ResponseEntity.ok(proxyResponse);
    }

    @PostMapping("/proxy/again/{name}")
    public ResponseEntity<String> testApp2Again(@PathVariable String name) {
        String app2Url = "http://localhost:9999/app2/test/proxy/" + name;
        String proxyResponse = CommonUtils.getResponseFromServer(app2Url, "UTF-8");
        log.info("proxyResponse: {}", proxyResponse);
        return ResponseEntity.ok(proxyResponse);
    }

}
