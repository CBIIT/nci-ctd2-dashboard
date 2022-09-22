const flare = {
    "name": "flare",
    "children": [{
        "name": "analytics",
        "children": [{
            "name": "cluster",
            "children": [
                { "name": "AgglomerativeCluster", "size": 3938 },
                { "name": "CommunityStructure", "size": 3812 },
                { "name": "HierarchicalCluster", "size": 6714 },
                { "name": "MergeEdge", "size": 743 }
            ]
        },
        {
            "name": "graph",
            "children": [
                { "name": "BetweennessCentrality", "size": 3534 },
                { "name": "LinkDistance", "size": 5731 },
                { "name": "MaxFlowMinCut", "size": 7840 },
                { "name": "ShortestPaths", "size": 5914 },
                { "name": "SpanningTree", "size": 3416 }
            ]
        },
        {
            "name": "optimization",
            "children": [
                { "name": "AspectRatioBanker", "size": 7074 }
            ]
        }
        ]
    },
    {
        "name": "animate",
        "children": [
            { "name": "Easing", "size": 17010 },
            { "name": "FunctionSequence", "size": 5842 },
            {
                "name": "interpolate",
                "children": [
                    { "name": "ArrayInterpolator", "size": 1983 },
                    { "name": "ColorInterpolator", "size": 2047 },
                    { "name": "DateInterpolator", "size": 1375 },
                    { "name": "Interpolator", "size": 8746 },
                    { "name": "MatrixInterpolator", "size": 2202 },
                    { "name": "NumberInterpolator", "size": 1382 },
                    { "name": "ObjectInterpolator", "size": 1629 },
                    { "name": "PointInterpolator", "size": 1675 },
                    { "name": "RectangleInterpolator", "size": 2042 }
                ]
            },
            { "name": "ISchedulable", "size": 1041 },
            { "name": "Parallel", "size": 5176 },
            { "name": "Pause", "size": 449 },
            { "name": "Scheduler", "size": 5593 },
            { "name": "Sequence", "size": 5534 },
            { "name": "Transition", "size": 9201 },
            { "name": "Transitioner", "size": 19975 },
            { "name": "TransitionEvent", "size": 1116 },
            { "name": "Tween", "size": 6006 }
        ]
    },
    {
        "name": "data",
        "children": [{
            "name": "converters",
            "children": [
                { "name": "Converters", "size": 721 },
                { "name": "DelimitedTextConverter", "size": 4294 },
                { "name": "GraphMLConverter", "size": 9800 },
                { "name": "IDataConverter", "size": 1314 },
                { "name": "JSONConverter", "size": 2220 }
            ]
        },
        { "name": "DataField", "size": 1759 },
        { "name": "DataSchema", "size": 2165 },
        { "name": "DataSet", "size": 586 },
        { "name": "DataSource", "size": 3331 },
        { "name": "DataTable", "size": 772 },
        { "name": "DataUtil", "size": 3322 }
        ]
    },
    {
        "name": "display",
        "children": [
            { "name": "DirtySprite", "size": 8833 },
            { "name": "LineSprite", "size": 1732 },
            { "name": "RectSprite", "size": 3623 },
            { "name": "TextSprite", "size": 10066 }
        ]
    },
    {
        "name": "flex",
        "children": [
            { "name": "FlareVis", "size": 4116 }
        ]
    },
    {
        "name": "physics",
        "children": [
            { "name": "DragForce", "size": 1082 },
            { "name": "GravityForce", "size": 1336 },
            { "name": "IForce", "size": 319 },
            { "name": "NBodyForce", "size": 10498 },
            { "name": "Particle", "size": 2822 },
            { "name": "Simulation", "size": 9983 },
            { "name": "Spring", "size": 2213 },
            { "name": "SpringForce", "size": 1681 }
        ]
    },
    {
        "name": "query",
        "children": [
            { "name": "AggregateExpression", "size": 1616 },
            { "name": "And", "size": 1027 },
            { "name": "Arithmetic", "size": 3891 },
            { "name": "Average", "size": 891 },
            { "name": "BinaryExpression", "size": 2893 },
            { "name": "Comparison", "size": 5103 },
            { "name": "CompositeExpression", "size": 3677 },
            { "name": "Count", "size": 781 },
            { "name": "DateUtil", "size": 4141 },
            { "name": "Distinct", "size": 933 },
            { "name": "Expression", "size": 5130 },
            { "name": "ExpressionIterator", "size": 3617 },
            { "name": "Fn", "size": 3240 },
            { "name": "If", "size": 2732 },
            { "name": "IsA", "size": 2039 },
            { "name": "Literal", "size": 1214 },
            { "name": "Match", "size": 3748 },
            { "name": "Maximum", "size": 843 },
            {
                "name": "methods",
                "children": [
                    { "name": "add", "size": 593 },
                    { "name": "and", "size": 330 },
                    { "name": "average", "size": 287 },
                    { "name": "count", "size": 277 },
                    { "name": "distinct", "size": 292 },
                    { "name": "div", "size": 595 },
                    { "name": "eq", "size": 594 },
                    { "name": "fn", "size": 460 },
                    { "name": "gt", "size": 603 },
                    { "name": "gte", "size": 625 },
                    { "name": "iff", "size": 748 },
                    { "name": "isa", "size": 461 },
                    { "name": "lt", "size": 597 },
                    { "name": "lte", "size": 619 },
                    { "name": "max", "size": 283 },
                    { "name": "min", "size": 283 },
                    { "name": "mod", "size": 591 },
                    { "name": "mul", "size": 603 },
                    { "name": "neq", "size": 599 },
                    { "name": "not", "size": 386 },
                    { "name": "or", "size": 323 },
                    { "name": "orderby", "size": 307 },
                    { "name": "range", "size": 772 },
                    { "name": "select", "size": 296 },
                    { "name": "stddev", "size": 363 },
                    { "name": "sub", "size": 600 },
                    { "name": "sum", "size": 280 },
                    { "name": "update", "size": 307 },
                    { "name": "variance", "size": 335 },
                    { "name": "where", "size": 299 },
                    { "name": "xor", "size": 354 },
                    { "name": "_", "size": 264 }
                ]
            },
            { "name": "Minimum", "size": 843 },
            { "name": "Not", "size": 1554 },
            { "name": "Or", "size": 970 },
            { "name": "Query", "size": 13896 },
            { "name": "Range", "size": 1594 },
            { "name": "StringUtil", "size": 4130 },
            { "name": "Sum", "size": 791 },
            { "name": "Variable", "size": 1124 },
            { "name": "Variance", "size": 1876 },
            { "name": "Xor", "size": 1101 }
        ]
    },
    {
        "name": "scale",
        "children": [
            { "name": "IScaleMap", "size": 2105 },
            { "name": "LinearScale", "size": 1316 },
            { "name": "LogScale", "size": 3151 },
            { "name": "OrdinalScale", "size": 3770 },
            { "name": "QuantileScale", "size": 2435 },
            { "name": "QuantitativeScale", "size": 4839 },
            { "name": "RootScale", "size": 1756 },
            { "name": "Scale", "size": 4268 },
            { "name": "ScaleType", "size": 1821 },
            { "name": "TimeScale", "size": 5833 }
        ]
    },
    {
        "name": "util",
        "children": [
            { "name": "Arrays", "size": 8258 },
            { "name": "Colors", "size": 10001 },
            { "name": "Dates", "size": 8217 },
            { "name": "Displays", "size": 12555 },
            { "name": "Filter", "size": 2324 },
            { "name": "Geometry", "size": 10993 },
            {
                "name": "heap",
                "children": [
                    { "name": "FibonacciHeap", "size": 9354 },
                    { "name": "HeapNode", "size": 1233 }
                ]
            },
            { "name": "IEvaluable", "size": 335 },
            { "name": "IPredicate", "size": 383 },
            { "name": "IValueProxy", "size": 874 },
            {
                "name": "math",
                "children": [
                    { "name": "DenseMatrix", "size": 3165 },
                    { "name": "IMatrix", "size": 2815 },
                    { "name": "SparseMatrix", "size": 3366 }
                ]
            },
            { "name": "Maths", "size": 17705 },
            { "name": "Orientation", "size": 1486 },
            {
                "name": "palette",
                "children": [
                    { "name": "ColorPalette", "size": 6367 },
                    { "name": "Palette", "size": 1229 },
                    { "name": "ShapePalette", "size": 2059 },
                    { "name": "SizePalette", "size": 2291 }
                ]
            },
            { "name": "Property", "size": 5559 },
            { "name": "Shapes", "size": 19118 },
            { "name": "Sort", "size": 6887 },
            { "name": "Stats", "size": 6557 },
            { "name": "Strings", "size": 22026 }
        ]
    },
    {
        "name": "vis",
        "children": [{
            "name": "axis",
            "children": [
                { "name": "Axes", "size": 1302 },
                { "name": "Axis", "size": 24593 },
                { "name": "AxisGridLine", "size": 652 },
                { "name": "AxisLabel", "size": 636 },
                { "name": "CartesianAxes", "size": 6703 }
            ]
        },
        {
            "name": "controls",
            "children": [
                { "name": "AnchorControl", "size": 2138 },
                { "name": "ClickControl", "size": 3824 },
                { "name": "Control", "size": 1353 },
                { "name": "ControlList", "size": 4665 },
                { "name": "DragControl", "size": 2649 },
                { "name": "ExpandControl", "size": 2832 },
                { "name": "HoverControl", "size": 4896 },
                { "name": "IControl", "size": 763 },
                { "name": "PanZoomControl", "size": 5222 },
                { "name": "SelectionControl", "size": 7862 },
                { "name": "TooltipControl", "size": 8435 }
            ]
        },
        {
            "name": "data",
            "children": [
                { "name": "Data", "size": 20544 },
                { "name": "DataList", "size": 19788 },
                { "name": "DataSprite", "size": 10349 },
                { "name": "EdgeSprite", "size": 3301 },
                { "name": "NodeSprite", "size": 19382 },
                {
                    "name": "render",
                    "children": [
                        { "name": "ArrowType", "size": 698 },
                        { "name": "EdgeRenderer", "size": 5569 },
                        { "name": "IRenderer", "size": 353 },
                        { "name": "ShapeRenderer", "size": 2247 }
                    ]
                },
                { "name": "ScaleBinding", "size": 11275 },
                { "name": "Tree", "size": 7147 },
                { "name": "TreeBuilder", "size": 9930 }
            ]
        },
        {
            "name": "events",
            "children": [
                { "name": "DataEvent", "size": 2313 },
                { "name": "SelectionEvent", "size": 1880 },
                { "name": "TooltipEvent", "size": 1701 },
                { "name": "VisualizationEvent", "size": 1117 }
            ]
        },
        {
            "name": "legend",
            "children": [
                { "name": "Legend", "size": 20859 },
                { "name": "LegendItem", "size": 4614 },
                { "name": "LegendRange", "size": 10530 }
            ]
        },
        {
            "name": "operator",
            "children": [{
                "name": "distortion",
                "children": [
                    { "name": "BifocalDistortion", "size": 4461 },
                    { "name": "Distortion", "size": 6314 },
                    { "name": "FisheyeDistortion", "size": 3444 }
                ]
            },
            {
                "name": "encoder",
                "children": [
                    { "name": "ColorEncoder", "size": 3179 },
                    { "name": "Encoder", "size": 4060 },
                    { "name": "PropertyEncoder", "size": 4138 },
                    { "name": "ShapeEncoder", "size": 1690 },
                    { "name": "SizeEncoder", "size": 1830 }
                ]
            },
            {
                "name": "filter",
                "children": [
                    { "name": "FisheyeTreeFilter", "size": 5219 },
                    { "name": "GraphDistanceFilter", "size": 3165 },
                    { "name": "VisibilityFilter", "size": 3509 }
                ]
            },
            { "name": "IOperator", "size": 1286 },
            {
                "name": "label",
                "children": [
                    { "name": "Labeler", "size": 9956 },
                    { "name": "RadialLabeler", "size": 3899 },
                    { "name": "StackedAreaLabeler", "size": 3202 }
                ]
            },
            {
                "name": "layout",
                "children": [
                    { "name": "AxisLayout", "size": 6725 },
                    { "name": "BundledEdgeRouter", "size": 3727 },
                    { "name": "CircleLayout", "size": 9317 },
                    { "name": "CirclePackingLayout", "size": 12003 },
                    { "name": "DendrogramLayout", "size": 4853 },
                    { "name": "ForceDirectedLayout", "size": 8411 },
                    { "name": "IcicleTreeLayout", "size": 4864 },
                    { "name": "IndentedTreeLayout", "size": 3174 },
                    { "name": "Layout", "size": 7881 },
                    { "name": "NodeLinkTreeLayout", "size": 12870 },
                    { "name": "PieLayout", "size": 2728 },
                    { "name": "RadialTreeLayout", "size": 12348 },
                    { "name": "RandomLayout", "size": 870 },
                    { "name": "StackedAreaLayout", "size": 9121 },
                    { "name": "TreeMapLayout", "size": 9191 }
                ]
            },
            { "name": "Operator", "size": 2490 },
            { "name": "OperatorList", "size": 5248 },
            { "name": "OperatorSequence", "size": 4190 },
            { "name": "OperatorSwitch", "size": 2581 },
            { "name": "SortOperator", "size": 2023 }
            ]
        },
        { "name": "Visualization", "size": 16540 }
        ]
    }
    ]
}

