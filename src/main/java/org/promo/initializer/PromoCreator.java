package org.promo.initializer;

import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alena Misan
 * Date: 20.12.15
 * Time: 11:49
 * To change this template use File | Settings | File Templates.
 */
public interface PromoCreator {
    public abstract List<PromoExclusion> initializeExclusion(List<Promo> promos);
    public abstract List<Promo> initializePromos();


}
