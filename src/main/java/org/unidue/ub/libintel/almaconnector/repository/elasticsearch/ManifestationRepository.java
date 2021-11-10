package org.unidue.ub.libintel.almaconnector.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.unidue.ub.libintel.almaconnector.model.media.elasticsearch.EsPrintManifestation;

import java.util.List;

@Repository
public interface ManifestationRepository extends ElasticsearchRepository<EsPrintManifestation, String> {

    List<EsPrintManifestation> findManifestationByTitleIDOrAlmaId(String titleId, String almaId);
}
