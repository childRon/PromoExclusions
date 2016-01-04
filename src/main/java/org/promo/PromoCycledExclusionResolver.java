package org.promo;

import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;

import java.util.*;

public class PromoCycledExclusionResolver extends PromoExclusionResolver {
    private List<PromoExclusion> removedPromoExclusions = new ArrayList<PromoExclusion>();
    private Set<Promo> excludedPromos;

    public Set<Promo> resolvePromos(List<Promo> promos, List<PromoExclusion> promoExclusions) {
        Set<List<Promo>> exclusionPaths = new HashSet<List<Promo>>();
        fillExclusionPaths(promos, promoExclusions, exclusionPaths);
        return processExclusion(promos, exclusionPaths);
    }

    // Получение набора всех путей вытеснения
    private void fillExclusionPaths(List<Promo> promos, List<PromoExclusion> promoExclusions, Set<List<Promo>> exclusionPaths) {
        Set<Promo> rootElements = getRootElements(promos, promoExclusions);
        Map<String, List<Promo>> cycledPaths = new HashMap<String, List<Promo>>();
        for (Promo nextPromo : rootElements) {
            List<Promo> nextPathValue = new ArrayList<Promo>();
            nextPathValue.add(nextPromo);
            exclusionPaths.add(nextPathValue);
            fillExclusionPathsRecursive(nextPromo, nextPathValue, promoExclusions, exclusionPaths, cycledPaths);
        }
        SortedSet<Promo> promoInCycleByPriority = new TreeSet<Promo>();
        promoInCycleByPriority.addAll(getNotInHierarchyPromos(promos, exclusionPaths, promoExclusions));
        if(!cycledPaths.isEmpty()) {
            for(Map.Entry<String, List<Promo>> entry: cycledPaths.entrySet()) {
                promoInCycleByPriority.addAll(entry.getValue());
            }
        }
        if(!promoInCycleByPriority.isEmpty()) {
            removeMinorPromoExclusion(promoInCycleByPriority, promoExclusions);
            exclusionPaths.clear();
            fillExclusionPaths(promos, promoExclusions, exclusionPaths);
        }
    }

    // Поиск корневых элементов иерархии вытеснения
    private Set<Promo> getRootElements(List<Promo> promos, List<PromoExclusion> promoExclusions) {
        HashSet<Promo> rootElements = new HashSet<Promo>();
        for (Promo promo : promos) {
            boolean isInPromo = false;
            boolean isInExcludedPromo = false;
            for (PromoExclusion promoExclusion : promoExclusions) {
                if (!isInPromo) {
                    isInPromo = promo.equals(promoExclusion.getPromo());
                }
                if (!isInExcludedPromo) {
                    isInExcludedPromo = promo.equals(promoExclusion.getExcludedPromo());
                }
            }
            if (isInPromo && !isInExcludedPromo) {
                rootElements.add(promo);
            }
        }
        return rootElements;
    }

    // Получение набора всех путей вытеснения а также всех путей вытеснения с зацикливаниями
    private void fillExclusionPathsRecursive(Promo currentPromo, List<Promo> currentPathValue,
                                             List<PromoExclusion> promoExclusions, Set<List<Promo>> exclusionPaths,
                                             Map<String, List<Promo>> cycledPaths) {
        boolean hasNext = false;
        for (PromoExclusion promoExclusion : promoExclusions) {
            if (currentPromo.equals(promoExclusion.getPromo())) {
                hasNext = true;
                Promo nextPromo = promoExclusion.getExcludedPromo();
                List<Promo> nextPathValue = new ArrayList<Promo>(currentPathValue);
                nextPathValue.add(nextPromo);
                exclusionPaths.add(nextPathValue);
                if (currentPathValue.contains(nextPromo)) {
                    List<Promo> promos = currentPathValue.subList(currentPathValue.indexOf(nextPromo), currentPathValue.size());
                    cycledPaths.put(new TreeSet<Promo>(promos).toString(), promos);
                }
                else {
                    fillExclusionPathsRecursive(nextPromo, nextPathValue, promoExclusions, exclusionPaths, cycledPaths);
                }
            }
        }
        if (hasNext) {
            exclusionPaths.remove(currentPathValue);
        }
    }

