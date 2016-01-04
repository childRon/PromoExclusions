package org.promo;

import org.junit.Before;
import org.junit.Test;

/**
 * Test suits for checking exclusions examples with cycles and duplicates of priority
 *
 * @author: Andrei_Misan
 */
public class PromoCycledExclusionResolverTest extends PromoExclusionResolverTest {

	@Before
	public void setUp(){
		setExclusionResolver(new PromoCycledExclusionResolver());
	}

	@Test
	public void testCaseWithCycle3(){
		String prefixTest = "testCases/testCaseWithCycle3/";
		runTestCase(prefixTest);
	}

	@Test
	public void testCaseWithCycle6(){
		String prefixTest = "testCases/testCaseWithCycle6/";
		runTestCase(prefixTest);
	}
}
