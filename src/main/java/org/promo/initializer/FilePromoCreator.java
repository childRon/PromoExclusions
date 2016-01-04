package org.promo.initializer;

import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;
import org.promo.utils.PromoUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Reads information about promo and exclusions from file and creates appropriate structures.
 * Object is not thread-safe.
 */
public class FilePromoCreator implements PromoCreator {
    private String PROMO_FILE = "promo.txt";
    private String PROMO_EXCL_FILE = "excl3.txt";

    public FilePromoCreator defineFilePaths(String promoFile, String exclusionsFile) {
        this.PROMO_FILE = promoFile;
        this.PROMO_EXCL_FILE = exclusionsFile;
        return this;
    }

    @Override
    public List<PromoExclusion> initializeExclusion(List<Promo> promos) {
        TreeMap<String, Promo> promoTreeMap = PromoUtils.convertToMap(promos);
        List<PromoExclusion> promoExclusionList = new LinkedList<PromoExclusion>();
        try {
            BufferedReader bufferedPromoReader = new BufferedReader(new FileReader(PROMO_EXCL_FILE));
            String promoExclusion;
            while ((promoExclusion = bufferedPromoReader.readLine()) != null) {
                String[] promoParts = promoExclusion.split(">");
                if (promoParts != null && promoParts.length == 2) {
                    String promoName = promoParts[0];
                    String promoExcludedName = promoParts[1];
                    Promo promo = promoTreeMap.get(promoName);
                    Promo promoExcluded = promoTreeMap.get(promoExcludedName);
                    if (promo != null && promoExcluded != null) {
                        PromoExclusion promoExclusionObj = new PromoExclusion(promo, promoExcluded);
                        promoExclusionList.add(promoExclusionObj);
                    } else {
                        System.err.println(String.format("No promo exists with names: %s, %s", promoName, promoExcludedName));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return promoExclusionList;
    }

	public List<Promo> initializePromos(String promoFilePath) {
		List<Promo> promoList = new LinkedList<Promo>();
		try {
			BufferedReader bufferedPromoReader = new BufferedReader(new FileReader(promoFilePath));
			String promo;
			int counter = 1;
			while ((promo = bufferedPromoReader.readLine()) != null ) {
				if(promo.trim().length() == 0){
					continue;
				}
				String name = null;
				int priority = 0;
				String[] promoParts = promo.split("-");
				if (promoParts != null && promoParts.length == 2) {
					name = promoParts[0];
					priority = Integer.parseInt(promoParts[1]);
					counter = priority;
				} else if (promo.trim().length() != 0) {
					name = promo;
					priority = counter;
				}
				counter++;
				Promo promoObj = new Promo(name.trim(), priority);
				promoList.add(promoObj);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}
		return promoList;

	}

    @Override
    public List<Promo> initializePromos() {
	    return initializePromos(PROMO_FILE);
    }
}
