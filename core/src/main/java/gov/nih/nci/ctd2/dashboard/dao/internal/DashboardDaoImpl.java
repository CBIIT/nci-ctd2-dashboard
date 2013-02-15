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
    public List<Compound> findCompoundsByName(String compoundName) {
		List<Compound> compounds = new ArrayList<Compound>();

        for (Object o : getHibernateTemplate().find("from CompoundImpl where displayName = ?", compoundName)) {
            assert o instanceof Compound;
            compounds.add((Compound) o);
        }

        return compounds;
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
                        .find("from XrefImpl where databaseName = ? and databaseId = ?", databaseName, databaseId);
        for (Object o : list) {
            assert o instanceof Xref;
            subjects.addAll(findSubjectsByXref((Xref) o));
        }

        return new ArrayList<Subject>(subjects);
    }

    @Override
    public List<Subject> findSubjectsByXref(Xref xref) {
        List<Subject> list = new ArrayList<Subject>();
        for (Object o : getHibernateTemplate().find("select o from SubjectImpl as o where ? member of o.xrefs", xref)) {
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

    @Override
    public List<Subject> findSubjectsBySynonym(String synonym, boolean exact) {
        Set<Subject> subjects = new HashSet<Subject>();

        // First grab the synonyms
        String query = "from SynonymImpl where displayName "
                + (exact ? " = ?" : "like concat('%', ?, '%')");
        for (Object o : getHibernateTemplate().find(query, synonym)) {
            assert o instanceof Synonym;

            // Second: find subjects with the synonym
            List subjectList = getHibernateTemplate()
                    .find("select o from SubjectImpl as o where ? member of o.synonyms", (Synonym) o);
            for (Object o2 : subjectList) {
                assert o2 instanceof Subject;
                subjects.add((Subject) o2);
            }
        }

        return new ArrayList<Subject>(subjects);
    }

    @Override
    public ObservedSubjectRole findObservedSubjectRole(String templateName, String columnName) {
        List<ObservedSubjectRole> list = new ArrayList<ObservedSubjectRole>();
		// first grab observation template name
		for (Object ot : getHibernateTemplate()
				 .find("from ObservationTemplateImpl where displayName = ?", templateName)) {
			assert ot instanceof ObservationTemplate;
			for (Object o : getHibernateTemplate().
					 find("from ObservedSubjectRoleImpl as osr where columnName = ? and " +
						  "osr.observationTemplate = ?", columnName, (ObservationTemplate)ot)) {
				assert o instanceof ObservedSubjectRole;
				list.add((ObservedSubjectRole) o);
			}
		}
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
    }

    @Override
    public ObservedEvidenceRole findObservedEvidenceRole(String templateName, String columnName) {
        List<ObservedEvidenceRole> list = new ArrayList<ObservedEvidenceRole>();
		// first grab observation template name
		for (Object ot : getHibernateTemplate()
				 .find("from ObservationTemplateImpl where displayName = ?", templateName)) {
			assert ot instanceof ObservationTemplate;
			for (Object o : getHibernateTemplate()
					 .find("from ObservedEvidenceRoleImpl as oer where columnName = ? and " +
						   "oer.observationTemplate = ?", columnName, (ObservationTemplate)ot)) {
				assert o instanceof ObservedEvidenceRole;
				list.add((ObservedEvidenceRole) o);
			}
		}
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
    }

	@Override
    public ObservationTemplate findObservationTemplateByName(String templateName) {
		List<ObservationTemplate> list = new ArrayList<ObservationTemplate>();
        for (Object o : getHibernateTemplate()
				 .find("from ObservationTemplateImpl where displayName = ?", templateName)) {
            assert o instanceof ObservationTemplate;
            list.add((ObservationTemplate) o);
        }
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
	}

	@Override
    public SubmissionCenter findSubmissionCenterByName(String submissionCenterName) {
		List<SubmissionCenter> list = new ArrayList<SubmissionCenter>();
        for (Object o : getHibernateTemplate()
				 .find("from SubmissionCenterImpl where displayName = ?", submissionCenterName)) {
            assert o instanceof SubmissionCenter;
            list.add((SubmissionCenter) o);
        }
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
	}
}
