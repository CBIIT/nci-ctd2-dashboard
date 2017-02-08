package gov.nih.nci.ctd2.dashboard.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.SubmissionTemplateImpl;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.model.SubmissionTemplate;

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
            @RequestParam("centerId") Integer centerId,
            @RequestParam("name") String name,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("description") String description,
            @RequestParam("project") String project,
            @RequestParam("tier") Integer tier,
            @RequestParam("isStory") Boolean isStory
            )
    {
    	SubmissionTemplate template = new SubmissionTemplateImpl();
    	template.setDisplayName(name);
    	template.setDateLastModified(new Date());
    	SubmissionCenter submissionCenter = dashboardDao.getEntityById(SubmissionCenter.class, centerId);
    	template.setSubmissionCenter(submissionCenter);
    	template.setDescription(description);
    	template.setProject(project);
        template.setTier(tier);
        template.setIsStory(isStory);
    	template.setFirstName(firstName);
    	template.setLastName(lastName);
        template.setEmail(email);
        template.setPhone(phone);
    	dashboardDao.save(template);

    	return new ResponseEntity<String>(template.getId().toString(), HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value="update", method = {RequestMethod.POST}, headers = "Accept=application/text")
    public 
    ResponseEntity<String>
    updateSubmissionTemplate(
            @RequestParam("templateId") Integer templateId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("project") String project,
            @RequestParam("tier") Integer tier,
            @RequestParam("isStory") Boolean isStory,
            @RequestParam("subjects") String[] subjects,
            @RequestParam("subjectClasses") String[] subjectClasses,
            @RequestParam("subjectRoles") String[] subjectRoles,
            @RequestParam("subjectDescriptions") String[] subjectDescriptions,
            @RequestParam("evidences") String[] evidences,
            @RequestParam("evidenceTypes") String[] evidenceTypes,
            @RequestParam("valueTypes") String[] valueTypes,
            @RequestParam("evidenceDescriptions") String[] evidenceDescriptions,
            @RequestParam("observationNumber") Integer observationNumber,
            @RequestParam("observations") String[] observations,
            @RequestParam("summary") String summary
            )
    {
        SubmissionTemplate template = dashboardDao.getEntityById(SubmissionTemplate.class, templateId);
    	template.setDisplayName(name);
    	template.setDateLastModified(new Date());
    	template.setDescription(description);
    	template.setProject(project);
    	template.setTier(tier);
        template.setIsStory(isStory);
    	template.setFirstName(firstName);
    	template.setLastName(lastName);
        template.setEmail(email);
        template.setPhone(phone);

        template.setSubjectColumns(subjects);
        template.setSubjectClasses(subjectClasses);
        template.setSubjectRoles(subjectRoles);
        template.setSubjectDescriptions(subjectDescriptions);
        template.setEvidenceColumns(evidences);
        template.setEvidenceTypes(evidenceTypes);
        template.setValueTypes(valueTypes);
        template.setEvidenceDescriptions(evidenceDescriptions);
        template.setObservationNumber(observationNumber);

        int subjectColumnCount = subjects.length;
        int evidenceColumnCount = evidences.length;
        int columnTagCount = subjectColumnCount + evidenceColumnCount;
        for(int i=0; i<valueTypes.length; i++) {
            if(valueTypes[i].equals("Document") || valueTypes[i].equals("Image")) {
                for(int j=0; j<observationNumber; j++) {
                    int index = columnTagCount*j + subjectColumnCount + i;
                    String obv = observations[index];
                    if(obv==null || obv.length()<10) {
                        System.out.println("no observation content for i="+i+" j="+j+" observation="+obv);
                        continue; // prevent later null pointer exception
                    }
                    String filename = obv.substring(0, obv.indexOf(":"));
                    FileOutputStream stream = null;
                    try {
                        byte[] bytes = DatatypeConverter.parseBase64Binary(obv.substring( obv.indexOf("base64:")+7 ));
                        stream = new FileOutputStream(filename);
                        stream.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if(stream!=null)
                            try {
                                stream.close();
                            } catch (IOException e) {
                            }
                    }
                    observations[index] = new File(filename).getAbsolutePath();
                }
            }
        }
        template.setObservations(observations);

        template.setSummary(summary);

        dashboardDao.update(template);

        return new ResponseEntity<String>("SubmissionTemplate " + templateId + " UPDATED", HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value="delete", method = {RequestMethod.POST}, headers = "Accept=application/text")
    public 
    ResponseEntity<String>
    deleteSubmissionTemplate(
            @RequestParam("templateId") Integer templateId
            )
    {
        SubmissionTemplate template = dashboardDao.getEntityById(SubmissionTemplate.class, templateId);
        dashboardDao.delete(template);
        return new ResponseEntity<String>("SubmissionTemplate " + templateId + " DELETED", HttpStatus.OK);
    }

    @Transactional
    @RequestMapping(value="download", method = {RequestMethod.POST})
    public void downloadTemplate(
            @RequestParam("template-id") Integer templateId,
            @RequestParam("filename") String filename,
            HttpServletResponse response)
    {
        SubmissionTemplate template = dashboardDao.getEntityById(SubmissionTemplate.class, templateId);
        String xlsFile = "excelFile.xls" ;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("FirstSheet");  

        HSSFRow rowhead = sheet.createRow((short)0);
        //rowhead.createCell(0).setCellValue(""); // TODO check if default is the same as empty
        rowhead.createCell(1).setCellValue("submission_name");
        rowhead.createCell(2).setCellValue("submission_date");
        rowhead.createCell(3).setCellValue("template_name");
        String[] subjects = template.getSubjectColumns();
        for(int i=0; i<subjects.length; i++) {
            rowhead.createCell(i+3).setCellValue(subjects[i]);
        }

        HSSFRow row = sheet.createRow((short)1);
        for(int i=0; i<3; i++) row.createCell(i).setCellValue("");
        String[] classes = template.getSubjectClasses();
        for(int i=0; i<classes.length; i++) { // classes should have the same length as subject column
            row.createCell(i+3).setCellValue(classes[i]);
        }

        HSSFRow lastrow = sheet.createRow((short)2);
        lastrow.createCell(0).setCellValue("");
        lastrow.createCell(1).setCellValue(template.getDisplayName());
        lastrow.createCell(2).setCellValue(new Date());
        lastrow.createCell(3).setCellValue(template.getDisplayName());

        FileOutputStream fileOut;
        try {
            fileOut = new FileOutputStream(xlsFile);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("... ... download controller is called: zip file name="+filename+", tempalte Id="+templateId);
        
        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");
        response.addHeader("Content-Transfer-Encoding", "binary");

        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            zipOutputStream.putNextEntry(new ZipEntry(xlsFile));
            zipOutputStream.write(Files.readAllBytes( Paths.get(xlsFile) ));
            zipOutputStream.closeEntry();

            String attached = "blue-jay.jpg"; // TODO placeholder
            zipOutputStream.putNextEntry(new ZipEntry(attached));
            zipOutputStream.write(Files.readAllBytes( Paths.get(attached) ));
            zipOutputStream.closeEntry();

            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
