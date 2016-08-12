package gov.nih.nci.ctd2.dashboard.controller;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.SubmissionTemplate;

@Controller
@RequestMapping("/template")
public class TemplateController {
    @Autowired
    private DashboardDao dashboardDao;

    @Transactional
    @RequestMapping(value="create", method = {RequestMethod.POST}, headers = "Accept=application/text")
    public 
    ResponseEntity<String>
    createNewSubmissionTemplate( /* the method name has no effect, @RequestMapping value binds this method */
            @RequestParam("name") String name
            )
    {
    	System.out.println("... ... creating submission template:"+name+"...");
    	SubmissionTemplate template = new SubmissionTemplate();
    	template.setDescription("description:"+name);
    	template.setSubmissionCenter("submission center:"+name);
    	dashboardDao.save(template);
    	System.out.println("=== === DONE with submission template:"+name+"...");

    	return new ResponseEntity<String>(name+" CREATED", HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value="download", method = {RequestMethod.POST})
    public void downloadTemplate(
            @RequestParam("filename") String filename,
            @RequestParam("template") String template,
            HttpServletResponse response)
    {
        System.out.println("... ... download controller is called:"+filename+"..."+template);
        
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");
        response.addHeader("Content-Transfer-Encoding", "binary");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            zipOutputStream.putNextEntry(new ZipEntry(filename + ".tsv"));
            zipOutputStream.write(template.getBytes());
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
