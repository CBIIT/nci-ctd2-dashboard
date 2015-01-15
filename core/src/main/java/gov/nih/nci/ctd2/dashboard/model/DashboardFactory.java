package gov.nih.nci.ctd2.dashboard.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class DashboardFactory {
    private static Log log = LogFactory.getLog(DashboardFactory.class);
    public abstract <T extends DashboardEntity> T create(Class<T> aClass);

    public <T extends DashboardEntity> Class<T> getImplClass(Class<T> aClass) {
        Class<T> implClass = null;

        if(aClass.isInterface()) {
            String name = getImplClassName(aClass.getSimpleName());
            try {
                implClass = (Class<T>) Class.forName(name);
            } catch (ClassNotFoundException e) {
                log.error("Could not get class with name: " + name);
            }
        } else {
            implClass = aClass;
        }

        return implClass;

    }

    private String getImplClassName(String simpleClassName) {
        return "gov.nih.nci.ctd2.dashboard.impl." + simpleClassName + "Impl";
    }

}
