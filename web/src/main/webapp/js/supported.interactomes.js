export const supported_confidence_types = [{
    name: "p-value",
    description: "Probability that the measured interaction occurred by chance, under a null model.",
    directionality: "decreasing"
}, {
    name: "likelihood ratio",
    description: "Ratio of interaction mutual information divided by the maximum mutual information observed in the network (ranges between 0 and 1).",
    directionality: "increasing"
}, {
    name: "mutual information",
    description: "Measure of statistical correlation between the gene expression of the two genes comprising the interaction.",
    directionality: "increasing"
}, {
    name: "probability",
    description: "The probability that the given interaction actually exists in the system measured.",
    directionality: "increasing"
}, {
    name: "mode of action",
    description: "Spearman correlation between the gene expression of the two genes comprising the interaction (ranges between -1 and 1). Provides information about the possible mode of mutual regulation (activating or repressing, depending on positive or negative MoA).",
    directionality: "increasing" /* absolute values */
}, ]

export const supported_interactomes = [{
    name: "preppi",
    confidence_types: ["probability"]
}, {
    name: "blca_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "brca_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "cesc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "coad_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "esca_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "gbm_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "hnsc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "kirc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "kirp_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "laml_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "lgg_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "lihc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "luad_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "lusc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "ov_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "paad_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "pcpg_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "prad_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "read_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "sarc_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "skcm_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "stad_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "tgct_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "thca_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "thym_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, {
    name: "ucec_tcga",
    confidence_types: ["p-value", "likelihood ratio", "mutual information", "mode of action"]
}, ]