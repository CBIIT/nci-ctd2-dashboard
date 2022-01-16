package gov.nih.nci.ctd2.dashboard.util;

import java.util.List;
import gov.nih.nci.ctd2.dashboard.model.Observation;

public class SearchResults {
    public List<DashboardEntityWithCounts> subject_result;
    public List<DashboardEntityWithCounts> submission_result;
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
}
