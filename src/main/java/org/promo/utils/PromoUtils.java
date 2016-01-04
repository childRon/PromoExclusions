package org.promo.utils;

import org.promo.entity.Promo;
import org.promo.initializer.FilePromoCreator;
import org.promo.initializer.PromoCreator;
import org.promo.initializer.PromoCreatorFactory;

import java.util.List;
import java.util.TreeMap;

public class PromoUtils {

	public static FilePromoCreator initializer(String promoFile, String exclusionsFile) {
		PromoCreator promoCreator = PromoCreatorFactory.getCreator(PromoCreatorFactory.TYPE.AUTO);
		return ((FilePromoCreator)promoCreator).defineFilePaths(promoFile, exclusionsFile);
	}

   public static TreeMap<String, Promo> convertToMap(List<Promo> promos){
       TreeMap<String, Promo> promoTreeMap = new TreeMap<String, Promo>();
       for (Promo promo : promos) {
            promoTreeMap.put(promo.getName(), promo);
       }
       return promoTreeMap;
   }
}
