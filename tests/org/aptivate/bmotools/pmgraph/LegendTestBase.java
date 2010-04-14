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

	public LegendTestBase() throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, SQLException, IOException {

		m_testUtils = new TestUtils();
	}

	/**
	 * Just check if the donwloaded and uploaded values of a legend table match
	 * the values specified by the parameters.
	 * 
	 * @param table
	 * @param downloaded
	 * @param uploaded
	 * @param rows
	 * @param view
	 * @throws IOException
	 * @throws SAXException
	 */
	protected void checkUploadDownloadLegendTable(WebTable table, long downloaded[],
			long uploaded[], String rows[], View view) throws IOException, SAXException
	{
		// Check the table data
		// The data starts in the second row because the first one is for the colours
		int j = 2;	
		int i = 0;
		while (j < table.getRowCount())	{	
			if (table.getCellAsText(j,1).equals(""))
				j++;
			assertEquals("Check the IP Or Port Address", rows[i], table.getCellAsText(j, 1));
			switch (view)
			{
			default:
			case LOCAL_IP:
				// Columns in the table are Colour, Host IP, Host Name,
				// Downloaded, Uploaded, avg downloaded, avg uploaded
				assertEquals("Check the Downloaded Value", String.valueOf(downloaded[i]), 
						table.getCellAsText(j, 3));
				assertEquals("Check the Uploaded Value", String.valueOf(uploaded[i]), 
						table.getCellAsText(j, 4));
				break;
			case LOCAL_PORT:
				// Columns in the table are Colour, port, protocol, service,
				// Downloaded, Uploaded, average downloaded, average uploaded
				assertEquals("Check the Downloaded Value", String.valueOf(downloaded[i]), 
						table.getCellAsText(j, 2));
				assertEquals("Check the Uploaded Value", String.valueOf(uploaded[i]), 
						table.getCellAsText(j, 3));
				break;
			}
			i++;
			j++;
		}
	}
}
