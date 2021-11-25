package gov.nih.nci.ctd2.dashboard.impl;

import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;

public class CTD2AnalysisConfigurer implements LuceneAnalysisConfigurer {

        @Override
        public void configure(LuceneAnalysisConfigurationContext context) {
                context.analyzer("ctd2analyzer").custom().tokenizer(WhitespaceTokenizerFactory.class)
                                .tokenFilter(LowerCaseFilterFactory.class).tokenFilter(StopFilterFactory.class)
                                .param("ignoreCase", "true");
        }
}