package gov.nih.nci.ctd2.dashboard.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class DashboardFactory {
    private static Log log = LogFactory.getLog(DashboardFactory.class);

    public <T extends DashboardEntity> T create(Class<T> aClass) {
        return create(aClass, null);
    }

    public <T extends DashboardEntity> T create(Class<T> aClass, Integer id) {
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

        // Set id
        try {
            Method m = DashboardEntity.class.getDeclaredMethod("setId", Integer.class);
            m.setAccessible(true);
            m.invoke(entity, id);
        } catch (Exception e) {
            log.error("Could not set ID for " + entity.getClass());
            log.error(e.getStackTrace());
            return null;
        }

        return entity;
    }

    private <T extends DashboardEntity> Class<T> getImplClass(Class<T> aClass) {
        Class<T> implClass = null;

        if(aClass.isInterface()) {
            String name = "gov.nih.nci.ctd2.dashboard.impl." + aClass.getSimpleName() + "Impl";
            try {
                implClass = (Class<T>) Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("Could not get class with name: " + name);
            }
        }

        return implClass;

    }
}
