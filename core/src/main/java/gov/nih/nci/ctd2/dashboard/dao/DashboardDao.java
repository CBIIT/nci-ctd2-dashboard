package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.*;

import java.util.List;

public interface DashboardDao {
    void save(DashboardEntity entity);
    void update(DashboardEntity entity);
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
    List<Compound> findCompoundsByName(String compoundName);
    List<Compound> findCompoundsBySmilesNotation(String smilesNotation);
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
}
