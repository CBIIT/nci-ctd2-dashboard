package gov.nih.nci.ctd2.dashboard.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
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
    @RequestMapping(value="clone", method = {RequestMethod.POST}, headers = "Accept=application/text")
    public 
    ResponseEntity<String>
    cloneSubmissionTemplate(
            @RequestParam("centerId") Integer centerId,
            @RequestParam("templateId") Integer templateId
            )
    {
        SubmissionTemplate existing = dashboardDao.getEntityById(SubmissionTemplate.class, templateId);
    	SubmissionTemplate template = new SubmissionTemplateImpl();
    	template.setDisplayName(existing.getDisplayName()+" - clone");
    	template.setDateLastModified(new Date());
    	SubmissionCenter submissionCenter = dashboardDao.getEntityById(SubmissionCenter.class, centerId);
    	template.setSubmissionCenter(submissionCenter);
    	template.setDescription(existing.getDescription());
    	template.setProject(existing.getProject());
        template.setTier(existing.getTier());
        template.setIsStory(existing.getIsStory());
    	template.setFirstName(existing.getFirstName());
    	template.setLastName(existing.getLastName());
        template.setEmail(existing.getEmail());
        template.setPhone(existing.getPhone());

        template.setSubjectColumns(existing.getSubjectColumns());
        template.setSubjectClasses(existing.getSubjectClasses());
        template.setSubjectRoles(existing.getSubjectRoles());
        template.setSubjectDescriptions(existing.getSubjectDescriptions());
        template.setEvidenceColumns(existing.getEvidenceColumns());
        template.setValueTypes(existing.getValueTypes());
        template.setEvidenceTypes(existing.getEvidenceTypes());
        template.setEvidenceDescriptions(existing.getEvidenceDescriptions());
        template.setObservationNumber(0);
        template.setObservations("");
        template.setSummary(existing.getSummary());

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
            @RequestParam("observations") String allObservations,
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

        String[] observations = allObservations.split(",", -1);
        String[] previousObservations = template.getObservations().split(",", -1);

        int subjectColumnCount = subjects.length;
        int evidenceColumnCount = evidences.length;
        int columnTagCount = subjectColumnCount + evidenceColumnCount;
        for(int i=0; i<valueTypes.length; i++) {
            if(valueTypes[i].equals("Document") || valueTypes[i].equals("Image")) {
                for(int j=0; j<observationNumber; j++) {
                    int index = columnTagCount*j + subjectColumnCount + i;
                    if(index>=observations.length) {
                        System.out.println("ERROR: observation index="+index+">= observation length="+observations.length);
                        continue;
                    }
                    String obv = observations[index];
                    if(obv==null || obv.indexOf(":")<=0) {
                        System.out.println("no new observation content for column#="+i+" observation#="+j+" observation="+obv);
                        if(index<previousObservations.length)observations[index] = previousObservations[index];
                        continue; // prevent later null pointer exception
                    }
                    String filename = uploadLocation + obv.substring(0, obv.indexOf(":"));
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
                    //new File(previousObservations[index]).delete(); // TODO cannot remove the previous upload safely. it may be used for a different observation
                    observations[index] = new File(filename).getAbsolutePath();
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<observations.length-1; i++){
            sb.append(observations[i]).append(",");
        }
        if(observations.length>1);
            sb.append(observations[observations.length-1]);
        template.setObservations(sb.toString());

        template.setSummary(summary);

        dashboardDao.update(template);

        return new ResponseEntity<String>("SubmissionTemplate " + templateId + " UPDATED", HttpStatus.OK);
    }

    private String[] uploadedFiles(Integer templateId) {
        SubmissionTemplate template = dashboardDao.getEntityById(SubmissionTemplate.class, templateId);
        String[] valueTypes = template.getValueTypes();
        Integer observationNumber = template.getObservationNumber();
        int subjectColumnCount = template.getSubjectColumns().length;
        int evidenceColumnCount = template.getEvidenceColumns().length;
        int columnTagCount = subjectColumnCount + evidenceColumnCount;
        String[] observations = template.getObservations().split(",", -1);
        Set<String> files = new HashSet<String>(); // duplicate entry not allowed in ZIP
        for(int i=0; i<valueTypes.length; i++) {
            if(valueTypes[i].equals("Document") || valueTypes[i].equals("Image")) {
                for(int j=0; j<observationNumber; j++) {
                    int index = columnTagCount*j + subjectColumnCount + i;
                    String obv = observations[index];
                    if(obv==null || obv.trim().length()==0) {
                        continue;
                    }
                    if(!new File(obv).exists()) {
                        System.out.println(obv+" not existing. evidence#="+i+" observation#="+j+" index="+index);
                        continue;
                    }
                    files.add(obv);
                }
            }
        }
        return files.toArray(new String[0]);
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("FirstSheet");

        CellStyle blue = workbook.createCellStyle();
        blue.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);
        blue.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle green = workbook.createCellStyle();
        green.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
        green.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle yellow = workbook.createCellStyle();
        yellow.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        yellow.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        CellStyle tan = workbook.createCellStyle();
        tan.setFillForegroundColor(HSSFColor.TAN.index);
        tan.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        HSSFRow rowhead = sheet.createRow((short)0);
        //rowhead.createCell(0).setCellValue(""); // default is the same as empty
        rowhead.createCell(1).setCellValue("submission_name");
        rowhead.createCell(2).setCellValue("submission_date");
        rowhead.createCell(3).setCellValue("template_name");
        String[] subjects = template.getSubjectColumns();
        for(int i=0; i<subjects.length; i++) {
            rowhead.createCell(i+4).setCellValue(subjects[i]);
        }
        String[] evd = template.getEvidenceColumns();
        for(int i=0; i<evd.length; i++) {
            rowhead.createCell(i+4+subjects.length).setCellValue(evd[i]);
        }

        HSSFRow row = sheet.createRow((short)1);
        row.setRowStyle(blue);
        Cell cell = row.createCell(0);
        cell.setCellValue("subject");
        cell.setCellStyle(blue);
        String[] classes = template.getSubjectClasses();
        for(int i=0; i<classes.length; i++) { // classes should have the same length as subject column
            cell = row.createCell(i+4);
            cell.setCellValue(classes[i]);
            cell.setCellStyle(blue);
        }

        row = sheet.createRow((short)2);
        row.setRowStyle(green);
        cell = row.createCell(0);
        cell.setCellValue("evidence");
        cell.setCellStyle(green);
        String[] valueType = template.getValueTypes();
        for(int i=0; i<valueType.length; i++) { // value types should have the same length as evidence column
            cell = row.createCell(i+4+subjects.length);
            cell.setCellValue(valueType[i]);
            cell.setCellStyle(green);
        }

        row = sheet.createRow((short)3);
        row.setRowStyle(yellow);
        cell = row.createCell(0);
        cell.setCellValue("role");
        cell.setCellStyle(yellow);
        String[] roles = template.getSubjectRoles();
        for(int i=0; i<roles.length; i++) {
            cell = row.createCell(i+4);
            cell.setCellValue(roles[i]);
            cell.setCellStyle(yellow);
        }

        row = sheet.createRow((short)4);
        row.setRowStyle(yellow);
        cell = row.createCell(0);
        cell.setCellValue("mime_types");
        cell.setCellStyle(yellow);

        row = sheet.createRow((short)5);
        row.setRowStyle(yellow);
        cell = row.createCell(0);
        cell.setCellValue("numeric_units");
        cell.setCellStyle(yellow);

        row = sheet.createRow((short)6);
        row.setRowStyle(yellow);
        cell = row.createCell(0);
        cell.setCellValue("display_text");
        cell.setCellStyle(yellow);
        String[] displayTexts = template.getSubjectDescriptions();
        for(int i=0; i<displayTexts.length; i++) {
            cell = row.createCell(i+4);
            cell.setCellValue(displayTexts[i]);
            cell.setCellStyle(yellow);
        }

        String[] obv = template.getObservations().split(",", -1);
        int index = 0;
        for(int i=0; i<template.getObservationNumber(); i++) {
            row = sheet.createRow((short)(7+i));
            row.setRowStyle(tan);
            cell = row.createCell(1);
            cell.setCellValue("observation "+(i+1));
            cell.setCellStyle(tan);
            cell = row.createCell(2);
            cell.setCellValue(template.getDateLastModified().toString());
            cell.setCellStyle(tan);
            cell = row.createCell(3);
            cell.setCellValue(template.getDisplayName());
            cell.setCellStyle(tan);
            for(int j=0; j<subjects.length; j++) {
                cell = row.createCell(j+4);
                cell.setCellValue( obv[index] );
                index++;
                cell.setCellStyle(tan);
            }
            for(int j=0; j<evd.length; j++) {
                cell = row.createCell(subjects.length+j+4);
                cell.setCellValue( obv[index] );
                index++;
                cell.setCellStyle(tan);
            }
        }

        int totalColumn = 4+subjects.length+evd.length;
        for(int i=0; i<totalColumn; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setContentType("application/zip");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip\"");
        response.addHeader("Content-Transfer-Encoding", "binary");

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            ZipOutputStream zipOutputStream = new ZipOutputStream(response.getOutputStream());
            zipOutputStream.putNextEntry(new ZipEntry("template"+templateId+".xls"));
            zipOutputStream.write(outputStream.toByteArray());
            zipOutputStream.closeEntry();

            String[] files = uploadedFiles(templateId);
            for(String f : files) {
                Path path = Paths.get(f);
                if(!path.toFile().exists()) { // this should not happen, but be cautious anyway
                    System.out.println("ERROR: uploaded file "+path.toFile()+" not found");
                    continue; 
                }
                zipOutputStream.putNextEntry(new ZipEntry( path.toFile().getName() ));
                zipOutputStream.write(Files.readAllBytes( path ));
                zipOutputStream.closeEntry();
            }

            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String uploadLocation = System.getProperty("user.home") + File.separator + "ctd2upload" + File.separator;
}
