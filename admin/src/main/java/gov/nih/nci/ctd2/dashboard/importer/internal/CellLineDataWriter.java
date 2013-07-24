package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

public class CellLineDataWriter implements Tasklet {

    @Autowired
	private DashboardDao dashboardDao;

    @Autowired
	@Qualifier("cellSampleMap")
	private HashMap<String,CellSample> cellSampleMap;

    @Autowired
    @Qualifier("indexBatchSize")
    private Integer batchSize;

	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
        dashboardDao.batchSave(cellSampleMap.values(), batchSize);
        return RepeatStatus.FINISHED;
    }
}
