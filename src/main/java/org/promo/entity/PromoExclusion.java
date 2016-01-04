package org.promo.entity;

/**
 * {@link PromoExclusion} defines that {@link Promo promo} has directional exclusion with {@link Promo excludedPromo}.
 *
 * */
public class PromoExclusion {
    private Promo promo;
    private Promo excludedPromo;

    public PromoExclusion(Promo promo, Promo excludedPromo) {
        this.promo = promo;
        this.excludedPromo = excludedPromo;
    }

    public Promo getPromo() {
        return promo;
    }


    public Promo getExcludedPromo() {
        return excludedPromo;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromoExclusion)) return false;

        PromoExclusion that = (PromoExclusion) o;

        if (!excludedPromo.equals(that.excludedPromo)) return false;
        if (!promo.equals(that.promo)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = promo.hashCode();
        result = 31 * result + excludedPromo.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s -> %s", promo, excludedPromo);
    }
}
