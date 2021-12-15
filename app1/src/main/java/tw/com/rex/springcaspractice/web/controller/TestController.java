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

    @PostMapping("/app2/{name}")
    public ResponseEntity<String> testApp2(@PathVariable String name, HttpServletRequest request)
            throws UnsupportedEncodingException {
        String app2Url = "http://localhost:9999/app2";
        // AttributePrincipal attributePrincipal = ((CasAuthenticationToken) principal).getAssertion().getPrincipal();
        // String proxyTicket = attributePrincipal.getProxyTicketFor(app2Url);
        // log.info("proxy ticket: {}", proxyTicket);
        // log.info("app1 get name: {}", name);
        // String requestUrl = app2Url + "/test/proxy/response/" + name + "?ticket=" + proxyTicket;
        // log.info("request url {}", requestUrl);
        // ResponseEntity<String> response = new RestTemplate().getForEntity(requestUrl, String.class);
        // log.info("response: {}", response.getBody());
        // return response;

        final CasAuthenticationToken token = (CasAuthenticationToken) request.getUserPrincipal();
        final String proxyTicket = token.getAssertion().getPrincipal().getProxyTicketFor(app2Url);
        log.info("proxyTicket: {}", proxyTicket);
        final String serviceUrl = app2Url + "/test/proxy/response/" + name + "?ticket=" + URLEncoder.encode(proxyTicket,
                                                                                                            "UTF-8");
        log.info("serviceUrl: {}", serviceUrl);
        String proxyResponse = CommonUtils.getResponseFromServer(serviceUrl, "UTF-8");
        log.info("proxyResponse: {}", proxyResponse);

        return ResponseEntity.ok("test");
    }

}
