const gene_cart_actions = [
    "Retrieve molecular interactions involving the selected genes from the Cellular Networks Knowledge Base (CNKB). The CNKB is a database of gene and protein interaction networks maintained at Columbia University.  It includes PREPPI, a large database of predicted and experimentally confirmed protein-protein interactions.", // CNKB
    "Send the contents of the gene cart to the external Enrichr web service for gene set enrichment analysis.  Enrichment analysis is a computational method for inferring knowledge about an input gene set by comparing it to annotated gene sets representing prior biological knowledge. Enrichment analysis checks whether an input set of genes significantly overlaps with annotated gene sets.", // Enrichr
    "STRING is a database of known and predicted protein-protein interactions. The interactions include direct (physical) and indirect (functional) associations; they stem from computational prediction, from knowledge transfer between organisms, and from interactions aggregated from other (primary) databases.", // STRING
];
const ctd2_ocg_dash = {
    "Broad Institute": "broad-institute",
    "Cold Spring Harbor Laboratory": "cold-spring-harbor-laboratory",
    "Columbia University": "columbia-university",
    "Dana-Farber Cancer Institute": "dana-farber-cancer-institute",
    "Emory University": "emory-university",
    "Fred Hutchinson Cancer Research Center (1)": "fred-hutchinson-cancer-research-center-1",
    "Fred Hutchinson Cancer Research Center (2)": "fred-hutchinson-cancer-research-center-2",
    "University of Texas MD Anderson Cancer Center": "university-of-texas-md-anderson-cancer-center",
    "Stanford University": "stanford-university",
    "University of California San Francisco (1)": "university-of-california-san-francisco-1",
    "University of California San Francisco (2)": "university-of-california-san-francisco-2",
    "University of Texas Southwestern Medical Center": "university-of-texas-southwestern-medical-center",
    "Translational Genomics Research Institute": "translational-genomics-research-institute",
    "Johns Hopkins University": "johns-hopkins-university",
    "Oregon Health and Science University": "oregon-health-and-science-university",
    "Oregon Health and Science University (2)": "oregon-health-and-science-university-2",
    "University of California San Diego": "university-of-california-san-diego",
};
const wildcard_evidence_codes = {
    measured: {
        eco_term: 'direct assay evidence',
        eco_id: 'ECO:0000002'
    },
    observed: {
        eco_term: 'ad-hoc quantitative phenotype observation evidence',
        eco_id: 'ECO:0005675'
    },
    computed: {
        eco_term: 'computational combinatorial evidence',
        eco_id: 'ECO:0000053'
    },
    background: {
        eco_term: 'inference from background scientific knowledge',
        eco_id: 'ECO:0000001'
    },
    species: {
        eco_term: 'biological system reconstruction evidence by experimental evidence from single species',
        eco_id: 'ECO:0005553'
    },
    literature: {
        eco_term: 'traceable author statement',
        eco_id: 'ECO:0000033'
    },
    written: {
        eco_term: 'author statement',
        eco_id: 'ECO:0000204'
    },
    reference: {
        eco_term: 'traceable author statement',
        eco_id: 'ECO:0000033'
    },
    resource: {
        eco_term: 'imported information',
        eco_id: 'ECO:0000311'
    },
};
const __ctd2_hovertext = {
    BROWSE_GENES: "Browse experimental evidence implicating targets in a cancer model",
    BROWSE_COMPOUNDS: "Browse experimental evidence for agents that show activity in a cancer model",
    BROWSE_DISEASE: "Browse experimental evidence for disease-specific targets or agents",
    BROWSE_STORIES: "Browse summaries of research findings described for a general scientific audience",
    BROWSE_CELLLINES: "Browse cell lines appearing in experimental observations",
    BROWSE_ECO: "Browse observations by experimental method as annotated with Evidence Ontology Codes",
    TABLE_FILTER: "Filter tabular results based on text entered",
    EXPLORE_RESET_ORDER: "Subjects are displayed based on Tier level and number of reporting Centers",
    EXPLORE_SELECT_ROLES_TARGET: "Only observations matching selected roles will be displayed, e.g. target, biomarker, etc.",
    EXPLORE_SELECT_ROLES_COMPOUND: "Only observations matching selected roles will be displayed, e.g. perturbagen, candidate drug, control compound",
    EXPLORE_SELECT_ROLES_CONTEXT: "Only observations matching selected roles will be displayed, e.g. disease, metastatis, tissue",
    EXPLORE_SELECT_ROLES_CELLLINE: "Only observations matching selected roles will be displayed, e.g. cell line",
    EXPLORE_CLASS: "Gene, compound, tissue, etc.",
    EXPLORE_NAME: "Gene symbol, compound name, etc.",
    EXPLORE_ROLE: "Subjects have designated Roles based on the interpretation of Observations from experimental or computational context",
    EXPLORE_TIER_3: 'Validation of results in a cancer-relevant in vivo model',
    EXPLORE_TIER_2: 'Confirmation of primary results in a cancer-relevant in vitro model',
    EXPLORE_TIER_1: 'Preliminary results of a screening campaign or large-scale computational analysis',
    SEARCH_CLASS: "Gene, compound, tissue, etc.",
    SEARCH_NAME: "Gene symbol, compound name, etc.",
    SEARCH_ROLES: "Subjects have designated Roles based on the interpretation of Observations from experimental or computational context",
    SEARCH_SYNONYMS: 'Known synonyms for the Subject in Name',
    SEARCH_OBSERVATIONS: 'Number of individual Dashboard Observations associated with the Subject',
    CNKB_MODULATOR_TF: 'Interactions between transcription factors and proteins modulating their regulatory activity',
    CNKB_PROTEIN_DNA: 'Interactions between transcription factors and their transcriptional targets',
    CNKB_PROTEIN_PROTEIN: 'Protein-protein interactions',
    CNKB_LIMIT: 'Limit the number of displayed interactions',
    CNKB_LAYOUT: 'Choose from several available graphical layout options',
    CNKB_CREATE_NETWORK: 'Plot network interaction diagram',
    CENTER_LIST: 'Number of Dashboard “Submissions” contributed by a Center',
    GENE_CART: 'Build or edit a list of genes and query for molecular interactions',
    ALL_TIERS: 'Tier 1: Preliminary results of a screening campaign or large-scale computational analysis.<br/>Tier 2: Confirmation of primary results in a cancer-relevant in vitro model.<br/>Tier 3: Validation of results in a cancer relevant in vivo model.',
};
const ctd2_role_definition = {
    'target': 'a gene or protein targeted by chemical or genetic perturbagens',
    'biomarker': 'a gene product or other signal whose presence is an indication of a phenotype or activity',
    'oncogene': 'a gene known to be involved in causing cancer',
    'perturbagen': 'a substance (small molecule, shRNA, peptide, etc.) designed to disrupt intracellular processes',
    'master regulator': 'a gene for a transcription factor or signaling protein controlling the expression or repression of a number of direct target genes',
    'candidate master regulator': 'a master regulator identified in a computational screen but not yet confirmed in vitro',
    'interactor': 'a Dashboard "subject" involved in an interaction network',
    'background': 'a Dashboard "subject" necessary to understand the importance of the study but not part of that particular study',
    'candidate drug': 'a compound investigated as a potential new drug or for a new indication',
    'control compound': 'a compound used as control in an experiment',
    'metabolite': 'product of enzyme-catalyzed reaction',
    'disease': 'the condition or disease being investigated',
    'metastasis': 'spread of cancer cells to new areas of the body',
    'tissue': 'description of tissue used or investigated in an experiment',
    'cell line': 'a cell line used in an experiment',
    'strain': 'strain of an animal model',
    'tumor suppressor': '"a gene which regulates a cell during cell division and replication. Loss of function can lead to cells growing abnormally',
};