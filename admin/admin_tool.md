* [Introduction][1]
* [Environment Variables][2]
* [Admin Properties][3]
* [Taxonomy List][4]
* [Subject Data][5]
* [Submission Data][6]
* [Submission Metadata][7]
* [Admin Tool Usage][8]

# Introduction

This page describes how to properly build and use the CTD^2 Dashboard admin tool.

# Environment Variables

The following environment variables are referenced in this document and should be defined for the proper functioning of the admin tool:

* CTD2_HOME: points to the directory in which the entire dashboard source code repository has been downloaded.
* CTD2_DATA_HOME: points to the directory which contains dashboard data to be imported.

To make environment variables available to your shell, run the EXPORT command:

```
#!shell
    EXPORT CTD2_HOME=/path/to/ctd2-dashboard
    EXPORT CTD2_DATA_HOME=/path/to/ctd2-dashboard-data
```

# Admin Properties

In order for the admin tool to properly load data into the CTD^2 dashboard database, it needs to know the location of the data.  This is the function of $CTD2_HOME/admin/src/main/resources/META-INF-spring/admin.properties file.  This file need to exist and have proper values before compiling the admin tool.  In the source distribution you will find $CTD2_HOME/admin/src/main/resources/META-INF-spring/admin.properties.example to use as a basis for your admin.properties file.  More information about these properties can be found in the [Subject Data][5] and [Submission Data][6] sections of this document.

# Taxonomy List

All desired organisms should be listed in $CTD2_HOME/admin/src/main/resources/simple-taxonomy-list.txt.  This file follows a simple name, taxonomy_id format:

```
#!shell
name	taxonomy_id
Homo sapiens	 9606
Mus musculus	 10090
```

# Subject Data

Subjects in the CTD^2 Dashboard are those entities that play various roles in experiments conducted by CTD^2 network centers which result in [submission data][6] that you will find in the CTD^2 Dashboard.  Subject data includes gene, protein, and compound data.  Subject data needs to be imported into the Dashboard database before CTD^2 network center data can be imported.  With the exception of gene and protein data, all the required subject data can be found in the [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip) distribution.  This file should be downloaded an unzipped into $CTD2_DASHBOARD_DATA.

+The following subject data and sources are support for import by the admin tool:

