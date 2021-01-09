package gov.nih.nci.ctd2.dashboard.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/string")
public class STRINGController {

    @Transactional
    @RequestMapping(value = "identifier", method = { RequestMethod.GET }, headers = "Accept=application/json")
    public ResponseEntity<String> getIdentifier(@RequestParam("genes") String genes) {
        String[] geneSymbols = genes.split(",");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");

        HttpClient httpClient = HttpClient.newBuilder().build();
        StringBuffer identifiers = new StringBuffer();
        try {
            for (String g : geneSymbols) {
                URI uri = URI.create("https://string-db.org/api/tsv/get_string_ids?identifiers=" + g + "&species=9606");
                HttpRequest request = HttpRequest.newBuilder().uri(uri).build();
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                String[] line = response.body().split("\n");
                String[] f = line[1].split("\\s");
                if (identifiers.length() > 0) {
                    identifiers.append("%0D");
                }
                identifiers.append(f[1]); // second field of the second line is the identifier
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String>(identifiers.toString(), headers, HttpStatus.OK);
    }
}
