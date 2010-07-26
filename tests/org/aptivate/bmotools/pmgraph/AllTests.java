package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests extends PmGraphTestBase
{		
	
	public AllTests() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException
	{		
		super();		
	}		

	public static Test suite() throws IOException
	{		
		TestSuite suite = new TestSuite("pmGraph Unit Tests");
       	suite.addTest(DataBaseTest.suite());
		suite.addTest(ButtonsTest.suite());
		suite.addTest(GraphsTest.suite());
		suite.addTest(LegendTest.suite());
		suite.addTest(StartAndEndEntryTest.suite());
		suite.addTest(TimeFormEntryTest.suite());
		suite.addTest(ResultEntryTest.suite());
		suite.addTest(IPandPortEntryTest.suite());
		suite.addTest(LegendTestPortView.suite());
		suite.addTest(SpecificGraphsTest.suite());		
		suite.addTest(ErrorMessageTest.suite());
		suite.addTest(QueryBuilderTest.suite());
		suite.addTest(W3cValidationTest.suite());
		suite.addTest(RequestParamsTest.suite());
		suite.addTest(PortsToServiceTest.suite());
		suite.addTest(ColourTest.suite());
		suite.addTest(ConfigurationTest.suite());
		suite.addTest(TestMultiSubnetsLegend.suite());
		suite.addTest(TestMultiSubnets.suite());
		suite.addTest(TestGroups.suite());
		suite.addTest(UrlBuilderTest.suite());
		return suite;
	}	
}
