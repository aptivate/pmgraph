package org.aptivate.pmGraph.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{
    public AllTests( String s )
    {
        super( s );
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("pmGraph Unit Tests");

        suite.addTest(DataBaseTest2.suite());
        suite.addTest(ButtonsTest.suite());
        suite.addTest(GraphsTest.suite());
        suite.addTest(LegendTest.suite());
        suite.addTest(TimeFormTest.suite());
        suite.addTest(W3cValidationTest.suite());
                
        return suite;
    }
}
