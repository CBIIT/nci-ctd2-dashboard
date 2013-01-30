package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.*;

import java.util.List;

public interface DashboardDao {
    void save(DashboardEntity entity);
    void update(DashboardEntity entity);
    void delete(DashboardEntity entity);
    DashboardEntity getEntityById(Integer id);
    <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id);
    Long countEntities(Class<? extends DashboardEntity> entityClass);
    DashboardFactory getDashboardFactory();
    void setDashboardFactory(DashboardFactory dashboardFactory);
    <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass);
    List<Gene> findGenesByEntrezId(String entrezId);
    List<Protein> findProteinsByUniprotId(String uniprotId);
    List<Transcript> findTranscriptsByRefseqId(String refseqId);
    List<Compound> findCompoundsBySmilesNotation(String smilesNotation);
    List<Subject> findSubjectsByXref(String databaseName, String databaseId);
    List<Subject> findSubjectsByXref(Xref xref);
}
