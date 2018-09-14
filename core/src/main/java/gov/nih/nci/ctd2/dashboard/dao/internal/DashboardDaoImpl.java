package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.*;
import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.SubjectWithSummaries;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.*;
import org.hibernate.criterion.Projections;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

public class DashboardDaoImpl implements DashboardDao {
    private static final Log log = LogFactory.getLog(DashboardDaoImpl.class);

    private static final String[] defaultSearchFields = {
            DashboardEntityImpl.FIELD_DISPLAYNAME,
            DashboardEntityImpl.FIELD_DISPLAYNAME_WS,
            DashboardEntityImpl.FIELD_DISPLAYNAME_UT,
            SubjectImpl.FIELD_SYNONYM,
            SubjectImpl.FIELD_SYNONYM_WS,
            SubjectImpl.FIELD_SYNONYM_UT,
            ObservationTemplateImpl.FIELD_DESCRIPTION,
            ObservationTemplateImpl.FIELD_SUBMISSIONDESC,
            ObservationTemplateImpl.FIELD_SUBMISSIONNAME,
            TissueSampleImpl.FIELD_LINEAGE
    };

    private static final Class<?>[] searchableClasses = {
            SubjectWithOrganismImpl.class,
            TissueSampleImpl.class,
            CompoundImpl.class,
            SubmissionImpl.class,
            ObservationTemplateImpl.class
    };

