package com.amazon.ata.advertising.service.businesslogic;

import com.amazon.ata.advertising.service.dao.ReadableDao;
import com.amazon.ata.advertising.service.model.*;
import com.amazon.ata.advertising.service.targeting.TargetingEvaluator;
import com.amazon.ata.advertising.service.targeting.TargetingGroup;

import com.amazon.ata.advertising.service.targeting.predicate.TargetingPredicateResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;

/**
 * This class is responsible for picking the advertisement to be rendered.
 */
public class AdvertisementSelectionLogic {

    private static final Logger LOG = LogManager.getLogger(AdvertisementSelectionLogic.class);

    private final ReadableDao<String, List<AdvertisementContent>> contentDao;
    private final ReadableDao<String, List<TargetingGroup>> targetingGroupDao;
    private Random random = new Random();

    /**
     * Constructor for AdvertisementSelectionLogic.
     *
     * @param contentDao        Source of advertising content.
     * @param targetingGroupDao Source of targeting groups for each advertising content.
     */
    @Inject
    public AdvertisementSelectionLogic(ReadableDao<String, List<AdvertisementContent>> contentDao,
                                       ReadableDao<String, List<TargetingGroup>> targetingGroupDao) {
        this.contentDao = contentDao;
        this.targetingGroupDao = targetingGroupDao;
    }

    /**
     * Setter for Random class.
     *
     * @param random generates random number used to select advertisements.
     */
    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Gets all of the content and metadata for the marketplace and determines which content can be shown.  Returns the
     * eligible content with the highest click through rate.  If no advertisement is available or eligible, returns an
     * EmptyGeneratedAdvertisement.
     *
     * @param customerId    - the customer to generate a custom advertisement for
     * @param marketplaceId - the id of the marketplace the advertisement will be rendered on
     * @return an advertisement customized for the customer id provided, or an empty advertisement if one could
     * not be generated.
     */
    public GeneratedAdvertisement selectAdvertisement(String customerId, String marketplaceId) {

        TreeMap<TargetingGroup, AdvertisementContent> treeMap =
                new TreeMap<>(Comparator.comparing(TargetingGroup::getClickThroughRate).reversed());


        GeneratedAdvertisement generatedAdvertisement = new EmptyGeneratedAdvertisement();
//        TargetingEvaluator evaluator = new TargetingEvaluator( new RequestContext(customerId, marketplaceId));
//        final List<AdvertisementContent> contents = new ArrayList<>();

        if (StringUtils.isEmpty(marketplaceId)) {
            LOG.warn("MarketplaceId cannot be null or empty. Returning empty ad.");
            return generatedAdvertisement;
        }

//        Optional<GeneratedAdvertisement> optad =  contentDao.get(marketplaceId).stream()
//                .map(advertisementContent ->
//                        targetingGroupDao.get(advertisementContent.getContentId())
//                        .stream()
//                        .sorted(Comparator.comparingDouble(TargetingGroup::getClickThroughRate).reversed())
//                        .map(evaluator::evaluate)
//                        .anyMatch(TargetingPredicateResult::isTrue) ? advertisementContent : null)
//                .filter(Objects::nonNull)
//                .findFirst()
//              .map(GeneratedAdvertisement::new);
//2
//
//        return optad.isPresent() ? optad.get() : generatedAdvertisement;

        contentDao.get(marketplaceId).stream()
                .forEach(advertisementContent -> targetingGroupDao.get(advertisementContent.getContentId())
                        .stream().filter(targetingGroup1 ->
                                new TargetingEvaluator(new RequestContext(customerId, marketplaceId)).evaluate(targetingGroup1).isTrue())
//                        .forEach(targetingGroup -> contents.add(advertisementContent)));
                        .forEach(targetingGroup -> treeMap.put(targetingGroup, advertisementContent)));
        if (!treeMap.isEmpty()) {
//            AdvertisementContent randomAdvertisementContent = contents.get(random.nextInt(contents.size()));
            generatedAdvertisement = new GeneratedAdvertisement(treeMap.firstEntry().getValue());

        }

        return generatedAdvertisement;

    }
}