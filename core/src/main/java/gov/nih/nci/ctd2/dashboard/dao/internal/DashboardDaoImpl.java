package gov.nih.nci.ctd2.dashboard.dao.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

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
        // TODO: Filter by class?
        List list = getHibernateTemplate().find("from dashboard_entity where id=?",id);
        return list.isEmpty() ? null : (DashboardEntity) list.iterator().next();
    }
}
