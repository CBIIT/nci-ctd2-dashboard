package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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

}
