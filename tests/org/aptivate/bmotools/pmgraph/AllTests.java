package org.aptivate.bmotools.pmgraph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
	public AllTests(String s)
	{
		super(s);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite("pmGraph Unit Tests");

		suite.addTest(DataBaseTest.suite());
		suite.addTest(ButtonsTest.suite());
		suite.addTest(GraphsTest.suite());
		suite.addTest(LegendTest.suite());
		suite.addTest(TimeFormTest.suite());
		suite.addTest(LegendTestPortView.suite());
		suite.addTest(SpecificGraphsTest.suite());
		suite.addTest(W3cValidationTest.suite());
		suite.addTest(ResultEntryTest.suite());
		return suite;
	}
}