* ***Gene***: Gene data as provided by [Entrez](http://www.ncbi.nlm.nih.gov/gene).  This data can be downloaded via ftp at the following URL: [ftp://ftp.ncbi.nih.gov//gene/DATA/GENE_INFO/](ftp://ftp.ncbi.nih.gov//gene/DATA/GENE_INFO/).  The gene_data file should be downloaded into $CTD2_DASHBOARD_DATA/subject_data/gene.  If this file is placed in any other directory, the following entry in admin.properties needs to be update:

```
#!shell
gene.data.location=file:${CTD2_DATA_HOME}/subject_data/gene/*.gene_info
```

* ***Animal Model***: Animal Model data as provided by the [Clemons Group](http://www.broadinstitute.org/scientific-community/science/programs/csoft/chemical-biology/group-clemons/chemical-biology-clemons-) at the Broad Institute.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), this data can be found in $CTD2_DASHBOARD_DATA/subject_data/animal_model.  The following entries in admin.properties specify the location of animal model data:

```
#!shell
animal.model.location=file:${CTD2_DATA_HOME}/subject_data/animal_model/animal_model.txt
```
* ***Cell Line***: Cell Line data as provided by the [Clemons Group](http://www.broadinstitute.org/scientific-community/science/programs/csoft/chemical-biology/group-clemons/chemical-biology-clemons-) at the Broad Institute.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), this data can be found in $CTD2_DASHBOARD_DATA/subject_data/cell_sample.  The following entries in admin.properties specify the location of cell line data:

```
#!shell
cell.line.name.type.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_name_type.txt
cell.line.annotation.type.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_anno_type.txt
cell.line.annotation.name.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_anno_name.txt
cell.line.annotation.source.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_anno_source.txt
cell.line.annotation.sample.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_anno.txt
cell.line.id.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_sample.txt
cell.line.name.location=file:${CTD2_DATA_HOME}/subject_data/cell_sample/cell_sample_name.txt
```

* ***Compounds***: Compound data as provided by the [Clemons Group](http://www.broadinstitute.org/scientific-community/science/programs/csoft/chemical-biology/group-clemons/chemical-biology-clemons-) at the Broad Institute.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), this data can be found in $CTD2_DASHBOARD_DATA/subject_data/compound.  The following entries in admin.properties specify the location of compound data:

```
#!shell
compounds.location=file:${CTD2_DATA_HOME}/subject_data/compound/Compounds.txt
compound.synonyms.location=file:${CTD2_DATA_HOME}/subject_data/compound/CompoundSynonyms.txt
```

* ***Proteins***: Protein data as provided by [UniProt](http://www.uniprot.org/).  This data can be downloaded via ftp at the following URL: [ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/](ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/taxonomic_divisions/).  The UniProt data files should be downloaded into $CTD2_DASHBOARD_DATA/subject_data/protein.  If multiple UniProt files are downloaded, the following entry in admin.properties needs to be set:

```
#!shell
protein.data.location=file:${CTD2_DATA_HOME}/protein/uniprot_sprot_*.dat
```

otherwise the specific file can be reference:

```
#!shell
protein.data.location=file:${CTD2_DATA_HOME}/subject_data/protein/uniprot_sprot_human.dat
```

* ***shRNA***:  shRNA data as provide by the [RNAi Consortium at the Broad Institute](http://www.broadinstitute.org/rnai/trc/lib).  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), a subset of this data can be found in $CTD2_DASHBOARD_DATA/subject_data/shrna.  The following entries in admin.properties specify the location of shRNA data:

```
#!shell
trc.shrna.data.location=file:${CTD2_DATA_HOME}/subject_data/shrna/trc_public.05Apr11.txt
trc.shrna.filter.location=file:${CTD2_DATA_HOME}/subject_data/shrna/trc-shrnas-filter.txt
```

* ***siRNA***:  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), a subset of this data can be found in $CTD2_DASHBOARD_DATA/subject_data/sirna.  The following entries in admin.properties specify the location of siRNA data:

```
#!shell
sirna.reagents.location=file:${CTD2_DATA_HOME}/subject_data/sirna/siRNA_reagents.txt
```

* ***Tissue Sample***: Tissue Sample data as provided by the [Clemons Group](http://www.broadinstitute.org/scientific-community/science/programs/csoft/chemical-biology/group-clemons/chemical-biology-clemons-) at the Broad Institute.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), this data can be found in $CTD2_DASHBOARD_DATA/subject_data/tissue_sample.  The following entries in admin.properties specify the location of tissue-sample data:

```
#!shell
tissue.sample.data.location=file:${CTD2_DATA_HOME}/subject_data/tissue_sample/tissue_sample_name.txt
```

# Submission Data

As previously noted, the data that results from the experiments performed by CTD^2 network centers which makes its way into the CTD^2 dashboard database is called submission data.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), submission data can be found in $CTD2_DASHBOARD_DATA/subject_data.  For each center-submission pair is a property within admin.properties that specifies the location of the data:
 
```
#!shell
broad.cmp.sens.lineage.enrich.data.location=file:${CTD2_DATA_HOME}/submissions/20130328-broad_cpd_sens_lineage_enrich-MST-312/20130328-broad_cpd_sens_lineage_enrich-MST-312.txt
broad.cmp.sens.mutation.enrich.data.location=file:${CTD2_DATA_HOME}/submissions/20130328-broad_cpd_sens_mutation_enrich-navitoclax/20130328-broad_cpd_sens_mutation_enrich-navitoclax.txt
broad.tier3.navitoclax.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130402-broad_tier3_navitoclax_story/20130402-broad_tier3_navitoclax_story.txt
columbia.marina.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130402-columbia_marina_analysis-T-ALL/20130402-columbia_marina_analysis-T-ALL.txt
columbia.mra.fet.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130403-columbia_mra_fet_analysis-glioma/20130403-columbia_mra_fet_analysis-glioma.txt
columbia.joint.mr.shrna.diff.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130401-columbia_joint_mr_shrna_diff-T-ALL/20130401-columbia_joint_mr_shrna_diff-T-ALL.txt
columbia.tier4.glioma.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130401-columbia_tier4_glioma_story/20130401-columbia_tier4_glioma_story.txt
cshl.tier4.fgf19.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130403-cshl_tier4_fgf19_story/20130403-cshl_tier4_fgf19_story.txt
dfci.tier4.beta.catenin.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130401-dfci_tier4_beta-catenin_story/20130401-dfci_tier4_beta-catenin_story.txt
dfci.reporter.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130426-dfci_reporter_analysis-bcat/20130426-dfci_reporter_analysis-bcat.txt
dfci.ataris.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130429-dfci_ataris_analysis/20130429-dfci_ataris_analysis.txt
dfci.ovarian.analysis.data.location=file:${CTD2_DATA_HOME}/submissions/20130426-dfci_ovarian_analysis/20130426-dfci_ovarian_analysis.txt
dfci.pax8.tier3.data.location=file:${CTD2_DATA_HOME}/submissions/20130429-dfci_pax8_tier3/20130429-dfci_pax8_tier3.txt
emory.ppi-raf1.data.location=file:${CTD2_DATA_HOME}/submissions/20131220-emory_PPI_analysis-RAF1/20131220-emory_PPI_analysis-RAF1.txt
fhcrc.tier1.cst.profiling.data.location=file:${CTD2_DATA_HOME}/submissions/20131117-fhcrc-m_tier1_cst_profiling-SOC/20131117-fhcrc-m_tier1_cst_profiling-SOC.txt
utsw.tier2.discoipyrroles.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130921-utsw_discoipyrrole_tier2_story/20130921-utsw_discoipyrrole_tier2_story.txt
utsw.tier4.discoipyrroles.story.data.location=file:${CTD2_DATA_HOME}/submissions/20130503-utsw_tier4_discoipyrroles_story/20130503-utsw_tier4_discoipyrroles_story.txt
ucsf.differential-expression.data.location=file:${CTD2_DATA_HOME}/submissions/20140124-ucsf_differential_expression/20140124-ucsf_differential_expression.txt
```

# Submission Metadata

The dashboard imports metadata for each submission.  After downloading and unzipping [ctd2-dashboard-seed.zip](https://bitbucket.org/cbio_mskcc/ctd2-dashboard/downloads/ctd2-dashboard-seed.zip), this metadata can be found in the following two files:

1. ***$CTD2_DATA_HOME/dashboard-CV-per-template.txt***: Every dashboard submission is derived from an underlying template.  dashboard-CV-per-template.txt is the file that contains metadata for all submission templates known to the dashboard.  For each template, it contains the following information:

2. ***$CTD2_DATA_HOME/dashboard-CV-per-column.txt***: This file describes the experimental data and the relationships between the experimental data that each submission data template was designed to capture.

## Spring Batch

The CTD^2 dashboard pipeline has been developed using the [Spring Batch](http://projects.spring.io/spring-batch/) framework.  For each new submission the following Spring Batch configuration files need to be modified:

1. ***$CTD2_HOME/admin/src/main/resources/META-INF/spring/observationDataApplicationContext.xml***:  This file configures a Spring Batch reader to read the new submission data.  For example, here is a snippet from this file which defines the Emory University PPI analysis submission reader:

```
#!shell
  <bean name="emoryPPIRAF1Reader" class="org.springframework.batch.item.file.MultiResourceItemReader">
	<property name="resources" value="${emory.ppi-raf1.data.location}" />
	<property name="delegate">
	  <bean class="org.springframework.batch.item.file.FlatFileItemReader">
		<property name="lineMapper" ref="emoryPPIRAF1LineMapper" />
		<property name="linesToSkip" value="7" />
	  </bean>
	</property>
  </bean>
``` 

The important thing to note is the resource location, emory.ppi-raf1.data.location.  This should correspond to an entry in admin.properties as described above.  In addition, we have a reference to emoryPPIRAF1LineMapper.  This is a reference to the Spring bean which is responsible for the parsing of each line in the Emory University submission.  This mapper is defined in the following section.

2. ***$CTD2_HOME/admin/src/main/resources/META-INF/spring/observationDataSharedApplicationContext.xml***: This file configures the overall Spring Batch job in addition to the individual submission line mappers and tokenizers.  Another important reason for this file is to configure the mappings between the Spring Batch submission processors and the DashboardDao - data access class.  Continuing with our Emory University, within the "observationDataImportJob" recipe, you will find the following snippet:

```
#!shell
    <batch:step id="emoryPPIRAF1Step" parent="observationDataStep" next="mskccForetinibStep">
	  <batch:tasklet>
		<batch:chunk reader="emoryPPIRAF1Reader" processor="observationDataProcessor" writer="observationDataWriter"/>
	  </batch:tasklet>
    </batch:step>
```
Here we are defining the Emory submission processing step within the overall Dashboard submission processing Spring Batch job (observationDataImportJob).  Following the job description are the definitions for each submission line mapper and tokenizer.  The following snippet defines the Emory University submission line mapper and tokenizer:

```
#!shell
  <bean name="emoryPPIRAF1LineMapper" class="org.springframework.batch.item.file.mapping.DefaultLineMapper">
	<property name="fieldSetMapper" ref="observationDataMapper" />
	<property name="lineTokenizer" ref="emoryPPIRAF1LineTokenizer" />
  </bean>

  <bean name="emoryPPIRAF1LineTokenizer" class="org.springframework.batch.item.file.transform.DelimitedLineTokenizer" >
	<property name="delimiter" value="\u0009"/>
	<property name="names" value="dummy,submission_name,submission_date,template_name,cell_line,gene_symbol_1,gene_symbol_2,assay_type,number_of_measurements,average_fold_over_control_value,p_value,nci_portal"/>
  </bean>
```

The important thing to note here is the "names" property.  Here is listing of all column headers found in the Emory University submission template.  The first entry is always a dummy placeholder to take into account the metadata labels found in column one of each submission template.

Finally, the mapping between the Spring Batch submission processors and the DashboardDao is defined within the "observationTemplateMap" bean, which is defined after all the line mappers and tokenizers.  Here is a snippet which defines these mappings for the Emory University submission:

```
#!shell
        <entry key="emory_PPI_analysis:cell_line" value="subject:findSubjectsBySynonym" />
		<entry key="emory_PPI_analysis:gene_symbol_1" value="subject:findGenesBySymbol" />
		<entry key="emory_PPI_analysis:gene_symbol_2" value="subject:findGenesBySymbol" />
		<entry key="emory_PPI_analysis:assay_type" value="evidence:readString:createObservedLabelEvidence" />
		<entry key="emory_PPI_analysis:number_of_measurements" value="evidence:readInt:createObservedNumericEvidence" />
		<entry key="emory_PPI_analysis:average_fold_over_control_value" value="evidence:readDouble:createObservedNumericEvidence" />
		<entry key="emory_PPI_analysis:p_value" value="evidence:readDouble:createObservedNumericEvidence" />
		<entry key="emory_PPI_analysis:nci_portal" value="evidence:readString:createObservedUrlEvidence" />
``` 

For each column header in the Emory university submission template, there is an entry in the observationTemplateMap.  The key is a combination of the submission template name and column header.  The value is a combination of the following attributes:

* Submission Attribute Type: The type of the submission attribute, either 'evidence' or 'subject'.

If the submission attribute type is 'subject':

* DashboardDao Method: The DashboardDao method used to find the subject in the database.  Typically one of the following:
 * findCompoundsByName
 * findTissueSampleByName
 * findGenesByEntrezId
 * findGenesBySymbol
 * findSubjectsBySynonym (used to find cell lines)
 * findAnimalModelByName

If the submission attribute type is 'evidence':

* Evidence Read Method: Either readString, readDouble, readInt.
* Evidence Constructor: The name of the method used to create the observed evidence entry in the database.  The following methods are supported: createObservedLabelEvidence, createObservedNumericEvidence, createObservedFileEvidence, and createObservedUrlEvidence.

# Admin Tool Usage
 
The admin tool is a command line java application.  After building the admin tool from the source distribution, dashboard-admin.jar can be found within $CTD2_DASHBOARD_HOME/admin/target.  A list of commands that are recognized by the admin tool can be found by running the following command:

```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -h
```

The following commands are recognized by the admin tool:

## Import Animal Model Data (am)

This command is used to import animal model data [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -am
```

## Import Cell Line Data (cl)

This command is used to import cell line [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -cl
```

## Import Compound Data (cp)

This command is used to import compound [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -cp
```

## Import Submission Metadata (cv)

This command is used to import [submission metadata][7].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -cv
```

## Import Gene Data (g)

This command is used to import gene [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -g
```

## Index (i)

This command is used to create a lucene index for free text searching.

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -i
```

## Rank (i)

This command is used to rank subjects based on their observations (pre-processing for web site)

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -r
```

## Import Submission Data (o)

This command is used to import [submission data][6].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -o
```

## Import Protein Data (p)

This command is used to import protein [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -p
```

## Import shRNA Data (sh)

This command is used to import shRNA [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -sh
```

## Import siRNA Data (si)

This command is used to import siRNA [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -si
```

## Import Taxonomy Data (t)

This command is used to import [taxonomy data][4].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -t
```

## Import Tissue Sample Data (ts)

This command is used to import tissue sample [subject data][5].

Example usage:
```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_DASHBOARD_HOME/admin/target/dashboard-admin.jar -ts
```

## Use Case

In a typical dashboard database build, the following sequence of commands would be followed:

```
#!shell
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -t
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -am
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -cl
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -ts
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -cp
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -g
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -p
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -sh
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -si
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -cv
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -o
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -i
$JAVA_HOME/bin/java -jar $CTD2_HOME/admin/target/dashboard-admin.jar -r
```
[1]: Dashboard%20Admin%20Tool#markdown-header-introduction
[2]: Dashboard%20Admin%20Tool#markdown-header-environment-variables
[3]: Dashboard%20Admin%20Tool#markdown-header-admin-properties
[4]: Dashboard%20Admin%20Tool#markdown-header-taxonomy-list
[5]: Dashboard%20Admin%20Tool#markdown-header-subject-data
[6]: Dashboard%20Admin%20Tool#markdown-header-submission-data
[7]: Dashboard%20Admin%20Tool#markdown-header-submission-metadata
[8]: Dashboard%20Admin%20Tool#markdown-header-admin-tool-usage