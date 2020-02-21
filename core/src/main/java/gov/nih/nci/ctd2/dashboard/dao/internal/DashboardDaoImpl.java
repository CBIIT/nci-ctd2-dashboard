package gov.nih.nci.ctd2.dashboard.dao.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.Query;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.springframework.cache.annotation.Cacheable;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.CompoundImpl;
import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.impl.ObservationTemplateImpl;
import gov.nih.nci.ctd2.dashboard.impl.SubjectImpl;
import gov.nih.nci.ctd2.dashboard.impl.SubjectWithOrganismImpl;
import gov.nih.nci.ctd2.dashboard.impl.SubmissionImpl;
import gov.nih.nci.ctd2.dashboard.impl.TissueSampleImpl;
import gov.nih.nci.ctd2.dashboard.model.AnimalModel;
import gov.nih.nci.ctd2.dashboard.model.Annotation;
import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import gov.nih.nci.ctd2.dashboard.model.ECOTerm;
import gov.nih.nci.ctd2.dashboard.model.Evidence;
import gov.nih.nci.ctd2.dashboard.model.Gene;
import gov.nih.nci.ctd2.dashboard.model.Observation;
import gov.nih.nci.ctd2.dashboard.model.ObservationTemplate;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidence;
import gov.nih.nci.ctd2.dashboard.model.ObservedEvidenceRole;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubject;
import gov.nih.nci.ctd2.dashboard.model.ObservedSubjectRole;
import gov.nih.nci.ctd2.dashboard.model.Organism;
import gov.nih.nci.ctd2.dashboard.model.Protein;
import gov.nih.nci.ctd2.dashboard.model.ShRna;
import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.SubjectWithOrganism;
import gov.nih.nci.ctd2.dashboard.model.Submission;
import gov.nih.nci.ctd2.dashboard.model.SubmissionCenter;
import gov.nih.nci.ctd2.dashboard.model.Synonym;
import gov.nih.nci.ctd2.dashboard.model.TissueSample;
import gov.nih.nci.ctd2.dashboard.model.Transcript;
import gov.nih.nci.ctd2.dashboard.model.Xref;
import gov.nih.nci.ctd2.dashboard.util.DashboardEntityWithCounts;
import gov.nih.nci.ctd2.dashboard.util.EcoBrowse;
import gov.nih.nci.ctd2.dashboard.util.SubjectWithSummaries;
import gov.nih.nci.ctd2.dashboard.util.Summary;
import gov.nih.nci.ctd2.dashboard.api.EvidenceItem;
import gov.nih.nci.ctd2.dashboard.api.SubjectItem;
import gov.nih.nci.ctd2.dashboard.api.XRefItem;

public class DashboardDaoImpl implements DashboardDao {
    private static final Log log = LogFactory.getLog(DashboardDaoImpl.class);

    private static final String[] defaultSearchFields = { DashboardEntityImpl.FIELD_DISPLAYNAME,
            DashboardEntityImpl.FIELD_DISPLAYNAME_WS, DashboardEntityImpl.FIELD_DISPLAYNAME_UT,
            SubjectImpl.FIELD_SYNONYM, SubjectImpl.FIELD_SYNONYM_WS, SubjectImpl.FIELD_SYNONYM_UT,
            ObservationTemplateImpl.FIELD_DESCRIPTION, ObservationTemplateImpl.FIELD_SUBMISSIONDESC,
            ObservationTemplateImpl.FIELD_SUBMISSIONNAME, TissueSampleImpl.FIELD_LINEAGE };

