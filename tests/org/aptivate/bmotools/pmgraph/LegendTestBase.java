package org.aptivate.bmotools.pmgraph;

import java.io.IOException;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.meterware.httpunit.WebTable;

/**
 * Abstract Class which contains the method which checks if the values in the
 * legend table contains the data specified
 * 
 * @author Noe A. Rodriguez Gonzalez.
 * 
 */
abstract class LegendTestBase extends TestCase
{
	protected TestUtils m_testUtils;

	public LegendTestBase() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException,
			IOException
	{

		m_testUtils = new TestUtils();
	}

	/**
	 * Just check if the donwloaded and uploaded values of a legend tabele match
	 * the values specified by the parameters.
	 * 
	 * @param table
	 * @param downloaded
	 * @param uploaded
	 * @param rows
	 * @throws IOException
	 * @throws SAXException
	 */
	protected void checkUploadDownloadLegendTable(WebTable table,
			long downloaded[], long uploaded[], String rows[], View view)
			throws IOException, SAXException
	{

		// Check the table data
		for (int i = 2; i < table.getRowCount(); i++) // the data in the table
														// starts in row 2
		{
			assertEquals("Check the IP Or Port Address", rows[i - 2], table
					.getCellAsText(i, 1));
			switch (view)
			{
			default:
			case LOCAL_IP:
				// Columns in the table are Color, Host IP, Host Name,
				// Downloaded, Uploaded
				assertEquals("Check the Downloaded Value", String
						.valueOf(downloaded[i - 2]), table.getCellAsText(i, 3));
				assertEquals("Check the Uploaded Value", String
						.valueOf(uploaded[i - 2]), table.getCellAsText(i, 4));
				break;
			case LOCAL_PORT:
				// Columns in the table are Color, IP, Downloaded, Uploaded
				assertEquals("Check the Downloaded Value", String
						.valueOf(downloaded[i - 2]), table.getCellAsText(i, 2));
				assertEquals("Check the Uploaded Value", String
						.valueOf(uploaded[i - 2]), table.getCellAsText(i, 3));
				break;
			}
		}
	}

}