    // Получение промоушенов не участвующих в идентифицированной на текущий момент иерархии вытеснения
    // т.е. находящихся в отдельных кольцах и возможных перемычках между ними
    private Set<Promo> getNotInHierarchyPromos(List<Promo> promos, Set<List<Promo>> exclusionPaths,
                                               List<PromoExclusion> promoExclusions) {
        Set<Promo> notInHierarchyPromos = new HashSet<Promo>(promos);
        for (List<Promo> exclusionPath: exclusionPaths) {
            notInHierarchyPromos.removeAll(exclusionPath);
        }
        // Также не нужны промоушены которые никого не вытесняют
        Set<Promo> promosFromExclusion = new HashSet<Promo>();
        for(PromoExclusion promoExclusion: promoExclusions) {
            promosFromExclusion.add(promoExclusion.getPromo());
        }
        Set<Promo> separateFromExclusionPromos = new HashSet<Promo>(promos);
        separateFromExclusionPromos.removeAll(promosFromExclusion);
        notInHierarchyPromos.removeAll(separateFromExclusionPromos);
        return notInHierarchyPromos;
    }

    // Удаление из кольца наименее приоритетного вытеснения, которое определяется по следующей логике: промоушен с наименьшим приоритетом
    // из набора промоушенов, участвующих в закольцовываниях, вытесняет промоушен с наибольшим приоритетом
    // из всех им вытесняемых, при том, что удаляемое вытеснение должно быть звеном кольца
    private void removeMinorPromoExclusion(SortedSet<Promo> promoInCycleByPriority, List<PromoExclusion> promoExclusions) {
        // Цикл потому, что промоушен может оказаться не узлом кольца а узлом перемычки между кольцами
        // либо вытесняться из кольца
        for(Promo promoInCycle: promoInCycleByPriority) {
            NavigableSet<Promo> excludedPromos = new TreeSet<Promo>();
            for(PromoExclusion promoExclusion: promoExclusions) {
                if(promoInCycle.equals(promoExclusion.getPromo()) && promoInCycleByPriority.contains(promoExclusion.getExcludedPromo())) {
                    excludedPromos.add(promoExclusion.getExcludedPromo());
                }
            }
            int promoExclusionsSize = promoExclusions.size();
            for(Promo excludedPromo: excludedPromos.descendingSet()) {
                List<Promo> excludedPromoPath = new ArrayList<Promo>();
                excludedPromoPath.add(promoInCycle);
                excludedPromoPath.add(excludedPromo);
                removeCyclePromoExclusion(excludedPromo, promoExclusions, excludedPromoPath);
                if(promoExclusionsSize > promoExclusions.size()) {
                    getRemovedPromoExclusions().add(new PromoExclusion(promoInCycle, excludedPromo));
                    return;
                }
            }
        }
    }

    // Разрыв кольца через удаление вытеснения в случае если это вытеснение является звеном кольца
    private void removeCyclePromoExclusion(Promo excludedPromo, List<PromoExclusion> promoExclusions,
                                           List<Promo> excludedPromoPath) {
        for(PromoExclusion promoExclusion: new ArrayList<PromoExclusion>(promoExclusions)) {
            if(excludedPromoPath.get(excludedPromoPath.size()-1).equals(promoExclusion.getPromo())) {
                Promo nextPromo = promoExclusion.getExcludedPromo();
                if(excludedPromoPath.contains(nextPromo)) {
                    if(excludedPromoPath.get(0).equals(nextPromo)) {
                        promoExclusions.remove(new PromoExclusion(excludedPromoPath.get(0), excludedPromo));
                    }
                    return;
                }
                else {
                    List<Promo> nextPromoPath = new ArrayList<Promo>(excludedPromoPath);
                    nextPromoPath.add(nextPromo);
                    removeCyclePromoExclusion(excludedPromo, promoExclusions, nextPromoPath);
                }
            }
        }
    }

    // Определение применяемых промоушенов путем вычитания из общего набора промоушенов тех, которые вытесняются
    private Set<Promo> processExclusion(List<Promo> promos, Set<List<Promo>> exclusionPaths) {
        Set<Promo> excludedPromos = new TreeSet<Promo>();
        int i = 0;
        boolean isInProcess = true;
        while (isInProcess) {
            isInProcess = false;
            for (List<Promo> exclusionPath: exclusionPaths) {
                if (exclusionPath.size() > i + 1) {
                    isInProcess = true;
                    if (!excludedPromos.contains(exclusionPath.get(i))) {
                        excludedPromos.add(exclusionPath.get(i + 1));
                    }
                }
            }
            i++;
        }
        setExcludedPromos(excludedPromos);
        Set<Promo> calculatedPromos = new TreeSet<Promo>(promos);
        calculatedPromos.removeAll(excludedPromos);
        return calculatedPromos;
    }

    public List<PromoExclusion> getRemovedPromoExclusions() {
        return removedPromoExclusions;
    }

    public void setExcludedPromos(Set<Promo> excludedPromos) {
        this.excludedPromos = excludedPromos;
    }

    public Set<Promo> getExcludedPromos() {
        return excludedPromos;
    }
}