    private static final Class<?>[] searchableClasses = { SubjectWithOrganismImpl.class, TissueSampleImpl.class,
            CompoundImpl.class, SubmissionImpl.class, ObservationTemplateImpl.class };

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
        session.beginTransaction();
        session.save(entity);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void batchSave(Collection<? extends DashboardEntity> entities, int batchSize) {
        if (entities == null || entities.isEmpty())
            return;

        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        int i = 0;
        for (DashboardEntity entity : entities) {
            if (entity instanceof Subject) {
                Subject subject = (Subject) entity;
                for (Xref x : subject.getXrefs()) {
                    session.save(x);
                }
                for (Synonym x : subject.getSynonyms()) {
                    session.save(x);
                }
            }
            session.save(entity);
            i++;
            if (batchSize != 0 && i % batchSize == 0) {
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
    public void batchMerge(Collection<? extends Subject> subjects) {
        if (subjects == null || subjects.isEmpty())
            return;

        Session session = getSessionFactory().openSession();
        session.beginTransaction();
        for (Subject subject : subjects) {
            session.merge(subject);
        }
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void merge(DashboardEntity entity) {
        Session session = getSession();
        session.beginTransaction();
        session.merge(entity);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public void delete(DashboardEntity entity) {
        Session session = getSession();
        session.beginTransaction();
        session.delete(entity);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DashboardEntity> T getEntityById(Class<T> entityClass, Integer id) {
        Class<T> aClass = entityClass.isInterface() ? dashboardFactory.getImplClass(entityClass) : entityClass;
        Session session = getSession();
        Object object = session.get(aClass, id);
        session.close();
        return (T) object;
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
        typesWithStableURL.put("eco", "ECOTermImpl");
    }

    @Override
    public <T extends DashboardEntity> T getEntityByStableURL(String type, String stableURL) {
        String implementationClass = typesWithStableURL.get(type);
        log.debug("getEntityByStableURL " + type + " " + stableURL + " " + implementationClass);
        if (implementationClass != null) {
            List<T> r = queryWithClass("from " + implementationClass + " where stableURL = :urlId", "urlId", stableURL);
            if (r.size() == 1) {
                return r.get(0);
            } else if (implementationClass.equals("ObservedEvidenceImpl") && r.size() > 0) {
                /*
                 * This is to take care of a special case in the current data model
                 * implementation: multiple instances of the SAME evidence are created for
                 * multiple observations that refer to that evidence.
                 */
                return r.get(0);
            } else if (r.size() == 0) { // expected for incorrect URL (ID)
                log.info("no result found for " + stableURL);
                return null;
            } else {
                log.error("unexpected result number: " + r.size());
                return null;
            }
        } else {
            log.error("unrecognized type: " + type);
            return null;
        }
    }

    @Override
    public Long countEntities(Class<? extends DashboardEntity> entityClass) {
        Session session = getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(dashboardFactory.getImplClass(entityClass))));
        TypedQuery<Long> typedQuery = session.createQuery(cq);
        Long count = typedQuery.getSingleResult();
        session.close();
        return count;
    }

    @Override
    public <T extends DashboardEntity> List<T> findEntities(Class<T> entityClass) {
        Class<T> implClass = dashboardFactory.getImplClass(entityClass);
        Session session = getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(implClass);
        cq.from(implClass); // ignore the return value Root<T>
        TypedQuery<T> typedQuery = session.createQuery(cq);
        List<T> list = typedQuery.getResultList();
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
        return queryWithClass("from ObservationTemplateImpl where submissionCenter = :center", "center",
                submissionCenter);
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
    public List<Gene> findGenesBySymbolCaseSensitive(String symbol) {
        List<Gene> caseInsensitive = queryWithClass("from GeneImpl where displayName = :symbol", "symbol", symbol);
        // only case-sensitive match is accepted
        List<Gene> list = new ArrayList<Gene>();
        for (Gene gene : caseInsensitive) {
            if (gene.getDisplayName().equals(symbol))
                list.add(gene);
        }
        return list;
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
        List<Annotation> annoList = queryWithClass("from AnnotationImpl where " + field + " = :value", "value", value);
        for (Annotation anno : annoList) {
            List<CellSample> list = queryWithClass("from CellSampleImpl as cs where :anno member of cs.annotations",
                    "anno", anno);
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
        return queryWithClass("select cs from CellSampleImpl as cs where :anno member of cs.annotations", "anno",
                annotation);
    }

    @Override
    public List<TissueSample> findTissueSampleByName(String name) {
        return queryWithClass("from TissueSampleImpl where displayName = :name", "name", name);
    }

    @Override
    public List<CellSample> findCellLineByName(String name) {
        List<CellSample> cellSamples = new ArrayList<CellSample>();
        for (Subject subject : findSubjectsBySynonym(name, true)) {
            if (subject instanceof CellSample) {
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
        return queryWithClass("from ShRnaImpl where targetSequence = :targetSequence", "targetSequence",
                targetSequence);
    }

    @Override
    public List<Compound> findCompoundsByName(String compoundName) {
        return queryWithClass("from CompoundImpl where displayName = :displayName", "displayName", compoundName);
    }

    @Override
    public List<Compound> findCompoundsBySmilesNotation(String smilesNotation) {
        return queryWithClass("from CompoundImpl where smilesNotation = :smilesNotation", "smilesNotation",
                smilesNotation);
    }

    @Override
    public List<AnimalModel> findAnimalModelByName(String animalModelName) {
        return queryWithClass("from AnimalModelImpl where displayName = :aname", "aname", animalModelName);
    }

    @Override
    public List<Subject> findSubjectsByXref(String databaseName, String databaseId) {
        Set<Subject> subjects = new HashSet<Subject>();
        List<Xref> list = query2ParamsWithClass("from XrefImpl where databaseName = :dname and databaseId = :did",
                "dname", databaseName, "did", databaseId);
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
            List<Subject> subjectList = queryWithClass(
                    "select o from SubjectImpl as o where :synonyms member of o.synonyms", "synonyms", o);
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
        List<ObservationTemplate> otList = queryWithClass(
                "from ObservationTemplateImpl where displayName = :templateName", "templateName", templateName);
        for (ObservationTemplate ot : otList) {
            List<ObservedSubjectRole> osrList = query2ParamsWithClass(
                    "from ObservedSubjectRoleImpl as osr where columnName = :columnName and "
                            + "osr.observationTemplate = :ot",
                    "columnName", columnName, "ot", ot);
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
        List<ObservationTemplate> otList = queryWithClass(
                "from ObservationTemplateImpl where displayName = :templateName", "templateName", templateName);
        for (ObservationTemplate ot : otList) {
            List<ObservedEvidenceRole> oerList = query2ParamsWithClass(
                    "from ObservedEvidenceRoleImpl as oer where columnName = :columnName and "
                            + "oer.observationTemplate = :ot",
                    "columnName", columnName, "ot", ot);
            for (ObservedEvidenceRole o : oerList) {
                list.add(o);
            }
        }
        assert list.size() <= 1;
        return (list.size() == 1) ? list.iterator().next() : null;
    }

    @Override
    public ObservationTemplate findObservationTemplateByName(String templateName) {
        List<ObservationTemplate> list = queryWithClass("from ObservationTemplateImpl where displayName = :tname",
                "tname", templateName);
        assert list.size() <= 1;
        return (list.size() == 1) ? list.iterator().next() : null;
    }

    @Override
    public SubmissionCenter findSubmissionCenterByName(String submissionCenterName) {
        List<SubmissionCenter> list = queryWithClass("from SubmissionCenterImpl where displayName = :cname", "cname",
                submissionCenterName);
        assert list.size() <= 1;
        return (list.size() == 1) ? list.iterator().next() : null;
    }

    @Override
    public List<Submission> findSubmissionByIsStory(boolean isSubmissionStory, boolean sortByPriority) {
        List<ObservationTemplate> tmpList1 = queryWithClass(
                "from ObservationTemplateImpl where isSubmissionStory = :iss order by submissionStoryRank desc", "iss",
                isSubmissionStory);
        List<ObservationTemplate> tmpList2 = queryWithClass(
                "from ObservationTemplateImpl where isSubmissionStory = :iss", "iss", isSubmissionStory);
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
        List<Submission> submissions = queryWithClass("from SubmissionImpl where displayName = :sname", "sname",
                submissionName);
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

    /* purge the index if there is no observation having this subject */
    @SuppressWarnings("unchecked")
    @Override
    public void cleanIndex(int batchSize) {
        Session session = getSession();
        org.hibernate.query.Query<BigInteger> query = session.createNativeQuery(
                "SELECT id FROM subject WHERE id NOT IN (SELECT DISTINCT subject_id FROM observed_subject)");
        ScrollableResults scrollableResults = query.scroll(ScrollMode.FORWARD_ONLY);

        FullTextSession fullTextSession = Search.getFullTextSession(getSession());
        fullTextSession.setHibernateFlushMode(FlushMode.MANUAL);

        int cnt = 0;
        while (scrollableResults.next()) {
            Integer id = (Integer) scrollableResults.get(0);
            fullTextSession.purge(DashboardEntityImpl.class, id);

            if (++cnt % batchSize == 0) {
                fullTextSession.flushToIndexes();
            }
        }

        fullTextSession.flushToIndexes();
        fullTextSession.clear();

        session.close();
    }

    /*
     * The logic to find matching observations is both primitive and complicated at
     * the same time. It has been modified back and forth mostly based on specific
     * rules. The latest change is made following the request as described in issue
     * #388, which makes the logic more convoluted. The defination of 'term' here
     * WAS the complete exact entity name that is also a substring of the original
     * query string, not anymore. The queryString now is one re-constructed using
     * the tokens from the original query string.
     */
    private static String getMatchedTerm(String queryString, String entityName) {
        String name = entityName.toLowerCase();
        if (queryString.toLowerCase().contains(name))
            return name;
        else
            return null; // intentionally to return null if it is not a 'term' as defined above
    }

    @Override
    @Cacheable(value = "searchCache")
    public ArrayList<DashboardEntityWithCounts> search(String keyword) {
        HashSet<DashboardEntity> entitiesUnique = new HashSet<DashboardEntity>();

        FullTextSession fullTextSession = Search.getFullTextSession(getSession());
        Analyzer analyzer = new WhitespaceAnalyzer();
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(defaultSearchFields, analyzer);
        Query luceneQuery = null;
        try {
            luceneQuery = multiFieldQueryParser.parse(keyword);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*
         * The following two lines are part of the complicated logic to find 'matching'
         * observations. See the comment for method getMatchedTerm(String queryString,
         * String entityName), GitHub issue #388, and the part of code in this method
         * that is commented as 'add observations'
         */
        String[] tokens = keyword.toLowerCase().split("\\s+");
        String reconstructedQueryString = String.join(" ", tokens);

        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(luceneQuery, searchableClasses);
        fullTextQuery.setReadOnly(true);
        List<?> list = fullTextQuery.list();
        fullTextSession.close();

        Integer numberOfSearchResults = getMaxNumberOfSearchResults();
        if (numberOfSearchResults > 0 && list.size() > numberOfSearchResults) {
            // if lte 0, the maximum number is ignored
            log.warn("search result number " + list.size() + " is larger than the maximum expected, "
                    + numberOfSearchResults);
        }

        for (Object o : list) {
            assert o instanceof DashboardEntity;

            if (o instanceof ObservationTemplate) {
                List<Submission> submissionList = queryWithClass(
                        "select o from SubmissionImpl as o where o.observationTemplate = :ot", "ot",
                        (ObservationTemplate) o);
                for (Submission o2 : submissionList) {
                    if (!entitiesUnique.contains(o2))
                        entitiesUnique.add(o2);
                }
            } else {
                // Some objects came in as proxies, get the actual implementations for them when
                // necessary
                if (o instanceof HibernateProxy) {
                    o = ((HibernateProxy) o).getHibernateLazyInitializer().getImplementation();
                }

                if (!entitiesUnique.contains(o)) {
                    entitiesUnique.add((DashboardEntity) o);
                }
            }
        }

        ArrayList<DashboardEntityWithCounts> entitiesWithCounts = new ArrayList<DashboardEntityWithCounts>();
        Map<Observation, Set<String>> matchingObservations = new HashMap<Observation, Set<String>>();
        for (DashboardEntity entity : entitiesUnique) {
            DashboardEntityWithCounts entityWithCounts = new DashboardEntityWithCounts();
            entityWithCounts.setDashboardEntity(entity);
            if (entity instanceof Subject) {
                int observations = 0;
                int maxTier = 0;
                HashSet<SubmissionCenter> submissionCenters = new HashSet<SubmissionCenter>();
                HashSet<String> roles = new HashSet<String>();
                for (ObservedSubject observedSubject : findObservedSubjectBySubject((Subject) entity)) {
                    Observation observation = observedSubject.getObservation();
                    String term = getMatchedTerm(reconstructedQueryString, entity.getDisplayName());
                    if (term != null) {
                        Set<String> terms = matchingObservations.get(observation);
                        if (terms == null) {
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
            } else if (entity instanceof Submission) {
                entityWithCounts.setObservationCount(findObservationsBySubmission((Submission) entity).size());
                entityWithCounts.setMaxTier(((Submission) entity).getObservationTemplate().getTier());
                entityWithCounts.setCenterCount(1);
            }

            entitiesWithCounts.add(entityWithCounts);
        }

        /* search ECO terms */
        List<ECOTerm> ecoterms = findECOTerms(keyword);
        for (ECOTerm ecoterm : ecoterms) {
            int observationNumber = countObservation(ecoterm.getCode());
            if (observationNumber == 0)
                continue;
            DashboardEntityWithCounts entity = new DashboardEntityWithCounts();
            entity.setDashboardEntity(ecoterm);
            entity.setObservationCount(observationNumber);
            int[] x = templateSummary(ecoterm.getCode());
            entity.setMaxTier(x[0]);
            entity.setCenterCount(x[1]);

            entitiesWithCounts.add(entity);
        }

        // add observations
        Set<String> allTerms = new HashSet<String>();
        for (Observation ob : matchingObservations.keySet()) {
            for (String newTerm : matchingObservations.get(ob)) {
                Boolean add = true;
                Set<String> toBeRemoved = new HashSet<String>();
                for (String existingTerms : allTerms) {
                    if (existingTerms.contains(newTerm)) {
                        add = false;
                        break; // ignore this new term. no more comparison.
                    } else if (newTerm.contains(existingTerms)) {
                        toBeRemoved.add(existingTerms); // remove this existing term
                    }
                }
                if (add)
                    allTerms.add(newTerm);
                allTerms.removeAll(toBeRemoved);
            }
            // allTerms.addAll(matchingObservations.get(ob)); // too simple that we have to
            // give up this approach
        }
        if (allTerms.size() > 1) { // do not find 'intersection' if there is only one term
            for (Observation ob : matchingObservations.keySet()) {
                Set<String> terms = matchingObservations.get(ob);
                if (!terms.containsAll(allTerms))
                    continue;
                DashboardEntityWithCounts oneObservationResult = new DashboardEntityWithCounts();
                oneObservationResult.setDashboardEntity(ob);
                entitiesWithCounts.add(oneObservationResult);
            }
        }

        return entitiesWithCounts;
    }

    @SuppressWarnings("unchecked")
    private List<ECOTerm> findECOTerms(String queryString) {
        // search ECO codes first
        Pattern ECOCodePattern = Pattern.compile("(eco[:_])?(\\d{7})");
        Matcher matcher = ECOCodePattern.matcher(queryString);
        List<String> codes = new ArrayList<String>();
        while (matcher.find()) {
            codes.add("ECO:" + matcher.group(2));
        }

        org.hibernate.query.Query<?> query = null;
        List<ECOTerm> list = null;
        Session session = getSession();
        if (codes.size() > 0) {
            query = session.createQuery("FROM ECOTermImpl WHERE code in (:codes)");
            query.setParameterList("codes", codes);
            list = (List<ECOTerm>) query.list();
        } else {
            String[] words = queryString.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            Set<ECOTerm> set = new HashSet<ECOTerm>();
            for (String w : words) {
                String x = w.replaceAll("^\"|\"$", "");
                query = session.createQuery(
                        "FROM ECOTermImpl WHERE displayName LIKE '%" + x + "%' OR synonyms LIKE '%" + x + "%'");
                set.addAll((List<ECOTerm>) query.list());
            }
            list = new ArrayList<ECOTerm>(set);
        }
        log.debug("eco term number " + list.size());
        session.close();
        return list;
    }

    private int countObservation(String ecocode) {
        String sql = "SELECT count(observation.id) FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE ecocode LIKE '%" + ecocode + "%'";
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<BigInteger> query = session.createNativeQuery(sql);
        BigInteger count = query.getSingleResult();
        session.close();
        return count.intValue();

    }

    @Override
    public Map<Observation, BigInteger> getOneObservationPerSubmissionByEcoCode(String ecocode, int tier) {
        String sql = "SELECT MIN(observation.id), COUNT(DISTINCT observation.id) FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE ecocode LIKE '%" + ecocode + "%'";
        if (tier > 0) {
            sql += " AND tier=" + tier;
        }
        sql += " GROUP BY submission_id";

        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query = session.createNativeQuery(sql);
        List<Object[]> idList = query.list();
        session.close();

        Map<Observation, BigInteger> result = new HashMap<Observation, BigInteger>();
        for (Object[] pair : idList) {
            Observation observation = getEntityById(Observation.class, (Integer) pair[0]);
            BigInteger count = (BigInteger) pair[1];
            result.put(observation, count);
        }

        return result;
    }

    @Override
    public List<Observation> getObservationsForSubmissionAndEcoCode(Integer submissionId, String ecocode) {
        String sql = "SELECT observation.id FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE submission.id=" + submissionId + " AND ecocode LIKE '%" + ecocode + "%'";
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Integer> query = session.createNativeQuery(sql);
        List<Integer> idList = query.list();
        List<Observation> list = new ArrayList<Observation>();
        for (Integer id : idList) {
            Observation observation = getEntityById(Observation.class, id);
            list.add(observation);
        }
        session.close();
        return list;
    }

    private int[] templateSummary(String ecocode) {
        String sql = "SELECT MAX(tier), COUNT(DISTINCT submissionCenter_id) FROM observation_template WHERE ecocode='"
                + ecocode + "'";
        log.debug(sql);
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query = session.createNativeQuery(sql);
        Object[] result = query.getSingleResult();
        int[] x = new int[2];
        x[0] = result[0] == null ? 0 : ((Integer) result[0]).intValue();
        x[1] = result[1] == null ? 0 : ((BigInteger) result[1]).intValue();
        session.close();
        return x;
    }

    @Override
    public List<EcoBrowse> getEcoBrowse() {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> ecocodeQuery = session
                .createNativeQuery("SELECT ecocode, id FROM observation_template WHERE LENGTH(ecocode)>0");
        List<Object[]> ecocodeResult = ecocodeQuery.list();
        Map<String, EcoBrowse> map = new HashMap<String, EcoBrowse>();
        for (Object[] array : ecocodeResult) {
            String allcodes = (String) array[0];
            Integer templateId = (Integer) array[1];

            String countSql = "SELECT COUNT(DISTINCT submission.id), tier, COUNT(DISTINCT observation.id), COUNT(DISTINCT submissionCenter_id)"
                    + " FROM observation JOIN submission ON observation.submission_id=submission.id"
                    + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                    + " WHERE submission.observationTemplate_id=" + templateId + " GROUP BY tier";
            @SuppressWarnings("unchecked")
            org.hibernate.query.Query<Object[]> query = session.createNativeQuery(countSql);
            Object[] result = query.getSingleResult();
            BigInteger submissionCount = (BigInteger) result[0];
            Integer tier = (Integer) result[1];
            BigInteger tierCount = (BigInteger) result[2];
            BigInteger centerCount = (BigInteger) result[3];

            String[] ecocodes = allcodes.split("\\|");
            for (String code : ecocodes) {
                EcoBrowse b = map.get(code);
                if (b == null) { // this term not in the map yet
                    @SuppressWarnings("unchecked")
                    org.hibernate.query.Query<ECOTerm> ecotermQuery = session
                            .createQuery("FROM ECOTermImpl WHERE code='" + code + "'");
                    ECOTerm term = ecotermQuery.getSingleResult();
                    b = new EcoBrowse(term.getDisplayName(), term.getStableURL(), 0);
                    map.put(code, b);
                }

                b.setNumberOfSubmissions(b.getNumberOfSubmissions() + submissionCount.intValue());
                switch (tier) {
                    case 1:
                        b.setNumberOfTier1Observations(b.getNumberOfTier1Observations() + tierCount.intValue());
                        b.setNumberOfTier1SubmissionCenters(
                                b.getNumberOfTier1SubmissionCenters() + centerCount.intValue());
                        break;
                    case 2:
                        b.setNumberOfTier2Observations(b.getNumberOfTier2Observations() + tierCount.intValue());
                        b.setNumberOfTier2SubmissionCenters(
                                b.getNumberOfTier2SubmissionCenters() + centerCount.intValue());
                        break;
                    case 3:
                        b.setNumberOfTier3Observations(b.getNumberOfTier3Observations() + tierCount.intValue());
                        b.setNumberOfTier3SubmissionCenters(
                                b.getNumberOfTier3SubmissionCenters() + centerCount.intValue());
                        break;
                }
            }
        }
        session.close();
        log.debug("map size " + map.size());
        return new ArrayList<EcoBrowse>(map.values());
    }

    @Override
    public List<ObservedSubject> findObservedSubjectByRole(String role) {
        return queryWithClass("from ObservedSubjectImpl where observedSubjectRole.subjectRole.displayName = :role",
                "role", role);
    }

    @Override
    public List<SubjectWithSummaries> findSubjectWithSummariesByRole(String role, Integer minScore) {
        return query2ParamsWithClass("from SubjectWithSummaries where role = :role and score > :score", "role", role,
                "score", minScore);
    }

    @Cacheable(value = "uniprotCache")
    @Override
    public List<Protein> findProteinByGene(Gene gene) {
        Set<Protein> proteins = new HashSet<Protein>();
        List<Transcript> transcriptList = queryWithClass("from TranscriptImpl where gene = :gene", "gene", gene);
        for (Transcript t : transcriptList) {
            List<Protein> list = queryWithClass("from ProteinImpl as p where :transcript member of p.transcripts",
                    "transcript", t);
            for (Protein p : list) {
                proteins.add(p);
            }
        }

        return (new ArrayList<Protein>(proteins));
    }

    private <E> List<E> queryWithClass(String queryString, String parameterName, Object valueObject) {
        assert queryString.contains(":" + parameterName);
        Session session = getSession();
        org.hibernate.query.Query<?> query = session.createQuery(queryString);
        query.setParameter(parameterName, valueObject);
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) query.list();
        session.close();

        return list;
    }

    private <E> List<E> query2ParamsWithClass(String queryString, String parameterName1, Object valueObject1,
            String parameterName2, Object valueObject2) {
        assert queryString.contains(":" + parameterName1);
        assert queryString.contains(":" + parameterName2);
        Session session = getSession();
        org.hibernate.query.Query<?> query = session.createQuery(queryString);
        query.setParameter(parameterName1, valueObject1).setParameter(parameterName2, valueObject2);
        @SuppressWarnings("unchecked")
        List<E> list = (List<E>) query.list();
        session.close();

        return list;
    }

    @Override
    public Map<Observation, BigInteger> getOneObservationPerSubmission(Integer subjectId) {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query = session.createNativeQuery(
                "SELECT MIN(observation_id), COUNT(DISTINCT observation_id) FROM observed_subject JOIN observation on observed_subject.observation_id=observation.id WHERE subject_id="
                        + subjectId + " GROUP BY submission_id");
        List<Object[]> idList = query.list();
        session.close();

        Map<Observation, BigInteger> result = new HashMap<Observation, BigInteger>();
        for (Object[] pair : idList) {
            Observation observation = getEntityById(Observation.class, (Integer) pair[0]);
            BigInteger count = (BigInteger) pair[1];
            result.put(observation, count);
        }

        return result;
    }

    @Override
    public String expandSummary(Integer observationId, String summaryTemplate) {
        String summary = summaryTemplate;

        Session session1 = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query1 = session1
                .createNativeQuery("SELECT stableURL, displayName, columnName FROM subject"
                        + " JOIN observed_subject ON subject.id=observed_subject.subject_id"
                        + " JOIN observed_subject_role ON observed_subject.observedSubjectRole_id=observed_subject_role.id"
                        + " JOIN dashboard_entity ON subject.id=dashboard_entity.id" + " WHERE observation_id="
                        + observationId);
        List<Object[]> subjects = query1.list();
        session1.close();
        for (Object[] x : subjects) {
            String stableURL = (String) x[0];
            String subjectName = (String) x[1];
            String columnName = (String) x[2];
            String replacement = "<a class='summary-replacement' href='#" + stableURL + "'>" + subjectName + "</a>";
            summary = summary.replace("<" + columnName + ">", replacement);
        }

        Pattern pattern = Pattern.compile("\\<([0-9a-zA-Z_]+)\\>");
        Matcher matcher = pattern.matcher(summary);
        while (matcher.find()) {
            String match = matcher.group(0);
            String columnName = matcher.group(1);

            Session session = getSession();
            @SuppressWarnings("unchecked")
            org.hibernate.query.Query<String> query = session
                    .createNativeQuery("SELECT dashboard_entity.displayName FROM evidence"
                            + " JOIN observed_evidence on evidence.id=observed_evidence.evidence_id"
                            + " JOIN observed_evidence_role ON observed_evidence.observedEvidenceRole_id=observed_evidence_role.id"
                            + " JOIN dashboard_entity ON evidence.id=dashboard_entity.id"
                            + " WHERE observed_evidence.observation_id=" + observationId
                            + " AND observed_evidence_role.columnName='" + columnName + "'");
            String evidenceName = query.getSingleResult();
            session.close();
            summary = summary.replace(match, evidenceName);
        }
        return summary;
    }

    @Override
    public List<SubjectItem> getObservedSubjectInfo(Integer observationId) {
        Session session1 = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query1 = session1.createNativeQuery(
                "SELECT d2.displayName AS role, observed_subject_role.displayText AS description, d1.displayName AS name, subject.id, columnName, stableURL"
                        + " FROM observed_subject join subject on observed_subject.subject_id=subject.id"
                        + " JOIN dashboard_entity d1 ON subject.id=d1.id"
                        + " JOIN observed_subject_role ON observed_subject.observedSubjectRole_id = observed_subject_role.id"
                        + " JOIN subject_role ON observed_subject_role.subjectRole_id=subject_role.id"
                        + " JOIN dashboard_entity AS d2 ON subject_role.id=d2.id" + " where observation_id="
                        + observationId);
        List<Object[]> subjects = query1.list();

        List<SubjectItem> list = new ArrayList<SubjectItem>();
        for (Object[] obj : subjects) {
            String role = (String) obj[0];
            String description = (String) obj[1];
            String name = (String) obj[2];
            Integer subjectId = (Integer) obj[3];
            String columnName = (String) obj[4];
            String stableURL = (String) obj[5];

            @SuppressWarnings("unchecked")
            org.hibernate.query.Query<String> query2 = session1
                    .createNativeQuery("SELECT displayName FROM subject_synonym_map "
                            + " JOIN synonym ON subject_synonym_map.synonyms_id=synonym.id "
                            + " JOIN dashboard_entity ON synonym.id = dashboard_entity.id" + " WHERE SubjectImpl_id="
                            + subjectId);
            List<String> synonyms = query2.list();

            @SuppressWarnings("unchecked")
            org.hibernate.query.Query<Object[]> query3 = session1
                    .createNativeQuery("SELECT databaseId, databaseName FROM subject_xref_map"
                            + " JOIN xref ON subject_xref_map.xrefs_id=xref.id " + " WHERE SubjectImpl_id="
                            + subjectId);
            List<Object[]> xrefs = query3.list();
            List<XRefItem> xrefItems = new ArrayList<XRefItem>();
            for (Object[] x : xrefs) {
                xrefItems.add(new XRefItem((String) x[1], (String) x[0]));
            }

            SubjectItem subjectItem = new SubjectItem(stableURL.substring(0, stableURL.indexOf("/")), role, description,
                    name, synonyms.toArray(new String[0]), xrefItems.toArray(new XRefItem[0]), columnName);
            list.add(subjectItem);
        }
        session1.close();
        return list;
    }

    @Override
    public List<EvidenceItem> getObservedEvidenceInfo(Integer observationId) {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query = session.createNativeQuery(
                "SELECT d2.displayName AS type, observed_evidence_role.displayText AS description, evidence.id, columnName"
                        + " FROM observed_evidence join evidence on observed_evidence.evidence_id=evidence.id"
                        + " JOIN observed_evidence_role ON observed_evidence.observedEvidenceRole_id=observed_evidence_role.id"
                        + " JOIN evidence_role ON observed_evidence_role.evidenceRole_id=evidence_role.id"
                        + " JOIN dashboard_entity AS d2 ON evidence_role.id=d2.id WHERE observation_id="
                        + observationId);
        List<EvidenceItem> list = new ArrayList<EvidenceItem>();
        List<Object[]> evidences = query.list();

        for (Object[] obj : evidences) {
            String type = (String) obj[0];
            String description = (String) obj[1];
            Integer evidenceId = (Integer) obj[2];
            Evidence evidence = getEntityById(Evidence.class, evidenceId);
            String evidenceName = evidence.getDisplayName();
            String columnName = (String) obj[3];
            EvidenceItem evidenceItem = new EvidenceItem(evidence, type, description, evidenceName, columnName);
            list.add(evidenceItem);
        }
        session.close();
        return list;
    }

    private Summary summarizePerSubject(Class<? extends Subject> subjectClass) {
        String tableName = "";
        Class<?> implClass = subjectClass.isInterface() ? dashboardFactory.getImplClass(subjectClass) : subjectClass;
        java.lang.annotation.Annotation[] annotation = implClass.getAnnotationsByType(javax.persistence.Table.class);
        if (annotation.length == 1) {
            javax.persistence.Table table = (javax.persistence.Table) annotation[0];
            tableName = table.name();
        } else {
            return new Summary("exception", 0, 0, 0, 0);
        }

        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<BigInteger> query = session
                .createNativeQuery("SELECT count(DISTINCT submission_id) FROM " + tableName
                        + " JOIN observed_subject ON observed_subject.subject_id=" + tableName + ".id"
                        + " JOIN observation ON observation.id=observed_subject.observation_id");
        BigInteger submissions = query.getSingleResult();

        String tierQuery = "SELECT tier, COUNT(DISTINCT observation_id) FROM " + tableName
                + " JOIN observed_subject ON observed_subject.subject_id=" + tableName + ".id"
                + " JOIN observation ON observation.id=observed_subject.observation_id"
                + " JOIN submission ON submission.id=observation.submission_id"
                + " JOIN observation_template ON observation_template.id=submission.observationTemplate_id"
                + " GROUP BY tier";
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query2 = session.createNativeQuery(tierQuery);
        int[] tierCount = new int[3];
        List<Object[]> result = query2.list();
        for (Object[] obj : result) {
            Integer tier = (Integer) obj[0];
            BigInteger count = (BigInteger) obj[1];
            tierCount[tier - 1] = count.intValue();
        }

        session.close();
        return new Summary(subjectClass.getSimpleName(), submissions.intValue(), tierCount[0], tierCount[1],
                tierCount[2]);
    }

    private Summary summarizeStories() {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<BigInteger> query = session.createNativeQuery("SELECT COUNT(*) FROM submission"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE isSubmissionStory=True");
        BigInteger submissions = query.getSingleResult();

        String tierQuery = "SELECT tier, COUNT(*) FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE isSubmissionStory=TRUE" + " GROUP BY tier";
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query2 = session.createNativeQuery(tierQuery);
        int[] tierCount = new int[3];
        List<Object[]> result = query2.list();
        for (Object[] obj : result) {
            Integer tier = (Integer) obj[0];
            BigInteger count = (BigInteger) obj[1];
            tierCount[tier - 1] = count.intValue();
        }
        session.close();
        return new Summary("Stories", submissions.intValue(), tierCount[0], tierCount[1], tierCount[2]);
    }

    private Summary summarizeECO() {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<BigInteger> query = session.createNativeQuery("SELECT COUNT(*) FROM submission"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE ecoCode <> ''");
        BigInteger submissions = query.getSingleResult();

        String tierQuery = "SELECT tier, COUNT(*) FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " WHERE ecoCode <> ''" + " GROUP BY tier";
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query2 = session.createNativeQuery(tierQuery);
        int[] tierCount = new int[3];
        List<Object[]> result = query2.list();
        for (Object[] obj : result) {
            Integer tier = (Integer) obj[0];
            BigInteger count = (BigInteger) obj[1];
            tierCount[tier - 1] = count.intValue();
        }
        session.close();
        return new Summary("Evidence Ontology Codes", submissions.intValue(), tierCount[0], tierCount[1], tierCount[2]);
    }

    private Summary summarizeTotal() {
        Session session = getSession();
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<BigInteger> query = session.createNativeQuery("SELECT COUNT(*) FROM submission"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id");
        BigInteger submissions = query.getSingleResult();

        String tierQuery = "SELECT tier, COUNT(*) FROM observation"
                + " JOIN submission ON observation.submission_id=submission.id"
                + " JOIN observation_template ON submission.observationTemplate_id=observation_template.id"
                + " GROUP BY tier";
        @SuppressWarnings("unchecked")
        org.hibernate.query.Query<Object[]> query2 = session.createNativeQuery(tierQuery);
        int[] tierCount = new int[3];
        List<Object[]> result = query2.list();
        for (Object[] obj : result) {
            Integer tier = (Integer) obj[0];
            BigInteger count = (BigInteger) obj[1];
            tierCount[tier - 1] = count.intValue();
        }
        session.close();
        return new Summary("Total", submissions.intValue(), tierCount[0], tierCount[1], tierCount[2]);
    }

    @Override
    public void summarize() {
        findEntities(Summary.class).forEach(s -> delete(s));

        Class<?>[] summaryClasses = new Class<?>[] { AnimalModel.class, CellSample.class, Compound.class, Gene.class,
                ShRna.class, TissueSample.class };
        for (Class<?> c : summaryClasses) {
            @SuppressWarnings("unchecked")
            Summary s = summarizePerSubject((Class<? extends Subject>) c);
            save(s);
        }

        Summary s = summarizeStories();
        save(s);
        Summary eco = summarizeECO();
        save(eco);
        Summary total = summarizeTotal();
        save(total);
    }

    @Override
    public List<Summary> getOverallSummary() {
        return findEntities(Summary.class).stream().filter(s -> s.getLabel() != null).collect(Collectors.toList());
    }
}
