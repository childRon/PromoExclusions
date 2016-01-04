package org.promo;

import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;

import java.util.*;


public class PromoExclusionResolver {

    /**
     *
     * {@param promos} - initial global list of promos
     * {@param promoExclusions} - initial global list of promo exclusions
     *
     * */
    public Set<Promo> resolvePromos(List<Promo> promos, List<PromoExclusion> promoExclusions) {

        // create empty containers for storing processed entities
        Set<Promo> calculatedPromos = new TreeSet<Promo>();
        Set<PromoExclusion> calculatedExclusions = new LinkedHashSet<PromoExclusion>();

        return resolvePromos(promos, promoExclusions, calculatedPromos, calculatedExclusions);
    }

    /**
     *
     *
     *  @param promos - set A
     *  @param promoExclusions - set X
     *  @param calculatedPromos - set Q
     *  @param calculatedExclusions - set U
     *
     * */

    private Set<Promo> resolvePromos(List<Promo> promos, Collection<PromoExclusion> promoExclusions, Set<Promo> calculatedPromos, Collection<PromoExclusion> calculatedExclusions) {
        //sort initial collection of promo by priority
        TreeSet<Promo> sortedPromos = new TreeSet<Promo>(promos);
        // iterate throw initial collections of promo
        for (Promo promo : sortedPromos) {

            // check whether iterated promo was excluded before
            if (checkExclusions(promo, calculatedExclusions)) {
                //if promo was excluded before - proceed to the next promo in global collection
                continue;
            }

            // find out exclusions that creates current iterated promo
            List<PromoExclusion> exclusionsCreatedByPromo = findExclusionsCreatedByPromo(promo, promoExclusions);
            // mark current promo as processed (add it to container of processed promos)
            calculatedPromos.add(promo);
            // mark exclusions that promo creates as processed (add it to container of processed promo exclusions)
            calculatedExclusions.addAll(exclusionsCreatedByPromo);
            // retrieve promos that have been excluded by current iterated promo
            Set<Promo> excludedPromos = retrieveExcludedPromos(promo, exclusionsCreatedByPromo);

            // iterate throw excluded promos
            for (Promo excludedPromo : excludedPromos) {
                // check whether excluded promo has lower priority than iterated global promo or excluded promo is calculated before
                if (calculatedPromos.contains(excludedPromo) || excludedPromo.getPriority() < promo.getPriority()) {
                    // unmark excluded promo from processed list
                    removeFromCalculatedPromos(calculatedPromos, excludedPromo);
                    // unmark exclusions that excluded promo created from processed list
                    Set<Promo> promoListToCheck = removePromoExclusions(excludedPromo, calculatedExclusions);
                    // fetch all promo that have been iterated before current promo
                    List<Promo> subPromos = createSubList(promo, promos);
                    // invoke recursively current method for created sub list with populated containers of processed promo and exclusions
                    resolvePromos(subPromos, promoExclusions, calculatedPromos, calculatedExclusions);
                }
            }
        }

        return calculatedPromos;
    }

    private void removeFromCalculatedPromos(Set<Promo> calculatedPromos, Promo excludedPromo) {
        calculatedPromos.remove(excludedPromo);
    }

    private List<Promo> createSubList(Promo promo, List<Promo> promos) {
        List<Promo> subList = new LinkedList<Promo>();
        for (Promo promoToInclude : promos) {
	        if(promo.equals(promoToInclude)){
		        break;
	        }
            subList.add(promoToInclude);
        }
        return subList;
    }

    private void findAndRemove(Promo promo, Promo excludedPromo, Collection<PromoExclusion> promoExclusions) {
        PromoExclusion promoExclusionToRemove = null;
        for (PromoExclusion promoExclusion : promoExclusions) {
            if (promoExclusion.getPromo().equals(promo) && promoExclusion.getExcludedPromo().equals(excludedPromo)) {
                promoExclusionToRemove = promoExclusion;
                break;
            }
        }
        promoExclusions.remove(promoExclusionToRemove);
    }


    private Set<Promo> removePromoExclusions(Promo promo, Collection<PromoExclusion> promoExclusions) {
        Set<Promo> excludedPromos = new TreeSet<Promo>();
        List<PromoExclusion> promoExclusionListToRemove = new LinkedList<PromoExclusion>();
        for (PromoExclusion promoExclusion : promoExclusions) {
            if (promoExclusion.getPromo().equals(promo)) {
                excludedPromos.add(promoExclusion.getExcludedPromo());
                promoExclusionListToRemove.add(promoExclusion);
            }
        }
        promoExclusions.removeAll(promoExclusionListToRemove);
        return excludedPromos;
    }


    private Set<Promo> retrieveExcludedPromos(Promo promo, Collection<PromoExclusion> pExclusionsCreatedByPromo) {
        Set<Promo> excludedPromos = new TreeSet<Promo>();
        for (PromoExclusion promoExclusion : pExclusionsCreatedByPromo) {
            excludedPromos.add(promoExclusion.getExcludedPromo());
        }
        return excludedPromos;
    }

    private List<PromoExclusion> findExclusionsCreatedByPromo(Promo promoCreatedExclusions, Collection<PromoExclusion> promoExclusions) {
        List<PromoExclusion> exclusionsCreatedByPromo = new LinkedList<PromoExclusion>();
        for (PromoExclusion promoExclusion : promoExclusions) {
            if (promoExclusion.getPromo().equals(promoCreatedExclusions)) {
                exclusionsCreatedByPromo.add(promoExclusion);
            }
        }
        return exclusionsCreatedByPromo;
    }

    private boolean checkExclusions(Promo promoToCheck, Collection<PromoExclusion> promoExclusions) {
        for (PromoExclusion promoExclusion : promoExclusions) {
            if (promoExclusion.getExcludedPromo().equals(promoToCheck)) {
                return true;
            }
        }
        return false;
    }

}
