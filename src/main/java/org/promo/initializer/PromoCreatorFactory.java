package org.promo.initializer;

/**
 * Created with IntelliJ IDEA.
 * User: Alena Misan
 * Date: 20.12.15
 * Time: 12:21
 * To change this template use File | Settings | File Templates.
 */
public class PromoCreatorFactory {

    public static enum TYPE {MANUAL, AUTO};

    public synchronized static PromoCreator getCreator(TYPE pType ){
        PromoCreator promoCreator = null;
        switch (pType){
            case AUTO: promoCreator = new FilePromoCreator(); break;
            case MANUAL: break;
            default: promoCreator = new FilePromoCreator();
        }
        return  promoCreator;
    }


}
