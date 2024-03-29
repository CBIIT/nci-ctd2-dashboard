package gov.nih.nci.ctd2.dashboard.controller;

import flexjson.JSONSerializer;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.CyNetwork;
import gov.nih.nci.ctd2.dashboard.util.cytoscape.Element;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Scanner;

@Controller
@RequestMapping("/sif")
public class SifProxyController {
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
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, headers = "Accept=application/json")
    public ResponseEntity<String> convertSIFtoJSON(@RequestParam("url") String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        CyNetwork cyNetwork = new CyNetwork();

        if(isURLValid(url)) {
            // The following is a standard way to convert a SIF to a JSON Cytoscape.js model
            URLConnection urlConnection = null;
            try {
                urlConnection = new URL(url).openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                while(scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if(line.isEmpty()) continue;

                    String tokens[] = line.split("\t");
                    assert tokens.length == 3;
                    String source = tokens[0].trim();
                    String target = tokens[2].trim();

                    Element cyEdge = Element.createEdge(source + tokens[1].trim() + target, source, target);
                    cyNetwork.addEdge(cyEdge);
                }
                scanner.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONSerializer jsonSerializer = new JSONSerializer().exclude("*.class");
        return new ResponseEntity<String>(
                jsonSerializer.deepSerialize(cyNetwork),
                headers,
                HttpStatus.OK
        );
    }

    private boolean isURLValid(String url) {
        if(!url.toLowerCase().endsWith(".sif")) return false;

        try {
            URL x = new URL(url);
            for (String host : allowedProxyHosts.split(",")) {
                URL allowed = new URL(host);
                if (allowed.getHost().equals(x.getHost()))
                    return true;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return false;
    }


}
