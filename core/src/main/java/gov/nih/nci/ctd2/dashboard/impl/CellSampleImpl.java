package gov.nih.nci.ctd2.dashboard.impl;

import gov.nih.nci.ctd2.dashboard.model.CellSample;
import gov.nih.nci.ctd2.dashboard.model.Annotation;
import org.hibernate.annotations.Proxy;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

import java.util.Set;
import java.util.HashSet;

@Entity
@Proxy(proxyClass= CellSample.class)
@Table(name = "cell_sample")
@Indexed
public class CellSampleImpl extends SubjectWithOrganismImpl implements CellSample {
    private String gender;
    private Set<Annotation> annotations = new HashSet<Annotation>();

    @Column(length = 128, nullable = true)
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(targetEntity = AnnotationImpl.class, cascade = CascadeType.ALL)
    @JoinTable(name = "cell_sample_annotation_map")
    public Set<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<Annotation> annotations) {
        this.annotations = annotations;
    }
}
