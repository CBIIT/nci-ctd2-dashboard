package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.DashboardEntity;
import gov.nih.nci.ctd2.dashboard.model.DashboardFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DashboardFactoryImpl extends DashboardFactory {
    private static Log log = LogFactory.getLog(DashboardFactoryImpl.class);

    public <T extends DashboardEntity> T create(Class<T> aClass) {
        // Idea from
        T entity = null;

        try {
            Class<T> t = getImplClass(aClass);
            if(t != null) {
                Constructor<T> c = t.getDeclaredConstructor();
                c.setAccessible(true);
                entity = c.newInstance();
            } else {
                log.error("Could not create a class " + aClass);
            }
        } catch (Exception e) {
            log.error("Could not instantiate " + aClass);
            log.error(e.getStackTrace());
        }

        // Set displayName
        try {
            Method m = DashboardEntity.class.getDeclaredMethod("setDisplayName", String.class);
            m.setAccessible(true);
            m.invoke(entity, "");
//            m = DashboardEntity.class.getDeclaredMethod("setId", Integer.class);
//            m.setAccessible(true);
//            m.invoke(entity, (Integer) null);
        } catch (Exception e) {
            log.error("Could not set displayName/id for " + entity.getClass());
            log.error(e.getStackTrace());
            return null;
        }

        return entity;
    }


}
