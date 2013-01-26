package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.impl.DashboardEntityImpl;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class DashboardDaoImpl extends HibernateDaoSupport implements DashboardDao {
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
    public <T extends DashboardEntity> T getEntityById(Class<T> filterBy, Integer id) {
        try {
            Class<T> aClass = filterBy.isInterface()
                    ? (Class<T>) Class.forName(DashboardFactory.getImplClassName(filterBy.getSimpleName()))
                    : filterBy;
            return getHibernateTemplate().get(aClass, id);
        } catch (ClassNotFoundException e) {
            logger.error("Could not find class: " + filterBy.getSimpleName());
            e.printStackTrace();
        }

        return null;
    }
}
