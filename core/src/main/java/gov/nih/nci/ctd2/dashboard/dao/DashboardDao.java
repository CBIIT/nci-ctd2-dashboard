package gov.nih.nci.ctd2.dashboard.dao;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;

public interface DashboardDao {
    void save(DashboardEntity entity);
    void update(DashboardEntity entity);
    void delete(DashboardEntity entity);
    DashboardEntity getEntityById(Integer id);
    <T extends DashboardEntity> T getEntityById(Class<T> filterBy, Integer id);
}
