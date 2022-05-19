package gov.nih.nci.ctd2.dashboard.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/html")
public class StoryProxyController {
    @Autowired
    @Qualifier("allowedProxyHosts")
    private String allowedProxyHosts = "";

    public String getAllowedProxyHosts() {
        return allowedProxyHosts;
    }

    public void setAllowedProxyHosts(String allowedProxyHosts) {
        this.allowedProxyHosts = allowedProxyHosts;
    }

    @Transactional
    @RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> convertSIFtoJSON(@RequestParam("url") String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");
        if (isURLValid(url)) {
            try (InputStream in = new URL(url).openStream()) {
                return new ResponseEntity<String>(new String(in.readAllBytes(), StandardCharsets.UTF_8), headers,
                        HttpStatus.OK);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new ResponseEntity<String>(headers, HttpStatus.NOT_FOUND);
    }

    private boolean isURLValid(String url) {
        if (!url.toLowerCase().endsWith(".html"))
            return false;

        String[] hosts = allowedProxyHosts.split(",", -1);
        for (String host : hosts)
            if (url.toLowerCase().startsWith(host.toLowerCase()))
                return true;

        return false;
    }
}
