package org.promo;

import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;
import org.promo.initializer.FilePromoCreator;
import org.promo.initializer.PromoCreator;
import org.promo.initializer.PromoCreatorFactory;
import org.promo.utils.PromoUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;


public class PromoDemoExecutor {

    private static String promoFile = "promo.txt";
    private static String exclusionsFile = "promoExclusions.txt";
    private static String promoResultFile = "promoResult.txt";

    static{
        ResourceBundle resourceBundle = ResourceBundle.getBundle("settings");
        String promoFilePath = resourceBundle.getString("promoFile");
        String promoResultFilePath = resourceBundle.getString("promoResultFile");
        String promoExclusionsPath = resourceBundle.getString("promoExclusionsFile");
        if(promoFilePath != null){
            promoFile = promoFilePath;
        }
        if(promoResultFilePath != null){
            promoResultFile = promoResultFilePath;
        }
        if(promoExclusionsPath != null){
            exclusionsFile = promoExclusionsPath;
        }
    }

    public static void main(String[] args) throws IOException {

        PromoExclusionResolver promoExclusionResolver = new PromoExclusionResolver();

        FilePromoCreator filePromoCreator = PromoUtils.initializer(promoFile, exclusionsFile);

        List<Promo> promos = filePromoCreator.initializePromos();
        List<PromoExclusion> promoExclusions = filePromoCreator.initializeExclusion(promos);
        Set<Promo> calculatedPromos =  promoExclusionResolver.resolvePromos(promos, promoExclusions);

        output(promos, promoExclusions, calculatedPromos);

    }

    private static void output(List<Promo> promos, List<PromoExclusion> promoExclusions, Set<Promo> calculatedPromos) throws IOException {
        System.out.println("promos = " + promos);
        System.out.println("promoExclusions = " + promoExclusions);
        System.out.println("calculatedPromos = " + calculatedPromos);
        BufferedWriter bufferedWriter = null;
        try{
            FileWriter fileWriter = new FileWriter(promoResultFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            for (Promo calculatedPromo : calculatedPromos) {
                bufferedWriter.write(calculatedPromo.toString());
                bufferedWriter.newLine();
            }

        }finally {
             if(bufferedWriter != null){
                 bufferedWriter.close();
             }
        }

    }


}
