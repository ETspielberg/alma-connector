package org.unidue.ub.libintel.almaconnector.model.jobs;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * POJO containing the data for constructing WAYFless URLs
 */
@Entity
@Data
@Table(name="shibboleth_data")
public class ShibbolethData {

    @Id
    @Column(name="host")
    private String host;

    @Column(name="serviceprovider_sibboleth_url")
    private String serviceproviderSibbolethUrl;

    @Column(name="sp_side_wayfless")
    private boolean spSideWayfless = false;

    @Column(name="entity_id_string")
    private String entityIdString = "entityID";

    @Column(name="target_string")
    private String targetString = "target";

    @Column(name="shire")
    private String shire;

    @Column(name="provider_id")
    private String providerId;

    @Column(name="additional_url_parameters")
    private String additionalUrlParameters;
}
