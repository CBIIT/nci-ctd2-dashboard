export const supported_confidence_types = [{
    name: "p-value",
    description: "the probability that the measured interaction actually occurred by chance",
    directionality: ""
}, {
    name: "likelihood ratio",
    description: "an edge weight that indicates how strong the mutual information for an edge is when compared to the maximum observed MI in the network, it ranges between 0 and 1.",
    directionality: ""
}, {
    name: "mutual information",
    description: "quantifies the \"amount of information\" obtained about one random variable by observing the other random variable",
    directionality: ""
}, {
    name: "probability",
    description: "The probability that the given interaction actually exists in the system measured.",
    directionality: ""
}, {
    name: "mode of action",
    description: "indicates the sign of the association between regulator and target gene and ranges between -1 and +1. It is inferred by Spearman correlation analysis.",
    directionality: ""
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