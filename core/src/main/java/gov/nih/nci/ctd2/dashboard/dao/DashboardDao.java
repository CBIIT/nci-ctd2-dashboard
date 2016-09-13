package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.SubjectWithSummaries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface DashboardDao {
    void save(DashboardEntity entity);
    void update(DashboardEntity entity);
    void merge(DashboardEntity entity);
    void delete(DashboardEntity entity);
    <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id);
    Long countEntities(Class<? extends DashboardEntity> entityClass);
    DashboardFactory getDashboardFactory();
    void setDashboardFactory(DashboardFactory dashboardFactory);
    <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass);
    List<Gene> findGenesByEntrezId(String entrezId);
    List<Gene> findGenesBySymbol(String symbol);
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
    void createIndex(int batchSize);
    ArrayList<DashboardEntityWithCounts> search(String keyword);
    List<Submission> findSubmissionByIsStory(boolean isSubmissionStory, boolean sortByPriority);
    List<Submission> findSubmissionByObservationTemplate(ObservationTemplate observationTemplate);
    Submission findSubmissionByName(String submissionName);
    List<Gene> browseTargets(String startsWith);
    List<Compound> browseCompounds(String startsWith);
    List<ObservationTemplate> findObservationTemplateBySubmissionCenter(SubmissionCenter submissionCenter);
    List<ObservedSubject> findObservedSubjectByRole(String role);
    List<SubjectWithSummaries> findSubjectWithSummariesByRole(String role, Integer minScore);
    List<Protein> findProteinByGene(Gene gene);
}

