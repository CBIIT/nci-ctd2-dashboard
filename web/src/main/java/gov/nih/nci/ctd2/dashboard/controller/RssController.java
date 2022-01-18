package gov.nih.nci.ctd2.dashboard.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.rometools.rome.feed.synd.SyndCategory;
import com.rometools.rome.feed.synd.SyndCategoryImpl;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndContentImpl;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndEntryImpl;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.feed.synd.SyndImageImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.util.SearchResults;
import gov.nih.nci.ctd2.dashboard.util.SubjectResult;

@Controller
@RequestMapping("/feed")
public class RssController {
    @Autowired
    private DashboardDao dashboardDao;

    @Autowired
    private HttpServletRequest context;
    
    @Transactional
    @RequestMapping(value="submissions", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> submissionRSS() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/rss+xml");

        List<Submission> submissions = dashboardDao.findEntities(Submission.class);

        String titlePostfix = "Submissions";
        String rssDescription = "Lates submissions on the CTD^2 Dashboard.";
        String dashboardUrl = context.getScheme() + "://" + context.getServerName() + context.getContextPath() + "/";
        String rssLink = dashboardUrl + "#centers";
        String feedStr = generateFeed(submissions, titlePostfix, rssDescription, rssLink);

        return new ResponseEntity<String>(
                feedStr,
                headers,
                HttpStatus.OK
        );
    }


    @Transactional
    @RequestMapping(value="stories", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> storiesRSS() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/rss+xml");

        List<Submission> submissions = dashboardDao.findEntities(Submission.class);
        List<Submission> stories = new ArrayList<Submission>();
        for (Submission submission : submissions) {
            if(submission.getObservationTemplate().getIsSubmissionStory()) {
                stories.add(submission);
            }
        }

        String titlePostfix = "Stories";
        String rssDescription = "Latest stories on the CTD^2 Dashboard.";
        String dashboardUrl = context.getScheme() + "://" + context.getServerName() + context.getContextPath() + "/";
        String rssLink = dashboardUrl + "#centers";
        String feedStr = generateFeed(stories, titlePostfix, rssDescription, rssLink);

        return new ResponseEntity<String>(
                feedStr,
                headers,
                HttpStatus.OK
        );
    }

    @Transactional
    @RequestMapping(value="search/{keyword}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> searchRSS(@PathVariable String keyword) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/rss+xml");

