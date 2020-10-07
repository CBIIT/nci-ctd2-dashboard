package gov.nih.nci.ctd2.dashboard.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    @RequestMapping(value="identifier", method = {RequestMethod.GET}, headers = "Accept=application/json")
    public ResponseEntity<String> getIdentifier(@RequestParam("genes") String genes) {
        String[] geneSymbols = genes.split(",");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/html; charset=utf-8");

        StringBuffer identifiers = new StringBuffer();
        try {
            for(String g: geneSymbols) {
                URL url = new URL("https://string-db.org/api/tsv/get_string_ids?identifiers="+g+"&species=9606");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
                String inputLine;
                int line = 0;
                while ((inputLine = in.readLine()) != null) {
                    if(line==1) { // second line is the content
                        String[] f = inputLine.split("\\s");
                        if(identifiers.length()>0) {
                            identifiers.append("%0D");
                        }
                        identifiers.append(f[1]); // second field is the identifier
                        break; // skip the rest lines
                    }
                    line++;
                }
                in.close();
                con.disconnect();
            }
        } catch(Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>(headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<String> (
                identifiers.toString(),
                headers,
                HttpStatus.OK
        );
    }
}
