package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.SubjectWithSummaries;
import gov.nih.nci.ctd2.dashboard.util.Summary;
import gov.nih.nci.ctd2.dashboard.util.WordCloudEntry;
import gov.nih.nci.ctd2.dashboard.util.EcoBrowse;
import gov.nih.nci.ctd2.dashboard.util.ObservationURIsAndTiers;
import gov.nih.nci.ctd2.dashboard.util.SearchResults;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.nih.nci.ctd2.dashboard.api.ObservationItem;

public interface DashboardDao {
    void save(DashboardEntity entity);
    void update(DashboardEntity entity);
    void merge(DashboardEntity entity);
    void delete(DashboardEntity entity);
    <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id);
    <T extends DashboardEntity> T getEntityByStableURL(String type, String stableURL);
    Long countEntities(Class<? extends DashboardEntity> entityClass);
    DashboardFactory getDashboardFactory();
    void setDashboardFactory(DashboardFactory dashboardFactory);
    <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass);
    List<Gene> findGenesByEntrezId(String entrezId);
    List<Gene> findGenesBySymbol(String symbol);
    List<Gene> findGenesBySymbolCaseSensitive(String symbol);
    List<Protein> findProteinsByUniprotId(String uniprotId);
    List<Transcript> findTranscriptsByRefseqId(String refseqId);
    List<CellSample> findCellSampleByAnnoType(String type);
    List<CellSample> findCellSampleByAnnoSource(String source);
    List<CellSample> findCellSampleByAnnoName(String name);
    List<CellSample> findCellSampleByAnnotation(Annotation annotation);
    List<TissueSample> findTissueSampleByName(String name);
    List<CellSample> findCellLineByName(String name);
    List<ShRna> findSiRNAByReagentName(String reagent);
    List<ShRna> findSiRNAByTargetSequence(String targetSequence);
    List<Compound> findCompoundsByName(String compoundName);
    List<Compound> findCompoundsBySmilesNotation(String smilesNotation);
    List<AnimalModel> findAnimalModelByName(String animalModelName);
    List<Subject> findSubjectsByXref(String databaseName, String databaseId);
    List<Subject> findSubjectsByXref(Xref xref);
    List<Organism> findOrganismByTaxonomyId(String taxonomyId);
    List<SubjectWithOrganism> findSubjectByOrganism(Organism organism);
    List<Subject> findSubjectsBySynonym(String synonym, boolean exact);
    ObservedSubjectRole findObservedSubjectRole(String templateName, String columnName);
    ObservedEvidenceRole findObservedEvidenceRole(String templateName, String columnName);
    ObservationTemplate findObservationTemplateByName(String templateName);
    SubmissionCenter findSubmissionCenterByName(String submissionCenterName);
    List<Submission> findSubmissionBySubmissionCenter(SubmissionCenter submissionCenter);
    List<Observation> findObservationsBySubmission(Submission submission);
    List<ObservedSubject> findObservedSubjectBySubject(Subject subject);
    List<ObservedSubject> findObservedSubjectByObservation(Observation observation);
    List<ObservedEvidence> findObservedEvidenceByObservation(Observation observation);
    void batchSave(Collection<? extends DashboardEntity> entities, int batchSize);
    void batchMerge(Collection<? extends Subject> entities);
    void cleanIndex(int batchSize);
    SearchResults search(String keyword);
    List<Submission> findSubmissionByIsStory(boolean isSubmissionStory, boolean sortByPriority);
    List<Submission> findSubmissionByObservationTemplate(ObservationTemplate observationTemplate);
    Submission findSubmissionByName(String submissionName);
    List<Gene> browseTargets(String startsWith);
    List<Compound> browseCompounds(String startsWith);
    List<ObservationTemplate> findObservationTemplateBySubmissionCenter(SubmissionCenter submissionCenter);
    List<ObservedSubject> findObservedSubjectByRole(String role);
    List<SubjectWithSummaries> findSubjectWithSummariesByRole(String role, Integer minScore);
    List<Protein> findProteinByGene(Gene gene);
    Map<Observation, BigInteger> getOneObservationPerSubmission(Integer subjectId);
    String expandSummary(Integer observationId, String summaryTemplate);

    // the following are added to support more efficient API
    String[] findObservationURLs(Integer submissionId, int limit);
    List<ObservationItem> findObservationInfo(List<Integer> observationIds);

    void summarize();
    List<Summary> getOverallSummary();

    List<EcoBrowse> getEcoBrowse();
    ObservationURIsAndTiers ecoCode2ObservationURIsAndTiers(String ecoCode);
    Map<Observation, BigInteger> getOneObservationPerSubmissionByEcoCode(String ecocode, int tier);
    List<Observation> getObservationsForSubmissionAndEcoCode(Integer submissionId, String ecocode);
    ECOTerm getEcoTerm(String ecoTermCode);

    void prepareAPIData();

    SearchResults ontologySearch(String queryString);
    List<Submission> getSubmissionsForSubjectName(String subjectName);
    WordCloudEntry[] getSubjectCounts();
    WordCloudEntry[] getSubjectCountsForRoles(String[] roles);
    WordCloudEntry[] getSubjectCounts(Integer associatedSubject);

    ObservationItem getObservationInfo(String uri);
    ObservationItem[] getObservations(String submissionId, Set<Integer> indexes);

    void masterExport(String filename);
}
