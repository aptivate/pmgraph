package org.aptivate.pmGraph.test;

import junit.framework.*;

public class AllTests extends TestCase
{
    public AllTests( String s )
    {
        super( s );
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite("pmGraph Unit Tests");

        suite.addTest(DataBaseTest.suite());
                
        return suite;
    }
}
