package org.promo;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.promo.entity.Promo;
import org.promo.entity.PromoExclusion;
import org.promo.initializer.FilePromoCreator;
import org.promo.utils.PromoUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Test suits for checking exclusions examples without cycles and duplicates of priority
 *
 * @author: Andrei_Misan
 */
public class PromoExclusionResolverTest{

	private static String promoFile = "testPromo.txt";
	private static String exclusionsFile = "testPromoExclusions.txt";
	private static String expectedPromoFile = "expectedPromoResult.txt";

	@Before
	public void setUp(){
		   setExclusionResolver(new PromoExclusionResolver());
	}

	static{
		ResourceBundle resourceBundle = ResourceBundle.getBundle("testSettings");
		String promoFilePath = resourceBundle.getString("promoFile");
		String promoExpectedResultFile = resourceBundle.getString("promoExpectedResultFile");
		String promoExclusionsPath = resourceBundle.getString("promoExclusionsFile");
		if(promoFilePath != null){
			promoFile = promoFilePath;
		}
		if(promoExpectedResultFile != null){
			expectedPromoFile = promoExpectedResultFile;
		}
		if(promoExclusionsPath != null){
			exclusionsFile = promoExclusionsPath;
		}
	}

	private PromoExclusionResolver mExclusionResolver;

	public PromoExclusionResolver getExclusionResolver() {
		return mExclusionResolver;
	}

	public void setExclusionResolver(PromoExclusionResolver pExclusionResolver) {
		mExclusionResolver = pExclusionResolver;
	}

	@Test
	public void testCase1(){
		String prefixTest = "testCases/testCase1/";
		runTestCase(prefixTest);
	}

	@Test
	public void testCase4(){
		String prefixTest = "testCases/testCase4/";
		runTestCase(prefixTest);
	}

	@Test
	@Ignore
	public void testCaseWithCycle3(){
		String prefixTest = "testCases/testCaseWithCycle3/";
		runTestCase(prefixTest);
	}

	@Test
	public void testCase2(){
		String prefixTest = "testCases/testCase2/";
		runTestCase(prefixTest);
	}

	@Test
	public void testCase5(){
		String prefixTest = "testCases/testCase5/";
		runTestCase(prefixTest);
	}

	protected void runTestCase(String pPrefixTest) {
		String testPromoFile = pPrefixTest + promoFile;
		String testExclFile = pPrefixTest + exclusionsFile;

		FilePromoCreator filePromoCreator = PromoUtils.initializer(testPromoFile, testExclFile);

		List<Promo> expectedPromos = filePromoCreator.initializePromos(pPrefixTest + expectedPromoFile);
		List<Promo> promos = filePromoCreator.initializePromos();
		List<PromoExclusion> promoExclusions = filePromoCreator.initializeExclusion(promos);
		Set<Promo> calculatedPromos =  getExclusionResolver().resolvePromos(promos, promoExclusions);
		assertThat( new ArrayList<Promo>(calculatedPromos), equalTo( new ArrayList<Promo>(expectedPromos)));
	}


}
