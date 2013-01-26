package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.List;

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
}
