package gov.nih.nci.ctd2.dashboard.util;

import org.springframework.cache.annotation.Cacheable;

import java.util.Date;

public class CacheTestUtil {
    @Cacheable(value = "timeTestCache")
    public long cacheableMethod() {
        long time = (new Date()).getTime();
        System.out.println("time = " + time);
        return time;
    }
}
