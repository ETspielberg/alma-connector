package org.unidue.ub.libintel.almaconnector.model.jobs;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "job_id_with_description")
@Data
public class JobIdWithDescription {

    @Id
    @Column(name = "job_id")
    private String jobId;

    @Column(name = "description")
    private String description;

    @Column(name = "name")
    private String name;

    @Column(name = "category")
    private String category;

    public JobIdWithDescription() {}

    public JobIdWithDescription(String jobId) {
        this.jobId = jobId;
    }

    public JobIdWithDescription withDescription(String description) {
        this.description = description;
        return this;
    }

    public JobIdWithDescription withName(String name) {
        this.name= name;
        return this;
    }

    public JobIdWithDescription withCategory(String category) {
        this.category = category;
        return this;
    }
}
