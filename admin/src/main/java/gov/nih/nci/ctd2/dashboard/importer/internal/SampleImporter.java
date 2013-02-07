package gov.nih.nci.ctd2.dashboard.importer.internal;

import gov.nih.nci.ctd2.dashboard.dao.DashboardDao;
import gov.nih.nci.ctd2.dashboard.importer.AbstractImporter;
import gov.nih.nci.ctd2.dashboard.model.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SampleImporter extends AbstractImporter {
    private static Log log = LogFactory.getLog(SampleImporter.class);

    @Override
    public void run() {
        DashboardFactory dashboardFactory = getDashboardFactory();
        DashboardDao dashboardDao = getDashboardDao();

        Synonym synonym = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S1");

        Synonym synonym2 = dashboardFactory.create(Synonym.class);
        synonym.setDisplayName("S2");

        Synonym synonym3 = dashboardFactory.create(Synonym.class);
        synonym3.setDisplayName("S3");

        // Save with id
        Gene gene = dashboardFactory.create(Gene.class);
        gene.setDisplayName("G1");
        gene.setEntrezGeneId("E1");
        gene.getSynonyms().add(synonym);
        gene.getSynonyms().add(synonym2);
        dashboardDao.save(gene);

        // save without id
        Gene gene2 = dashboardFactory.create(Gene.class);
        gene2.setEntrezGeneId("E2");
        gene.setDisplayName("G2");
        dashboardDao.save(gene2);

        Transcript transcript = dashboardFactory.create(Transcript.class);
        transcript.setGene(gene2);
        transcript.setRefseqId("NM_21431");
        gene.setDisplayName("T1");
        dashboardDao.save(transcript);

        Protein protein = dashboardFactory.create(Protein.class);
        protein.getTranscripts().add(transcript);
        protein.setUniprotId("1000");
        protein.setDisplayName("P1");
        dashboardDao.save(protein);

        AnimalModel animalModel = dashboardFactory.create(AnimalModel.class);
        animalModel.getSynonyms().add(synonym3);
        animalModel.setDisplayName("MM1");
        dashboardDao.save(animalModel);

        UrlEvidence urlEvidence = dashboardFactory.create(UrlEvidence.class);
        urlEvidence.setUrl("http://ctd2.nci.nih.gov/");

        LabelEvidence labelEvidence = dashboardFactory.create(LabelEvidence.class);
        labelEvidence.setDisplayName("L1");

        log.info("Imported " + dashboardDao.findEntities(DashboardEntity.class).size() + " entities.");
    }
}
