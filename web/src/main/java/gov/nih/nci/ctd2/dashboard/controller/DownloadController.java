package gov.nih.nci.ctd2.dashboard.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@RequestMapping("/download")
public class DownloadController {
    @Transactional
    @RequestMapping(value="template", method = {RequestMethod.POST})
    public void downloadTemplate(
            @RequestParam("filename") String filename,
            @RequestParam("template") String template,
            @RequestParam("metatemplate") String metatemplate,
            HttpServletResponse response)
    {
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");
        response.addHeader("Content-Transfer-Encoding", "binary");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            zipOutputStream.putNextEntry(new ZipEntry(filename + ".tsv"));
            zipOutputStream.write(template.getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.putNextEntry(new ZipEntry("meta-" + filename + ".tsv"));
            zipOutputStream.write(metatemplate.getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
