package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.Compound;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import java.util.HashMap;

public class CompoundDataWriter implements Tasklet {

    @Autowired
	private DashboardDao dashboardDao;

    @Autowired
	@Qualifier("compoundMap")
	private HashMap<String,Compound> compoundMap;

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;
 
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        dashboardDao.batchSave(compoundMap.values(), batchSize);
        return RepeatStatus.FINISHED;
    }
}
