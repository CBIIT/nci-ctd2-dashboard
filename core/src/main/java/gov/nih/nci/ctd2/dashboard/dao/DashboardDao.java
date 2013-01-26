package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;

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
}