    private SessionFactory sessionFactory;

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        Session session = getSessionFactory().openSession();
        return session;
    }

    private DashboardFactory dashboardFactory;

    public DashboardFactory getDashboardFactory() {
        return dashboardFactory;
    }

    public void setDashboardFactory(DashboardFactory dashboardFactory) {
        this.dashboardFactory = dashboardFactory;
    }

    private Integer maxNumberOfSearchResults = 100;

    public Integer getMaxNumberOfSearchResults() {
        return maxNumberOfSearchResults;
    }

    public void setMaxNumberOfSearchResults(Integer maxNumberOfSearchResults) {
        this.maxNumberOfSearchResults = maxNumberOfSearchResults;
    }

    @Override
    public void save(DashboardEntity entity) {
        Session session = getSession();
        session.save(entity);
        session.flush();
        session.close();
    }

    @Override
    public void batchSave(Collection<? extends DashboardEntity> entities, int batchSize) {
        if(entities == null || entities.isEmpty())
            return;

        Session session = getSessionFactory().openSession();
        session.beginTransaction(); 
        int i = 0;
        for (DashboardEntity entity : entities) {
            if(entity instanceof Subject) {
                Subject subject = (Subject) entity;
                for(Xref x: subject.getXrefs()) {
                    session.save(x);
                }
                for(Synonym x: subject.getSynonyms()) {
                    session.save(x);
                }
            }
            session.save(entity);
            if(++i % batchSize == 0) {
                session.flush();
                session.clear();
            }
        }
        session.flush();
        session.clear();
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void update(DashboardEntity entity) {
        Session session = getSession();
        session.update(entity);
        session.flush();
        session.close();
    }

    @Override
    public void merge(DashboardEntity entity) {
        Session session = getSession();
        session.merge(entity);
        session.flush();
        session.close();
    }


    @Override
    public void delete(DashboardEntity entity) {
        Session session = getSession();
        session.delete(entity);
        session.flush();
        session.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id) {
        Class<T> aClass = entityClass.isInterface()
                ? dashboardFactory.getImplClass(entityClass)
                : entityClass;
        Session session = getSession();
        Object object = session.get(aClass, id);
        session.close();
        return (T)object;
    }

    final private static Map<String, String> typesWithStableURL = new HashMap<String, String>();
    static {
        typesWithStableURL.put("center", "SubmissionCenterImpl");
        typesWithStableURL.put("animal-model", "AnimalModelImpl");
        typesWithStableURL.put("cell-sample", "CellSampleImpl");
        typesWithStableURL.put("compound", "CompoundImpl");
        typesWithStableURL.put("protein", "ProteinImpl");
        typesWithStableURL.put("rna", "ShRnaImpl");
        typesWithStableURL.put("tissue", "TissueSampleImpl");
        typesWithStableURL.put("transcript", "TranscriptImpl");
        typesWithStableURL.put("submission", "SubmissionImpl");
        typesWithStableURL.put("observation", "ObservationImpl");
        typesWithStableURL.put("observedevidence", "ObservedEvidenceImpl");
    }
    @Override
    public <T extends DashboardEntity> T getEntityByStableURL(String type, String stableURL) {
        String implementationClass = typesWithStableURL.get(type);
        log.debug("getEntityByStableURL "+type+" "+stableURL+" "+implementationClass);
        if(implementationClass!=null) {
            List<T> r = queryWithClass("from "+implementationClass+" where stableURL = :urlId", "urlId", stableURL);
            if(r.size()==1) {
                return r.get(0);
            } else if(implementationClass.equals("ObservedEvidenceImpl") && r.size()>0) {
                /* This is to take care of a special case in the current data model implementation:
                multiple instances of the SAME evidence are created for multiple observations that refer to that evidence.
                */
                return r.get(0);
            } else if(r.size()==0){ // expected for incorrect URL (ID)
                log.info("no result found for "+stableURL);
                return null;
            } else {
                log.error("unexpected result number: "+r.size());
                return null;
            }
        } else {
            log.error("unrecognized type: "+type);
            return null;
        }
    }

    @Override
    public Long countEntities(Class<? extends DashboardEntity> entityClass) {
        Session session = getSession();
        Criteria criteria = session.createCriteria(dashboardFactory.getImplClass(entityClass));
        Object object = criteria.setProjection(Projections.rowCount()).uniqueResult();
        session.close();
        return (Long) object;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass) {
        List<T> list = new ArrayList<T>();
        Class<T> implClass = dashboardFactory.getImplClass(entityClass);
        Session session = getSession();
        Criteria criteria = session.createCriteria(implClass);
        for (Object o : criteria.list()) {
            assert implClass.isInstance(o);
            list.add((T) o);
        }
        session.close();
        return list;
    }

    @Override
    @Cacheable(value = "browseCompoundCache")
    public List<Compound> browseCompounds(String startsWith) {
        throw new java.lang.UnsupportedOperationException("not implemented");
    }

    @Override
    public List<ObservationTemplate> findObservationTemplateBySubmissionCenter(SubmissionCenter submissionCenter) {
        return queryWithClass("from ObservationTemplateImpl where submissionCenter = :center", "center", submissionCenter);
    }

    @Override
    @Cacheable(value = "browseTargetCache")
    public List<Gene> browseTargets(String startsWith) {
        throw new java.lang.UnsupportedOperationException("not implemented");
    }

    @Override
    public List<Gene> findGenesByEntrezId(String entrezId) {
        return queryWithClass("from GeneImpl where entrezGeneId = :entrezId", "entrezId", entrezId);
    }

    @Override
    public List<Gene> findGenesBySymbol(String symbol) {
        return queryWithClass("from GeneImpl where displayName = :symbol", "symbol", symbol);
    }

    @Override
    public List<Protein> findProteinsByUniprotId(String uniprotId) {
        return queryWithClass("from ProteinImpl where uniprotId = :uniprotId", "uniprotId", uniprotId);
    }

    @Override
    public List<Transcript> findTranscriptsByRefseqId(String refseqId) {
        String[] parts = refseqId.split("\\.");
        return queryWithClass("from TranscriptImpl where refseqId like :refseqId", "refseqId", parts[0] + "%");
    }

    private List<CellSample> findCellSampleByAnnotationField(String field, String value) {
        List<CellSample> cellSamples = new ArrayList<CellSample>();
        List<Annotation> annoList = queryWithClass("from AnnotationImpl where "+field+" = :value", "value", value);
        for (Annotation anno : annoList) {
            List<CellSample> list = queryWithClass("from CellSampleImpl as cs where :anno member of cs.annotations", "anno", anno);
            for (CellSample cellSample : list) {
                if (!cellSamples.contains(cellSample)) {
                    cellSamples.add(cellSample);
                }
            }
        }

        return cellSamples;
    }

	@Override
    public List<CellSample> findCellSampleByAnnoType(String type) {
        return findCellSampleByAnnotationField("type", type);
	}

    @Override
    public List<CellSample> findCellSampleByAnnoSource(String source) {
        return findCellSampleByAnnotationField("source", source);
	}

	@Override
    public List<CellSample> findCellSampleByAnnoName(String name) {
        return findCellSampleByAnnotationField("displayName", name);
	}

    @Override
    public List<CellSample> findCellSampleByAnnotation(Annotation annotation) {
        return queryWithClass("select cs from CellSampleImpl as cs where :anno member of cs.annotations", "anno", annotation);
    }

	@Override
    public List<TissueSample> findTissueSampleByName(String name) {
        return queryWithClass("from TissueSampleImpl where displayName = :name", "name", name);
	}

    @Override
    public List<CellSample> findCellLineByName(String name) {
        List<CellSample> cellSamples = new ArrayList<CellSample>();
        for (Subject subject : findSubjectsBySynonym(name, true)) {
            if(subject instanceof CellSample) {
                cellSamples.add((CellSample) subject);
            }
        }
        return cellSamples;
    }

    @Override
    public List<ShRna> findSiRNAByReagentName(String reagent) {
        return queryWithClass("from ShRnaImpl where reagentName = :reagentName", "reagentName", reagent);
    }
    
    @Override
    public List<ShRna> findSiRNAByTargetSequence(String targetSequence) {
        return queryWithClass("from ShRnaImpl where targetSequence = :targetSequence", "targetSequence", targetSequence);
    }

	@Override
    public List<Compound> findCompoundsByName(String compoundName) {
        return queryWithClass("from CompoundImpl where displayName = :displayName", "displayName", compoundName);
	}

    @Override
    public List<Compound> findCompoundsBySmilesNotation(String smilesNotation) {
        return queryWithClass("from CompoundImpl where smilesNotation = :smilesNotation", "smilesNotation", smilesNotation);
    }

	@Override
    public List<AnimalModel> findAnimalModelByName(String animalModelName) {
        return queryWithClass("from AnimalModelImpl where displayName = :aname", "aname", animalModelName);
	}

    @Override
    public List<Subject> findSubjectsByXref(String databaseName, String databaseId) {
        Set<Subject> subjects = new HashSet<Subject>();
        List<Xref> list = query2ParamsWithClass("from XrefImpl where databaseName = :dname and databaseId = :did",
            "dname", databaseName,
            "did", databaseId);
        for (Xref o : list) {
            subjects.addAll(findSubjectsByXref(o));
        }

        return new ArrayList<Subject>(subjects);
    }

    @Override
    public List<Subject> findSubjectsByXref(Xref xref) {
        return queryWithClass("select o from SubjectImpl o where :xref member of o.xrefs", "xref", xref);
    }

    @Override
    public List<Organism> findOrganismByTaxonomyId(String taxonomyId) {
        return queryWithClass("from OrganismImpl where taxonomyId = :tid", "tid", taxonomyId);
    }

    @Override
    public List<SubjectWithOrganism> findSubjectByOrganism(Organism organism) {
        return queryWithClass("from SubjectWithOrganismImpl where organism = :organism", "organism", organism);
    }

    @Override
    public List<Subject> findSubjectsBySynonym(String synonym, boolean exact) {
        Set<Subject> subjects = new HashSet<Subject>();

        // First grab the synonyms
        String query = "from SynonymImpl where displayName "
                + (exact ? " = :synonym" : "like concat('%', :synonym, '%')");
        List<Synonym> synonymList = queryWithClass(query, "synonym", synonym);
        for (Synonym o : synonymList) {
            // Second: find subjects with the synonym
            List<Subject> subjectList = queryWithClass("select o from SubjectImpl as o where :synonyms member of o.synonyms", "synonyms", o);
            for (Subject o2 : subjectList) {
                subjects.add(o2);
            }
        }

        return new ArrayList<Subject>(subjects);
    }

    @Override
    public ObservedSubjectRole findObservedSubjectRole(String templateName, String columnName) {
        List<ObservedSubjectRole> list = new ArrayList<ObservedSubjectRole>();
        // first grab observation template name
        List<ObservationTemplate> otList = queryWithClass("from ObservationTemplateImpl where displayName = :templateName", "templateName", templateName);
		for (ObservationTemplate ot : otList) {
            List<ObservedSubjectRole> osrList = query2ParamsWithClass("from ObservedSubjectRoleImpl as osr where columnName = :columnName and " +
                "osr.observationTemplate = :ot",
                "columnName", columnName,
                "ot", ot);
			for (ObservedSubjectRole o : osrList) {
				list.add(o);
			}
        }
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
    }

    @Override
    public ObservedEvidenceRole findObservedEvidenceRole(String templateName, String columnName) {
        List<ObservedEvidenceRole> list = new ArrayList<ObservedEvidenceRole>();
        // first grab observation template name
        List<ObservationTemplate> otList = queryWithClass("from ObservationTemplateImpl where displayName = :templateName", "templateName", templateName);
		for (ObservationTemplate ot : otList) {
            List<ObservedEvidenceRole> oerList = query2ParamsWithClass("from ObservedEvidenceRoleImpl as oer where columnName = :columnName and " +
                "oer.observationTemplate = :ot",
                "columnName", columnName,
                "ot", ot);
			for (ObservedEvidenceRole o : oerList) {
				list.add(o);
			}
        }
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
    }

	@Override
    public ObservationTemplate findObservationTemplateByName(String templateName) {
		List<ObservationTemplate> list = queryWithClass("from ObservationTemplateImpl where displayName = :tname", "tname", templateName);
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
	}

	@Override
    public SubmissionCenter findSubmissionCenterByName(String submissionCenterName) {
		List<SubmissionCenter> list = queryWithClass("from SubmissionCenterImpl where displayName = :cname", "cname", submissionCenterName);
		assert list.size() <= 1;
		return (list.size() == 1) ? list.iterator().next() : null;
	}

    @Override
    public List<Submission> findSubmissionByIsStory(boolean isSubmissionStory, boolean sortByPriority) {
        List<ObservationTemplate> tmpList1 = queryWithClass("from ObservationTemplateImpl where isSubmissionStory = :iss order by submissionStoryRank desc", "iss", isSubmissionStory);
        List<ObservationTemplate> tmpList2 = queryWithClass("from ObservationTemplateImpl where isSubmissionStory = :iss", "iss", isSubmissionStory);
        List<ObservationTemplate> tmpList = sortByPriority ? tmpList1 : tmpList2;

        List<Submission> list = new ArrayList<Submission>();
        for (ObservationTemplate o : tmpList) {
            list.addAll(findSubmissionByObservationTemplate(o));
        }

        return list;
    }

    @Override
    public List<Submission> findSubmissionByObservationTemplate(ObservationTemplate observationTemplate) {
        return queryWithClass("from SubmissionImpl where observationTemplate = :ot", "ot", observationTemplate);
    }

	@Override
    public Submission findSubmissionByName(String submissionName) {
        List<Submission> submissions = queryWithClass("from SubmissionImpl where displayName = :sname", "sname", submissionName);
        assert submissions.size() <= 1;
        return (submissions.size() == 1) ? submissions.iterator().next() : null;
	}

    @Override
    public List<Submission> findSubmissionBySubmissionCenter(SubmissionCenter submissionCenter) {
        List<Submission> list = new ArrayList<Submission>();
        for (ObservationTemplate o : findObservationTemplateBySubmissionCenter(submissionCenter)) {
            list.addAll(findSubmissionByObservationTemplate(o));
        }

        return list;
    }

    @Override
    public List<Observation> findObservationsBySubmission(Submission submission) {
        return queryWithClass("from ObservationImpl where submission = :submission", "submission", submission);
    }

    @Override
    public List<ObservedSubject> findObservedSubjectBySubject(Subject subject) {
        return queryWithClass("from ObservedSubjectImpl where subject = :subject ", "subject", subject);
    }

    @Override
    public List<ObservedSubject> findObservedSubjectByObservation(Observation observation) {
        return queryWithClass("from ObservedSubjectImpl where observation = :observation", "observation", observation);
    }

    @Override
    public List<ObservedEvidence> findObservedEvidenceByObservation(Observation observation) {
        return queryWithClass("from ObservedEvidenceImpl where observation = :observation", "observation", observation);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createIndex(int batchSize) {
        FullTextSession fullTextSession = Search.getFullTextSession(getSession());
        fullTextSession.setFlushMode(FlushMode.MANUAL);
        for (Class<?> searchableClass : searchableClasses) {
            createIndexForClass(fullTextSession, (Class<? extends DashboardEntity>)searchableClass, batchSize);
        }
        fullTextSession.flushToIndexes();
        fullTextSession.clear();
        fullTextSession.close();
    }

    private void createIndexForClass(FullTextSession fullTextSession, Class<? extends DashboardEntity> clazz, int batchSize) {
        ScrollableResults scrollableResults
                = fullTextSession.createCriteria(clazz).scroll(ScrollMode.FORWARD_ONLY);
        int cnt = 0;
        while(scrollableResults.next()) {
            DashboardEntity entity = (DashboardEntity) scrollableResults.get(0);
            fullTextSession.purge(DashboardEntityImpl.class, entity);
            fullTextSession.index(entity);

            if(++cnt % batchSize == 0) {
                fullTextSession.flushToIndexes();
                fullTextSession.clear();
            }
        }
    }

    /* defination of 'term': the complete exact entity name that is also a substring of the original query string */
    private static String getMatchedTerm(String queryString, String entityName) {
        String name = entityName.toLowerCase();
        if( queryString.toLowerCase().contains(name) ) return name;
        else return null; // intentionally to return null if it is not a 'term' as defined above
    }

    @Override
    @Cacheable(value = "searchCache")
    public ArrayList<DashboardEntityWithCounts> search(String keyword) {
        HashSet<DashboardEntity> entitiesUnique = new HashSet<DashboardEntity>();

        FullTextSession fullTextSession = Search.getFullTextSession(getSession());
        Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_36);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(
            Version.LUCENE_36,
                defaultSearchFields,
                analyzer
        );
        Query luceneQuery = null;
        try {
            luceneQuery = multiFieldQueryParser.parse(keyword);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, searchableClasses);
        fullTextQuery.setReadOnly(true);

        Integer numberOfSearchResults = getMaxNumberOfSearchResults();
        if(numberOfSearchResults > 0) { // if lte 0, don't set this.
                fullTextQuery.setMaxResults(numberOfSearchResults);
        }

        List<?> list = fullTextQuery.list();
        fullTextSession.close();
        for (Object o : list) {
            assert o instanceof DashboardEntity;

            if(o instanceof ObservationTemplate) {
                List<Submission> submissionList = queryWithClass("select o from SubmissionImpl as o where o.observationTemplate = :ot", "ot", (ObservationTemplate)o);
                for (Submission o2 : submissionList) {
                    if(!entitiesUnique.contains(o2)) entitiesUnique.add(o2);
                }
            } else {
                // Some objects came in as proxies, get the actual implementations for them when necessary
                if(o instanceof HibernateProxy) {
                    o = ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation();
                }

                if(!entitiesUnique.contains(o)) {
                    entitiesUnique.add((DashboardEntity) o);
                }
            }
        }

        ArrayList<DashboardEntityWithCounts> entitiesWithCounts = new ArrayList<DashboardEntityWithCounts>();
        Map<Observation, Set<String> > matchingObservations = new HashMap<Observation, Set<String> >();
        for (DashboardEntity entity : entitiesUnique) {
            DashboardEntityWithCounts entityWithCounts = new DashboardEntityWithCounts();
            entityWithCounts.setDashboardEntity(entity);
            if(entity instanceof Subject) {
                int observations = 0;
                int maxTier = 0;
                HashSet<SubmissionCenter> submissionCenters = new HashSet<SubmissionCenter>();
                HashSet<String> roles = new HashSet<String>();
                for (ObservedSubject observedSubject : findObservedSubjectBySubject((Subject) entity)) {
                    Observation observation = observedSubject.getObservation();
                    String term = getMatchedTerm(keyword, entity.getDisplayName());
                    if(term!=null) {
                        Set<String> terms = matchingObservations.get(observation);
                        if(terms==null) {
                            terms = new HashSet<String>();
                            matchingObservations.put(observation, terms);
                        }
                        terms.add(term);
                    }
                    observations++;
                    ObservationTemplate observationTemplate = observation.getSubmission().getObservationTemplate();
                    maxTier = Math.max(maxTier, observationTemplate.getTier());
                    submissionCenters.add(observationTemplate.getSubmissionCenter());
                    roles.add(observedSubject.getObservedSubjectRole().getSubjectRole().getDisplayName());
                }
                entityWithCounts.setObservationCount(observations);
                entityWithCounts.setMaxTier(maxTier);
                entityWithCounts.setRoles(roles);
                entityWithCounts.setCenterCount(submissionCenters.size());
            } else if(entity instanceof Submission) {
                entityWithCounts.setObservationCount(findObservationsBySubmission((Submission) entity).size());
                entityWithCounts.setMaxTier(((Submission) entity).getObservationTemplate().getTier());
                entityWithCounts.setCenterCount(1);
            }

            entitiesWithCounts.add(entityWithCounts);
        }

        // add observations
        Set<String> allTerms = new HashSet<String>();
        for(Observation ob: matchingObservations.keySet()) {
            for(String newTerm: matchingObservations.get(ob)) {
                Boolean add = true;
                Set<String> toBeRemoved = new HashSet<String>();
                for(String existingTerms: allTerms) {
                    if(existingTerms.contains(newTerm)) {
                        add = false;
                        break; // ignore this new term. no more comparison.
                    } else if(newTerm.contains(existingTerms)) {
                        toBeRemoved.add(existingTerms); // remove this existing term
                    }
                }
                if(add)allTerms.add(newTerm);
                allTerms.removeAll(toBeRemoved);
            }
            // allTerms.addAll(matchingObservations.get(ob)); // too imple that we have to give up this approach
        }
        if(allTerms.size()>1) { // do not find 'intersection' if there is only one term
            for(Observation ob: matchingObservations.keySet()) {
                Set<String> terms = matchingObservations.get(ob);
                if(!terms.containsAll(allTerms)) continue;
                DashboardEntityWithCounts oneObservationResult = new DashboardEntityWithCounts();
                oneObservationResult.setDashboardEntity(ob);
                entitiesWithCounts.add(oneObservationResult);
            }
        }

        return entitiesWithCounts;
    }

    @Override
    public List<ObservedSubject> findObservedSubjectByRole(String role) {
        return queryWithClass("from ObservedSubjectImpl where observedSubjectRole.subjectRole.displayName = :role", "role", role);
    }

    @Override
    public List<SubjectWithSummaries> findSubjectWithSummariesByRole(String role, Integer minScore) {
        return query2ParamsWithClass("from SubjectWithSummaries where role = :role and score > :score",
            "role", role,
            "score", minScore);
    }

    @Cacheable(value = "uniprotCache")
    @Override
    public List<Protein> findProteinByGene(Gene gene) {
        Set<Protein> proteins = new HashSet<Protein>();
        List<Transcript> transcriptList = queryWithClass("from TranscriptImpl where gene = :gene", "gene", gene);
        for(Transcript t: transcriptList) {
            List<Protein> list = queryWithClass("from ProteinImpl as p where :transcript member of p.transcripts", "transcript", t);
            for(Protein p: list) {
                proteins.add(p);
            }
        }

        return (new ArrayList<Protein>(proteins));
    }

    private <E> List<E> queryWithClass(String queryString, String parameterName, Object valueObject) {
        assert queryString.contains(":"+parameterName);
        Session session = getSession();
        org.hibernate.Query query = session.createQuery(queryString);
        query.setParameter(parameterName, valueObject);
        @SuppressWarnings("unchecked")
        List<E> list = query.list();
        session.close();

        return list;
    }

    private <E> List<E> query2ParamsWithClass(String queryString, String parameterName1, Object valueObject1,
            String parameterName2, Object valueObject2) {
        assert queryString.contains(":"+parameterName1);
        assert queryString.contains(":"+parameterName2);
        Session session = getSession();
        org.hibernate.Query query = session.createQuery(queryString);
        query.setParameter(parameterName1, valueObject1).setParameter(parameterName2, valueObject2);
        @SuppressWarnings("unchecked")
        List<E> list = query.list();
        session.close();

        return list;
    }
}
