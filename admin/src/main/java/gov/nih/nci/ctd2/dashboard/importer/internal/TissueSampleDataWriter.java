package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.*;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.*;

@Component("tissueSampleDataWriter")
public class TissueSampleDataWriter implements Tasklet {

    @Autowired
	private DashboardDao dashboardDao;
 
	private static final Log log = LogFactory.getLog(TissueSampleDataWriter.class);

    @Autowired
	@Qualifier("tissueSampleMap")
	private HashMap<String,TissueSample> tissueSampleMap;       

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;

    public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        ArrayList<DashboardEntity> entities = new ArrayList<DashboardEntity>();
        for (TissueSample tissueSample : tissueSampleMap.values()) {
            entities.add(tissueSample);
        }
        dashboardDao.batchSave(entities, batchSize);
        return RepeatStatus.FINISHED;
    }
}