        // Do not allow search with really genetic keywords
        // This is to prevent unnecessary server loads
        if(keyword.length() < 2)
            return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
        try {
            keyword = URLDecoder.decode(keyword, Charset.defaultCharset().displayName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // Search and find the entity hits
        SearchResults entitiesWithCounts = dashboardDao.search(keyword);
        List<DashboardEntity> searchEntities = new ArrayList<DashboardEntity>();
        for (SubjectResult subjectResult : entitiesWithCounts.subject_result) {
            try {
                Class<? extends DashboardEntity> clazz = (Class<? extends DashboardEntity>) Class
                        .forName("gov.nih.nci.ctd2.dashboard.model." + subjectResult.className);
                DashboardEntity entity = dashboardDao.getEntityById(clazz, subjectResult.id);
                searchEntities.add(entity);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }
        }

        String titlePostfix = keyword;
        String rssDescription = "Latest observations and submission related to '" + keyword + "'";
        String dashboardUrl = context.getScheme() + "://" + context.getServerName() + context.getContextPath() + "/";
        String rssLink = dashboardUrl + "#search/" + keyword;
        String feedStr = generateFeed(searchEntities, titlePostfix, rssDescription, rssLink);

        return new ResponseEntity<String>(
                feedStr,
                headers,
                HttpStatus.OK
        );
    }

    private String generateFeed(List<? extends DashboardEntity> dashboardEntities,
                                String rssTitlePostfix,
                                String rssDescription,
                                String rssLink) {
        
        String dashboardUrl = context.getScheme() + "://" + context.getServerName() + context.getContextPath() + "/";
        
        // Set the stage and define metadata on the RSS feed
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("rss_2.0");
        feed.setTitle("CTD^2 Dashboard - " + rssTitlePostfix);
        feed.setDescription(rssDescription);
        feed.setLink(rssLink);

        // We will have two major categories: Subject and Submission
        //  and will have subcategories of Subject for each particular subject
        List<SyndCategory> categories = new ArrayList<SyndCategory>();
        SyndCategory subjectCategory = new SyndCategoryImpl();
        subjectCategory.setName("Subject");
        categories.add(subjectCategory);

        SyndCategory submissionCategory = new SyndCategoryImpl();
        submissionCategory.setName("Submission");
        categories.add(submissionCategory);

        feed.setCategories(categories);

        feed.setLanguage("en");
        feed.setManagingEditor("ocg@mail.nih.gov (CTD^2 Network)");

        SyndImage feedImg = new SyndImageImpl();
        feedImg.setTitle("CTD^2 Logo");
        feedImg.setUrl(dashboardUrl + "img/logos/ctd2_overall.png");
        feedImg.setLink(dashboardUrl);
        feed.setImage(feedImg);

        // And prepare the items to be put into the RSS
        List<SyndEntry> rssItems = new ArrayList<SyndEntry>();
        for (DashboardEntity entity : dashboardEntities) {
            if(entity instanceof Subject) {
                Subject subject = (Subject) entity;

                // Collect all role & submission pairs for this particular subject
                Map<Submission, Set<String>> roleMap = new HashMap<Submission, Set<String>>();
                Map<Submission, Set<ObservedSubject>> osMap = new HashMap<Submission, Set<ObservedSubject>>();
                List<ObservedSubject> sObservations = dashboardDao.findObservedSubjectBySubject((Subject) entity);
                for (ObservedSubject sObservation : sObservations) {
                    String role = sObservation.getObservedSubjectRole().getSubjectRole().getDisplayName();
                    Submission submission = sObservation.getObservation().getSubmission();

                    Set<String> roles = roleMap.get(submission);
                    if(roles == null) {
                        roles = new HashSet<String>();
                        roleMap.put(submission, roles);
                    }
                    roles.add(role);

                    Set<ObservedSubject> oses = osMap.get(submission);
                    if(oses == null) {
                        oses = new HashSet<ObservedSubject>();
                        osMap.put(submission, oses);
                    }
                    oses.add(sObservation);
                }

                // These will always belong to "Subject"
                List<SyndCategory> sCategories = new ArrayList<SyndCategory>();
                sCategories.add(subjectCategory);
                //  and "Subject/<name>" categories
                SyndCategory sCategory = new SyndCategoryImpl();
                sCategory.setName(subjectCategory.getName() + "/" + entity.getDisplayName());
                categories.add(sCategory);

                // Also add this specific category to the global list
                sCategories.add(sCategory);

                // And now combine this information into individual items around submissions/subject
                for (Submission submission : roleMap.keySet()) {
                    Set<String> roles = roleMap.get(submission);
                    assert roles != null;

                    Set<ObservedSubject> oses = osMap.get(submission);
                    assert oses != null;

                    SyndEntry item = new SyndEntryImpl();
                    item.setCategories(sCategories);

                    // Construct title
                    ObservationTemplate observationTemplate = submission.getObservationTemplate();
                    String description = observationTemplate.getDescription();
                    Integer tier = observationTemplate.getTier();
                    String combinedRoles = StringUtils.join(roles, ", ");
                    String name = subject.getDisplayName();
                    int noOfObservations = oses.size();
                    StringBuilder title = new StringBuilder();
                    title
                        .append("Role '").append(combinedRoles).append("'").append(": ")
                        .append(description).append(" ")
                        .append("(")
                            .append("Tier ").append(tier).append(" - ")
                            .append(noOfObservations).append(" ")
                                .append(noOfObservations > 1 ? "observations" : "observation")
                                .append(" on ").append(name)
                        .append(")");
                    item.setTitle(title.toString());

                    String centerName = observationTemplate.getSubmissionCenter().getDisplayName();
                    item.setAuthor(centerName);
                    item.setPublishedDate(submission.getSubmissionDate());

                    String submissionDescription = observationTemplate.getSubmissionDescription();
                    StringBuilder itemDescStr = new StringBuilder();
                    itemDescStr
                            .append("<b>Project</b>: ")
                            .append(observationTemplate.getProject())
                            .append("<br />");
                    if(submissionDescription != null && !submissionDescription.isEmpty()) {
                        itemDescStr.append("<b>Summary</b>: ").append(submissionDescription);
                    }
                    SyndContent itemDesc = new SyndContentImpl();
                    itemDesc.setType("text/html");
                    itemDesc.setValue(itemDescStr.toString());
                    item.setDescription(itemDesc);

                    // Create a link to the subject page with filters enabled
                    item.setLink(dashboardUrl + "#subject/" + subject.getId() + "/" + combinedRoles + "/" + tier);
                    item.setUri(
                        String.format(
                            "#ctd2#subject#%s#%d",
                            name,
                            title.hashCode() & Integer.MAX_VALUE
                        )
                    );

                    rssItems.add(item);
                }
                // End of subject-centered items
            } else if(entity instanceof Submission) {
                Submission submission = (Submission) entity;

                SyndEntry item = new SyndEntryImpl();
                ObservationTemplate observationTemplate = submission.getObservationTemplate();
                int noOfObservations = dashboardDao.findObservationsBySubmission(submission).size();

                StringBuilder title = new StringBuilder();
                Boolean isStory = observationTemplate.getIsSubmissionStory();
                title.append(isStory ? "Story" : "Submission").append(": ");
                title
                    .append(observationTemplate.getDescription().replaceAll("\\.$", ""))
                    .append(" (")
                        .append("Tier ").append(observationTemplate.getTier())
                        .append(" - ")
                        .append(noOfObservations)
                            .append(" ")
                            .append(noOfObservations > 1 ? "observations" : "observation")
                    .append(")");
                item.setTitle(title.toString());

                String submissionDescription = observationTemplate.getSubmissionDescription();
                StringBuilder itemDescStr = new StringBuilder();
                itemDescStr
                    .append("<b>Project</b>: ")
                    .append(observationTemplate.getProject())
                    .append("<br />");
                if(submissionDescription != null && !submissionDescription.isEmpty()) {
                    itemDescStr.append("<b>Summary</b>: ").append(submissionDescription);
                }
                SyndContent itemDesc = new SyndContentImpl();
                itemDesc.setType("text/html");
                itemDesc.setValue(itemDescStr.toString());
                item.setDescription(itemDesc);

                item.setPublishedDate(submission.getSubmissionDate());
                String centerName = observationTemplate.getSubmissionCenter().getDisplayName();
                item.setAuthor(centerName);

                item.setLink(dashboardUrl + "#submission/" + submission.getId());
                item.setUri(
                    String.format(
                            "ctd2#submission#%d",
                            title.hashCode() & Integer.MAX_VALUE
                    )
                );

                rssItems.add(item);
            } // End of submission item(s)
        }

        // Add all items into the feed
        feed.setEntries(rssItems);

        // And print the XML/RSS version out
        SyndFeedOutput feedOutput = new SyndFeedOutput();
        String feedStr = null;
        try {
            feedStr = feedOutput.outputString(feed, true);
        } catch (FeedException e) {
            e.printStackTrace();
        }
        return feedStr;
    }

}
