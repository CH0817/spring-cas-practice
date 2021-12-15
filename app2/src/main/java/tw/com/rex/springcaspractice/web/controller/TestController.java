package tw.com.rex.springcaspractice.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/proxy/{name}")
    public ResponseEntity<String> responseForApp1(@PathVariable String name) {
        log.info("app2 get name: {}", name);
        return ResponseEntity.ok("Hi~ " + name);
    }

}