const data2 = {
    "children": [
        {
            "name": "Activity",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "name": "Agent",
                    "parent": "AdministrativeEntity"
                }
            ],
            "name": "AdministrativeEntity",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "name": "BehavioralExposure",
                    "parent": "Attribute"
                },
                {
                    "children": [
                        {
                            "name": "GenotypicSex",
                            "parent": "BiologicalSex"
                        },
                        {
                            "name": "PhenotypicSex",
                            "parent": "BiologicalSex"
                        }
                    ],
                    "name": "BiologicalSex",
                    "parent": "Attribute"
                },
                {
                    "name": "BioticExposure",
                    "parent": "Attribute"
                },
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "DrugToGeneInteractionExposure",
                                    "parent": "DrugExposure"
                                }
                            ],
                            "name": "DrugExposure",
                            "parent": "ChemicalExposure"
                        }
                    ],
                    "name": "ChemicalExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "ChemicalRole",
                    "parent": "Attribute"
                },
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "Onset",
                                    "parent": "ClinicalCourse"
                                }
                            ],
                            "name": "ClinicalCourse",
                            "parent": "ClinicalAttribute"
                        },
                        {
                            "name": "ClinicalMeasurement",
                            "parent": "ClinicalAttribute"
                        },
                        {
                            "name": "ClinicalModifier",
                            "parent": "ClinicalAttribute"
                        }
                    ],
                    "name": "ClinicalAttribute",
                    "parent": "Attribute"
                },
                {
                    "name": "ComplexChemicalExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "DiseaseOrPhenotypicFeatureExposure",
                    "parent": "Attribute"
                },
                {
                    "children": [
                        {
                            "name": "GeographicExposure",
                            "parent": "EnvironmentalExposure"
                        }
                    ],
                    "name": "EnvironmentalExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "GenomicBackgroundExposure",
                    "parent": "Attribute"
                },
                {
                    "children": [
                        {
                            "name": "Inheritance",
                            "parent": "OrganismAttribute"
                        },
                        {
                            "name": "PhenotypicQuality",
                            "parent": "OrganismAttribute"
                        }
                    ],
                    "name": "OrganismAttribute",
                    "parent": "Attribute"
                },
                {
                    "name": "PathologicalAnatomicalExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "PathologicalProcessExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "SeverityValue",
                    "parent": "Attribute"
                },
                {
                    "name": "SocioeconomicAttribute",
                    "parent": "Attribute"
                },
                {
                    "name": "SocioeconomicExposure",
                    "parent": "Attribute"
                },
                {
                    "name": "Zygosity",
                    "parent": "Attribute"
                }
            ],
            "name": "Attribute",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "Behavior",
                                    "parent": "BiologicalProcess"
                                },
                                {
                                    "name": "PathologicalProcess",
                                    "parent": "BiologicalProcess"
                                },
                                {
                                    "name": "Pathway",
                                    "parent": "BiologicalProcess"
                                },
                                {
                                    "name": "PhysiologicalProcess",
                                    "parent": "BiologicalProcess"
                                }
                            ],
                            "name": "BiologicalProcess",
                            "parent": "BiologicalProcessOrActivity"
                        },
                        {
                            "name": "MolecularActivity",
                            "parent": "BiologicalProcessOrActivity"
                        }
                    ],
                    "name": "BiologicalProcessOrActivity",
                    "parent": "BiologicalEntity"
                },
                {
                    "children": [
                        {
                            "name": "Disease",
                            "parent": "DiseaseOrPhenotypicFeature"
                        },
                        {
                            "children": [
                                {
                                    "name": "BehavioralFeature",
                                    "parent": "PhenotypicFeature"
                                },
                                {
                                    "name": "ClinicalFinding",
                                    "parent": "PhenotypicFeature"
                                }
                            ],
                            "name": "PhenotypicFeature",
                            "parent": "DiseaseOrPhenotypicFeature"
                        }
                    ],
                    "name": "DiseaseOrPhenotypicFeature",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "Gene",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "GeneFamily",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "GeneticInheritance",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "Genome",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "Genotype",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "Haplotype",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "NucleicAcidSequenceMotif",
                    "parent": "BiologicalEntity"
                },
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "Cell",
                                    "parent": "AnatomicalEntity"
                                },
                                {
                                    "name": "CellularComponent",
                                    "parent": "AnatomicalEntity"
                                },
                                {
                                    "name": "GrossAnatomicalStructure",
                                    "parent": "AnatomicalEntity"
                                },
                                {
                                    "name": "PathologicalAnatomicalStructure",
                                    "parent": "AnatomicalEntity"
                                }
                            ],
                            "name": "AnatomicalEntity",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "name": "CellLine",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "name": "CellularOrganism",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "children": [
                                {
                                    "name": "Case",
                                    "parent": "IndividualOrganism"
                                }
                            ],
                            "name": "IndividualOrganism",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "name": "LifeStage",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "children": [
                                {
                                    "children": [
                                        {
                                            "name": "Cohort",
                                            "parent": "StudyPopulation"
                                        }
                                    ],
                                    "name": "StudyPopulation",
                                    "parent": "PopulationOfIndividualOrganisms"
                                }
                            ],
                            "name": "PopulationOfIndividualOrganisms",
                            "parent": "OrganismalEntity"
                        },
                        {
                            "name": "Virus",
                            "parent": "OrganismalEntity"
                        }
                    ],
                    "name": "OrganismalEntity",
                    "parent": "BiologicalEntity"
                },
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "ProteinIsoform",
                                    "parent": "Protein"
                                }
                            ],
                            "name": "Protein",
                            "parent": "Polypeptide"
                        }
                    ],
                    "name": "Polypeptide",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "ProteinDomain",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "ProteinFamily",
                    "parent": "BiologicalEntity"
                },
                {
                    "name": "ReagentTargetedGene",
                    "parent": "BiologicalEntity"
                },
                {
                    "children": [
                        {
                            "name": "Snv",
                            "parent": "SequenceVariant"
                        }
                    ],
                    "name": "SequenceVariant",
                    "parent": "BiologicalEntity"
                }
            ],
            "name": "BiologicalEntity",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "children": [
                        {
                            "name": "ComplexMolecularMixture",
                            "parent": "ChemicalMixture"
                        },
                        {
                            "name": "Food",
                            "parent": "ChemicalMixture"
                        },
                        {
                            "children": [
                                {
                                    "name": "Drug",
                                    "parent": "MolecularMixture"
                                }
                            ],
                            "name": "MolecularMixture",
                            "parent": "ChemicalMixture"
                        },
                        {
                            "name": "ProcessedMaterial",
                            "parent": "ChemicalMixture"
                        }
                    ],
                    "name": "ChemicalMixture",
                    "parent": "ChemicalEntity"
                },
                {
                    "name": "EnvironmentalFoodContaminant",
                    "parent": "ChemicalEntity"
                },
                {
                    "name": "FoodAdditive",
                    "parent": "ChemicalEntity"
                },
                {
                    "children": [
                        {
                            "children": [
                                {
                                    "name": "CodingSequence",
                                    "parent": "NucleicAcidEntity"
                                },
                                {
                                    "name": "Exon",
                                    "parent": "NucleicAcidEntity"
                                },
                                {
                                    "children": [
                                        {
                                            "children": [
                                                {
                                                    "children": [
                                                        {
                                                            "name": "MicroRNA",
                                                            "parent": "NoncodingRNAProduct"
                                                        },
                                                        {
                                                            "name": "SiRNA",
                                                            "parent": "NoncodingRNAProduct"
                                                        }
                                                    ],
                                                    "name": "NoncodingRNAProduct",
                                                    "parent": "RNAProduct"
                                                },
                                                {
                                                    "name": "RNAProductIsoform",
                                                    "parent": "RNAProduct"
                                                }
                                            ],
                                            "name": "RNAProduct",
                                            "parent": "Transcript"
                                        }
                                    ],
                                    "name": "Transcript",
                                    "parent": "NucleicAcidEntity"
                                }
                            ],
                            "name": "NucleicAcidEntity",
                            "parent": "MolecularEntity"
                        },
                        {
                            "name": "SmallMolecule",
                            "parent": "MolecularEntity"
                        }
                    ],
                    "name": "MolecularEntity",
                    "parent": "ChemicalEntity"
                },
                {
                    "children": [
                        {
                            "name": "Macronutrient",
                            "parent": "Nutrient"
                        },
                        {
                            "children": [
                                {
                                    "name": "Vitamin",
                                    "parent": "Micronutrient"
                                }
                            ],
                            "name": "Micronutrient",
                            "parent": "Nutrient"
                        }
                    ],
                    "name": "Nutrient",
                    "parent": "ChemicalEntity"
                }
            ],
            "name": "ChemicalEntity",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "children": [
                        {
                            "name": "Hospitalization",
                            "parent": "ClinicalIntervention"
                        }
                    ],
                    "name": "ClinicalIntervention",
                    "parent": "ClinicalEntity"
                },
                {
                    "name": "ClinicalTrial",
                    "parent": "ClinicalEntity"
                }
            ],
            "name": "ClinicalEntity",
            "parent": "NamedThing"
        },
        {
            "name": "Device",
            "parent": "NamedThing"
        },
        {
            "name": "Event",
            "parent": "NamedThing"
        },
        {
            "name": "ExposureEvent",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "name": "ConfidenceLevel",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "Dataset",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "DatasetDistribution",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "DatasetSummary",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "DatasetVersion",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "EvidenceType",
                    "parent": "InformationContentEntity"
                },
                {
                    "name": "InformationResource",
                    "parent": "InformationContentEntity"
                },
                {
                    "children": [
                        {
                            "name": "Article",
                            "parent": "Publication"
                        },
                        {
                            "name": "Book",
                            "parent": "Publication"
                        },
                        {
                            "name": "BookChapter",
                            "parent": "Publication"
                        },
                        {
                            "name": "Serial",
                            "parent": "Publication"
                        }
                    ],
                    "name": "Publication",
                    "parent": "InformationContentEntity"
                },
                {
                    "children": [
                        {
                            "name": "ChiSquaredAnalysisResult",
                            "parent": "StudyResult"
                        },
                        {
                            "name": "ConceptCountAnalysisResult",
                            "parent": "StudyResult"
                        },
                        {
                            "name": "ObservedExpectedFrequencyAnalysisResult",
                            "parent": "StudyResult"
                        },
                        {
                            "name": "RelativeFrequencyAnalysisResult",
                            "parent": "StudyResult"
                        },
                        {
                            "name": "TextMiningResult",
                            "parent": "StudyResult"
                        }
                    ],
                    "name": "StudyResult",
                    "parent": "InformationContentEntity"
                }
            ],
            "name": "InformationContentEntity",
            "parent": "NamedThing"
        },
        {
            "name": "OrganismTaxon",
            "parent": "NamedThing"
        },
        {
            "name": "Phenomenon",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "name": "MaterialSample",
                    "parent": "PhysicalEntity"
                }
            ],
            "name": "PhysicalEntity",
            "parent": "NamedThing"
        },
        {
            "children": [
                {
                    "name": "EnvironmentalFeature",
                    "parent": "PlanetaryEntity"
                },
                {
                    "name": "EnvironmentalProcess",
                    "parent": "PlanetaryEntity"
                },
                {
                    "children": [
                        {
                            "name": "GeographicLocationAtTime",
                            "parent": "GeographicLocation"
                        }
                    ],
                    "name": "GeographicLocation",
                    "parent": "PlanetaryEntity"
                }
            ],
            "name": "PlanetaryEntity",
            "parent": "NamedThing"
        },
        {
            "name": "Procedure",
            "parent": "NamedThing"
        },
        {
            "name": "Treatment",
            "parent": "NamedThing"
        }
    ],
    "name": "NamedThing",
    "parent": null
}

