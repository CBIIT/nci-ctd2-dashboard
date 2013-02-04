package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DashboardDaoImpl extends HibernateDaoSupport implements DashboardDao {
    private DashboardFactory dashboardFactory;

    public DashboardFactory getDashboardFactory() {
        return dashboardFactory;
    }

    public void setDashboardFactory(DashboardFactory dashboardFactory) {
        this.dashboardFactory = dashboardFactory;
    }

    @Override
    public void save(DashboardEntity entity) {
        getHibernateTemplate().save(entity);
    }

    @Override
    public void update(DashboardEntity entity) {
        getHibernateTemplate().update(entity);
    }

    @Override
    public void delete(DashboardEntity entity) {
        getHibernateTemplate().delete(entity);
    }

    @Override
    public DashboardEntity getEntityById(Integer id) {
        return getHibernateTemplate().get(DashboardEntityImpl.class, id);
    }

    @Override
    public <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id) {
        Class<T> aClass = entityClass.isInterface()
                ? dashboardFactory.getImplClass(entityClass)
                : entityClass;
        return getHibernateTemplate().get(aClass, id);
    }

    @Override
    public Long countEntities(Class<? extends DashboardEntity> entityClass) {
        Criteria criteria = getSession().createCriteria(dashboardFactory.getImplClass(entityClass));
        return (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();
    }

    @Override
    public <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass) {
        List<T> list = new ArrayList<T>();
        Class<T> implClass = dashboardFactory.getImplClass(entityClass);
        Criteria criteria = getSession().createCriteria(implClass);
        for (Object o : criteria.list()) {
            assert implClass.isInstance(o);
            list.add((T) o);
        }
        return list;
    }

    @Override
    public List<Gene> findGenesByEntrezId(String entrezId) {
        List<Gene> list = new ArrayList<Gene>();
        for (Object o : getHibernateTemplate().find("from GeneImpl where entrezGeneId = ?", entrezId)) {
            assert o instanceof Gene;
            list.add((Gene) o);
        }
        return list;
    }

    @Override
    public List<Protein> findProteinsByUniprotId(String uniprotId) {
        List<Protein> list = new ArrayList<Protein>();
        for (Object o : getHibernateTemplate().find("from ProteinImpl where uniprotId = ?", uniprotId)) {
            assert o instanceof Protein;
            list.add((Protein) o);
        }
        return list;
    }

    @Override
    public List<Transcript> findTranscriptsByRefseqId(String refseqId) {
        List<Transcript> list = new ArrayList<Transcript>();
        for (Object o : getHibernateTemplate().find("from TranscriptImpl where refseqId = ?", refseqId)) {
            assert o instanceof Transcript;
            list.add((Transcript) o);
        }
        return list;
    }

    @Override
    public List<Compound> findCompoundsBySmilesNotation(String smilesNotation) {
        List<Compound> list = new ArrayList<Compound>();
        for (Object o : getHibernateTemplate().find("from CompoundImpl where smilesNotation = ?", smilesNotation)) {
            assert o instanceof Compound;
            list.add((Compound) o);
        }
        return list;
    }

    @Override
    public List<Subject> findSubjectsByXref(String databaseName, String databaseId) {
        Set<Subject> subjects = new HashSet<Subject>();
        List list = getHibernateTemplate()
                        .find("FROM XrefImpl WHERE databaseName = ? AND databaseId = ?", databaseName, databaseId);
        for (Object o : list) {
            assert o instanceof Xref;
            subjects.addAll(findSubjectsByXref((Xref) o));
        }

        return new ArrayList<Subject>(subjects);
    }

    @Override
    public List<Subject> findSubjectsByXref(Xref xref) {
        List<Subject> list = new ArrayList<Subject>();
        for (Object o : getHibernateTemplate().find("SELECT o FROM SubjectImpl AS o WHERE ? MEMBER OF o.xrefs", xref)) {
            assert o instanceof Subject;
            list.add((Subject) o);
        }
        return list;
    }

    @Override
    public List<Organism> findOrganismByTaxonomyId(String taxonomyId) {
        List<Organism> list = new ArrayList<Organism>();
        for (Object o : getHibernateTemplate().find("from OrganismImpl where taxonomyId = ?", taxonomyId)) {
            assert o instanceof Organism;
            list.add((Organism) o);
        }
        return list;
    }

    @Override
    public List<SubjectWithOrganism> findSubjectByOrganism(Organism organism) {
        List<SubjectWithOrganism> list = new ArrayList<SubjectWithOrganism>();
        for (Object o : getHibernateTemplate().find("from SubjectWithOrganismImpl where organism = ?", organism)) {
            assert o instanceof SubjectWithOrganism;
            list.add((SubjectWithOrganism) o);
        }

        return list;
    }
}