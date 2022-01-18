package gov.nih.nci.ctd2.dashboard.util;

import java.util.Date;
import java.util.List;
import gov.nih.nci.ctd2.dashboard.model.Observation;

public class SearchResults {
    public List<SubjectResult> subject_result;
    public List<SubmissionResult> submission_result;
    public List<Observation> observation_result;

    public Boolean isEmpty() {
        return (subject_result == null || subject_result.isEmpty())
                && (submission_result == null || submission_result.isEmpty())
                && (observation_result == null || observation_result.isEmpty());
    }

    public int numberOfSubjects() {
        if (subject_result == null)
            return 0;
        else
            return subject_result.size();
    }

    // if greater 0, it shows the total number of results when the return size is
    // limited
    public int oversized = 0; // 'subjects', including ECO terms
    public int oversized_observations = 0;

    /*
     * this packs only the fields that are needed by the front-end, instead of the
     * bloated object of Submission
     */
    public static class SubmissionResult {
        final public String stableURL;
        final public Date submissionDate;
        final public String description;
        final public Integer tier;
        final public String centerName;
        final public Integer id;
        final public Integer observationCount;
        final public Boolean isStory;

        public SubmissionResult(String url, Date date, String description, Integer tier, String centerName, Integer id,
                Integer observationCount, Boolean isStory) {
            this.stableURL = url;
            this.submissionDate = date;
            this.description = description;
            this.tier = tier;
            this.centerName = centerName;
            this.id = id;
            this.observationCount = observationCount;
            this.isStory = isStory;
        }
    }
}