// Copyright 2021 Observable, Inc.
// Released under the ISC license.
// https://observablehq.com/@d3/tree
function Tree(data, { // data is either tabular (array of objects) or hierarchy (nested objects)
    path, // as an alternative to id and parentId, returns an array identifier, imputing internal nodes
    id = Array.isArray(data) ? d => d.id : null, // if tabular data, given a d in data, returns a unique identifier (string)
    parentId = Array.isArray(data) ? d => d.parentId : null, // if tabular data, given a node d, returns its parent’s identifier
    children, // if hierarchical data, given a d in data, returns its children
    tree = d3.tree, // layout algorithm (typically d3.tree or d3.cluster)
    sort, // how to sort nodes prior to layout (e.g., (a, b) => d3.descending(a.height, b.height))
    label, // given a node d, returns the display name
    title, // given a node d, returns its hover text
    link, // given a node d, its link (if any)
    linkTarget = "_blank", // the target attribute for links (if any)
    width = 640, // outer width, in pixels
    height, // outer height, in pixels
    r = 3, // radius of nodes
    padding = 1, // horizontal padding for first and last column
    fill = "#999", // fill for nodes
    fillOpacity, // fill opacity for nodes
    stroke = "#555", // stroke for links
    strokeWidth = 1.5, // stroke width for links
    strokeOpacity = 0.4, // stroke opacity for links
    strokeLinejoin, // stroke line join for links
    strokeLinecap, // stroke line cap for links
    halo = "#fff", // color of label halo 
    haloWidth = 3, // padding around the labels
} = {}) {

    // If id and parentId options are specified, or the path option, use d3.stratify
    // to convert tabular data to a hierarchy; otherwise we assume that the data is
    // specified as an object {children} with nested objects (a.k.a. the “flare.json”
    // format), and use d3.hierarchy.
    const root = path != null ? d3.stratify().path(path)(data) :
        id != null || parentId != null ? d3.stratify().id(id).parentId(parentId)(data) :
            d3.hierarchy(data, children);

    // Sort the nodes.
    if (sort != null) root.sort(sort);

    // Compute labels and titles.
    const descendants = root.descendants();
    const L = label == null ? null : descendants.map(d => label(d.data, d));

    // Compute the layout.
    const dx = 10;
    const dy = width / (root.height + padding);
    tree().nodeSize([dx, dy])(root);

    // Center the tree.
    let x0 = Infinity;
    let x1 = -x0;
    root.each(d => {
        if (d.x > x1) x1 = d.x;
        if (d.x < x0) x0 = d.x;
    });

    // Compute the default height.
    if (height === undefined) height = x1 - x0 + dx * 2;

    const svg = d3.create("svg")
        .attr("viewBox", [-dy * padding / 2, x0 - dx, width, height])
        .attr("width", width)
        .attr("height", height)
        .attr("style", "max-width: 100%; height: auto; height: intrinsic;")
        .attr("font-family", "sans-serif")
        .attr("font-size", 10);

    svg.append("g")
        .attr("fill", "none")
        .attr("stroke", stroke)
        .attr("stroke-opacity", strokeOpacity)
        .attr("stroke-linecap", strokeLinecap)
        .attr("stroke-linejoin", strokeLinejoin)
        .attr("stroke-width", strokeWidth)
        .selectAll("path")
        .data(root.links())
        .join("path")
        .attr("d", d3.linkHorizontal()
            .x(d => d.y)
            .y(d => d.x));

    const node = svg.append("g")
        .selectAll("a")
        .data(root.descendants())
        .join("a")
        .attr("xlink:href", link == null ? null : d => link(d.data, d))
        .attr("target", link == null ? null : linkTarget)
        .attr("transform", d => `translate(${d.y},${d.x})`);

    node.append("circle")
        .attr("fill", d => d.children ? stroke : fill)
        .attr("r", r);

    if (title != null) node.append("title")
        .text(d => title(d.data, d));

    if (L) node.append("text")
        .attr("dy", "0.32em")
        .attr("x", d => d.children ? -6 : 6)
        .attr("text-anchor", d => d.children ? "end" : "start")
        .attr("paint-order", "stroke")
        .attr("stroke", halo)
        .attr("stroke-width", haloWidth)
        .text((d, i) => L[i]);

    return svg.node();
}

export { Tree as default, flare, data2 };