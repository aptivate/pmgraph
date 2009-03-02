package org.aptivate.pmGraph.test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.aptivate.bmotools.pmgraph.GraphFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.Layer;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HTMLElement;
import com.meterware.httpunit.HttpInternalErrorException;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebForm;
import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.WebTable;

import org.aptivate.bmotools.pmgraph.DataAccess;

public class DataBaseTest extends TestCase {
	// The connection to the MySQL database
	private Connection conn;

	long l = (System.currentTimeMillis() / 60000);

	static final Timestamp t1 = new Timestamp(60000);

	static final Timestamp t2 = new Timestamp(120000);

	static final Timestamp t3 = new Timestamp(180000);

	static final Timestamp t4 = new Timestamp(240000);

	private static final String TABLE_NAME = "acct_v6";

	// MySQL table fields
	private static final String IP_SRC = "ip_src";

	private static final String IP_DEST = "ip_dst";

	private static final String BYTES = "bytes";

	private static final String TIME = "stamp_inserted";

	String m_urlPmgraph;

	// SQL query strings
	private static final String SELECT_DATABASE = "USE test;";

	/*
	 * private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
	 * "(" + BYTES + " bigint(20) unsigned NOT NULL, " + TIME + " datetime NOT
	 * NULL, " + IP_SRC + " char(15) NOT NULL, " + IP_DEST + " char(15) NOT
	 * NULL, " + "src_port int(2) unsigned NOT NULL, " + "dst_port int(2)
	 * unsigned NOT NULL" + ");";
	 */
	private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ "(" + BYTES + " bigint NOT NULL, " + TIME
			+ " timestamp NOT NULL, " + IP_SRC + " char(15) NOT NULL, "
			+ IP_DEST + " char(15) NOT NULL, " + "src_port int NOT NULL, "
			+ "dst_port int NOT NULL" + ");";

	private static final String INSERT_DATA = "INSERT INTO " + TABLE_NAME + "("
			+ BYTES + "," + TIME + "," + IP_SRC + "," + IP_DEST
			+ ",src_port, dst_port" + ") VALUES (?,?,?,?,?,?);";

	private static final String GET_TABLE_DATA = "SELECT * FROM " + TABLE_NAME
			+ ";";

	private static final String DELETE_TABLE = "DROP TABLE " + TABLE_NAME + ";";

	public DataBaseTest(String s) {
		super(s);
	}

	public void setUp() throws Exception {
		// Get port number from properties file
		Properties properties = new Properties();
		InputStream stream = DataBaseTest.class
				.getResourceAsStream("/tests.properties");
		properties.load(stream);
		stream.close();
		String portNumber = properties.getProperty("Port");
		m_urlPmgraph = "http://localhost:" + portNumber + "/pmgraph/";
		// Class.forName("org.postgresql.Driver");
		String sDriver = properties.getProperty("JdbcDriver");
		String sURL = properties.getProperty("DatabaseURL");
		String sUsername = properties.getProperty("DatabaseUser");
		String sPassword = properties.getProperty("DatabasePass");
		stream.close();
		
		try {

			// Load the JDBC driver
			Class.forName(sDriver).newInstance();
			// Connect to a data source
			conn = DriverManager.getConnection(sURL, sUsername, sPassword);
		} catch (SQLException se) {
			System.out.println("Couldn't connect: print out a stack trace.");
			se.printStackTrace();
			System.exit(1);
		}

	}

	private void CreateTable() throws SQLException {
		// Allow the program to be run more than once,
		// attempt to remove the table from the database
		try {
			// Delete the table
			PreparedStatement pstmt = conn.prepareStatement(DELETE_TABLE);
			System.out.println(pstmt);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			/* don't care if it fails, table may not exist */
			System.out.println(e.getMessage());
		}

		try {
			// Select Database
			PreparedStatement pstmt = conn.prepareStatement(SELECT_DATABASE);
			// System.out.println(pstmt);
			// pstmt.executeUpdate();

			// Create a table
			pstmt = conn.prepareStatement(CREATE_TABLE);
			System.out.println(pstmt);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw (e);
		}
	}

	public void tearDown() {

	}

	/* This test tests the SetTime form */
	public void testCheckSetTimeForm() throws Exception {
		CreateTable();

		// Insert rows into table
		InsertSampleData();

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=0&end=300000");
		WebResponse response = wc.getResponse(request);

		WebForm theForm = response.getFormWithID("SetDateAndTime");

		assertNotNull("Check if there is form SetDateAndTime.", theForm);
		assertNotNull("Check if there is button Go.", theForm
				.getButtonWithID("Go"));
		assertNotNull("Check if there is text box fromDate.", response
				.getElementWithID("fromDate"));
		assertNotNull("Check if there is text box toDate.", response
				.getElementWithID("toDate"));
		assertNotNull("Check if there is text box fromTime.", response
				.getElementWithID("fromTime"));
		assertNotNull("Check if there is text box toTime.", response
				.getElementWithID("toTime"));
	}

	/* This test tests the next button */
	public void testCheckNextButton() throws Exception {
		WebConversation wc;
		WebRequest request, requestImg;
		WebResponse response, responseImg;
		String nextURL;
		WebLink link;
		WebImage webImg;
		URL urlObj = new URL(m_urlPmgraph);
		InputStream inputStr;
		BufferedImage theActualImg, theExpectedImg;

		// Create a table and insert rows into it
		CreateTable();
		InsertSampleData();

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=75000&end=225000");
		response = wc.getResponse(request);

		/* Test the Next Button First Press */
		// nextStart = start + (end-start)/2 = 75000 + (225000 - 75000)/2 =
		// 150000
		// nextEnd = end + (end-start)/2 = 225000 + (225000-75000)/2 = 300000
		nextURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=150000&end=300000";

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

		// Load the page after press the Next Button
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=150000&end=300000");
		response = wc.getResponse(request);

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Get the image from the graph
		 * page webImg = response.getImageWithAltText("Bandwith Graph"); urlObj =
		 * new URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphFirstNext.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ---------------------------------------
		 */

		/* ------------------------------------ */

		/* Test the Next Button Second Press */
		// nextStart = start + (end-start)/2 = 150000 + (300000 - 150000)/2 =
		// 225000
		// nextEnd = end + (end-start)/2 = 300000 + (300000-150000)/2 = 375000
		nextURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=225000&end=375000";

		// Find the "next" link
		link = response.getLinkWithName("next");
		assertEquals("Compare the next link.", nextURL, link.getURLString());

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Load the page after press the
		 * Next Button request = new GetMethodWebRequest(m_urlPmgraph +
		 * "?start=225000&end=375000"); response = wc.getResponse(request);
		 *  // Get the image from the graph page webImg =
		 * response.getImageWithAltText("Bandwith Graph"); urlObj = new
		 * URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphSecondNext.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ----------------------------------------
		 */
	}

	/* This test tests the prev button */
	public void testCheckPrevButton() throws Exception {
		WebConversation wc;
		WebRequest request, requestImg;
		WebResponse response, responseImg;
		String prevURL;
		WebLink link;
		WebImage webImg;
		URL urlObj = new URL(m_urlPmgraph);
		InputStream inputStr;
		BufferedImage theActualImg, theExpectedImg;

		// Create a table and insert rows into it
		CreateTable();
		InsertSampleData();

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=150000&end=450000");
		response = wc.getResponse(request);

		/* Test the Prev Button First Press */
		// prevStart = start - (end-start)/2 = 150000 - (450000 - 150000)/2 = 0
		// prevEnd = end - (end-start)/2 = 450000 - (450000-150000)/2 = 300000
		prevURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=0&end=300000";

		// Find the "prev" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());

		// Load the page after press the Prev Button
		request = new GetMethodWebRequest(m_urlPmgraph + "?start=0&end=300000");
		response = wc.getResponse(request);

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Get the image from the graph
		 * page webImg = response.getImageWithAltText("Bandwith Graph"); urlObj =
		 * new URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphFirstPrev.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ---------------------------------------
		 */

		/* ------------------------------------ */

		/* Test the Prev Button Second Press */
		// prevStart = start - (end-start)/2 = 0 - (300000 - 0)/2 = -150000
		// prevEnd = end - (end-start)/2 = 300000 - (300000 - 0)/2 = 150000
		prevURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=150000";

		// Find the "next" link
		link = response.getLinkWithName("prev");
		assertEquals("Compare the prev link.", prevURL, link.getURLString());

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Load the page after press the
		 * Next Button request = new GetMethodWebRequest(m_urlPmgraph +
		 * "?start=-150000&end=150000"); response = wc.getResponse(request);
		 *  // Get the image from the graph page webImg =
		 * response.getImageWithAltText("Bandwith Graph"); urlObj = new
		 * URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphSecondPrev.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ---------------------------------
		 */
	}

	/* This test tests the zoom- button */
	public void testCheckZoomOutButton() throws Exception {
		WebConversation wc;
		WebRequest request, requestImg;
		WebResponse response, responseImg;
		String zoomURL;
		WebLink link;
		WebImage webImg;
		URL urlObj = new URL(m_urlPmgraph);
		InputStream inputStr;
		BufferedImage theActualImg, theExpectedImg;

		// Create a table and insert rows into it
		CreateTable();
		InsertSampleData();

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph + "?start=0&end=300000");
		response = wc.getResponse(request);

		/* Test the Zoom- Button First Press */
		// zoomOutStart = start - (endTime - startTime) / 2 = 0 - (300000 - 0)/2
		// = -150000
		// zoomOutEnd = end + (endTime - startTime) / 2 = 300000 + (300000 -
		// 0)/2 = 450000
		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=450000";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom- Button
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=-150000&end=450000");
		response = wc.getResponse(request);

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Get the image from the graph
		 * page webImg = response.getImageWithAltText("Bandwith Graph"); urlObj =
		 * new URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphFirstZoom-.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ------------------------------
		 */

		/* ------------------------------------ */

		/* Test the Zoom- Button Second Press */
		// zoomOutStart = start - (endTime - startTime) / 2 = -150000 - (450000
		// - (-150000))/2 = -450000
		// zoomOutEnd = end + (endTime - startTime) / 2 = 450000 + (450000 -
		// (-150000))/2 = 750000
		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-450000&end=750000";

		// Find the Zoom- link
		link = response.getLinkWithName("zoomOut");
		assertEquals("Compare the zoom- link.", zoomURL, link.getURLString());

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Load the page after press the
		 * Zoom- Button request = new GetMethodWebRequest(m_urlPmgraph +
		 * "?start=-450000&end=750000"); response = wc.getResponse(request);
		 *  // Get the image from the graph page webImg =
		 * response.getImageWithAltText("Bandwith Graph"); urlObj = new
		 * URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr); database.properties // Create BufferedImage
		 * from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphSecondZoom-.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ------------------------------------
		 */

		/* ------------------------------------ */

		/* Test if the Zoom- Button Disappear */
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=0&end=2147460000");
		response = wc.getResponse(request);

		// Check thar there isn't the Zoom- link in the page
		link = response.getLinkWithName("zoomOut");
		assertEquals("Check that the zoom- link is null.", null, link);
	}

	/* This test tests the zoom+ button */
	public void testCheckZoomInButton() throws Exception {
		WebConversation wc;
		WebRequest request, requestImg;
		WebResponse response, responseImg;
		String zoomURL;
		WebLink link;
		WebImage webImg;
		URL urlObj = new URL(m_urlPmgraph);
		InputStream inputStr;
		BufferedImage theActualImg, theExpectedImg;

		// Create a table and insert rows into it
		CreateTable();
		InsertSampleData();

		// Create a conversation
		wc = new WebConversation();

		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=-450000&end=750000");
		response = wc.getResponse(request);

		/* Test the Zoom+ Button First Press */
		// zoomInStart = start + (endTime - startTime) / 4 = -450000 + (450000 -
		// (-450000))/4 = -150000
		// zoomInEnd = end - (endTime - startTime) / 4 = 750000 - (750000 -
		// (-450000))/4 = 450000
		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=-150000&end=450000";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_urlPmgraph
				+ "?start=-150000&end=450000");
		response = wc.getResponse(request);

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Get the image from the graph
		 * page webImg = response.getImageWithAltText("Bandwith Graph"); urlObj =
		 * new URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphFirstZoom+.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ----------------------------------------
		 */

		/* ------------------------------------ */

		/* Test the Zoom+ Button Second Press */
		// zoomInStart = start + (endTime - startTime) / 4 = -150000 + (450000 -
		// (-150000))/4 = 0
		// zoomInEnd = end - (endTime - startTime) / 4 = 450000 - (450000 -
		// (-150000))/4 = 300000
		zoomURL = "/pmgraph/index.jsp?report=totals&graph=cumul&start=0&end=300000";

		// Find the Zoom+ link
		link = response.getLinkWithName("zoomIn");
		assertEquals("Compare the zoom+ link.", zoomURL, link.getURLString());

		// Load the page after press the Zoom+ Button
		request = new GetMethodWebRequest(m_urlPmgraph + "?start=0&end=300000");
		response = wc.getResponse(request);

		// The images comparence doesn't work,
		// because of different settings
		/*
		 * ----------------------------------- // Get the image from the graph
		 * page webImg = response.getImageWithAltText("Bandwith Graph"); urlObj =
		 * new URL(urlObj, webImg.getSource());
		 *  // Create new request for the image requestImg = new
		 * GetMethodWebRequest(urlObj.toString()); responseImg =
		 * wc.getResponse(requestImg);
		 *  // Get the input strem of the image inputStr =
		 * responseImg.getInputStream();
		 *  // Create BufferedImage from the input stream theActualImg =
		 * ImageIO.read(inputStr);
		 *  // Create BufferedImage from file theExpectedImg =
		 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphSecondZoom+.png"));
		 * 
		 * CompareImages(theExpectedImg, theActualImg);
		 * ---------------------------------------
		 */

		/* ------------------------------------ */

		/* Test if the Zoom+ Button Disappear */
		// Obtain the upload page on web site
		request = new GetMethodWebRequest(m_urlPmgraph + "?start=0&end=84000");
		response = wc.getResponse(request);

		// Check thar there isn't the Zoom+ link in the page
		link = response.getLinkWithName("zoomIn");
		assertEquals("Check that the zoom- link is null.", null, link);
	}

	/* Compare the pixels of the two images */
	void CompareImages(BufferedImage expectedImg, BufferedImage actualImg) {
		// Compare the height and the width of the images
		assertEquals("Compare the height of the images.", expectedImg
				.getHeight(), actualImg.getHeight());
		assertEquals("Compare the width of the images.",
				expectedImg.getWidth(), actualImg.getWidth());

		for (int y = 0; y < expectedImg.getHeight(); y++) {
			for (int x = 0; x < expectedImg.getWidth(); x++) {
				assertEquals("Compare the image's pixels.", expectedImg.getRGB(
						x, y), actualImg.getRGB(x, y));
			}
		}
	}

	/* This test tests the legend table in the pmGraph page */
	public void testCheckDataTranslationAndRepresentation() throws Exception {
		CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++) {
			// Set the values
			insertNewRow(500000, new Timestamp((l - 5) * 60000), "224.0.0.255",
					"10.0.156.10");
			insertNewRow(500000, new Timestamp((l - 5) * 60000), "10.0.156.10",
					"224.0.0.255");

			insertNewRow(100000, new Timestamp((l - 5) * 60000), "224.0.0.251",
					"10.0.156.1");
			insertNewRow(100000, new Timestamp((l - 5) * 60000), "10.0.156.1",
					"224.0.0.251");

			insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"10.0.156.110", "10.0.156.120");
			insertNewRow(500000, new Timestamp((l - 5) * 60000),
					"10.0.156.120", "10.0.156.110");
		}

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_urlPmgraph);
		WebResponse response = wc.getResponse(request);

		// Get the table data from the page
		WebTable tables[] = response.getTables();

		// Row 2
		String hostIP1 = tables[0].getCellAsText(2, 1);
		String downloaded1 = tables[0].getCellAsText(2, 2);
		String uploaded1 = tables[0].getCellAsText(2, 3);

		// Row 3
		String hostIP2 = tables[0].getCellAsText(3, 1);
		String downloaded2 = tables[0].getCellAsText(3, 2);
		String uploaded2 = tables[0].getCellAsText(3, 3);

		// Check the table data
		// 47 = 500000*100/1024/1024
		// 9 = 100000*100/1024/1024
		assertEquals("Check the IP Address", hostIP1, "10.0.156.10");
		assertEquals("Check the Downloaded Value", downloaded1, "47");
		assertEquals("Check the Downloaded Value", uploaded1, "47");
		assertEquals("Check the IP Address", hostIP2, "10.0.156.1");
		assertEquals("Check the Downloaded Value", downloaded2, "9");
		assertEquals("Check the Downloaded Value", uploaded2, "9");
	}

	/*
	 * This test tests that there is not crash in legend.dsp when working with
	 * large numbers
	 */
	public void testLargeValuesDoNotCrashLegendJsp() throws Exception {
		try {
			CreateTable();

			// Insert rows into table
			for (int i = 0; i < 2; i++) {
				// Set the values
				insertNewRow(1 << 30, new Timestamp((l - 5) * 60000),
						"224.0.0.251", "10.0.156.1");
				insertNewRow(1 << 30, new Timestamp((l - 5) * 60000),
						"10.0.156.1", "224.0.0.251");
			}

			// Open a graph page
			// Create a conversation
			WebConversation wc = new WebConversation();

			// Obtain the upload page on web site
			WebRequest request = new GetMethodWebRequest(m_urlPmgraph);
			WebResponse response = wc.getResponse(request);
			String path = response.getImageWithAltText("Bandwidth Graph")
					.getSource();
			URL urlObj = new URL(m_urlPmgraph);
			urlObj = new URL(urlObj, path);
			request = new GetMethodWebRequest(urlObj.toString());
			response = wc.getResponse(request);
			assertEquals("image/png", response.getContentType());
		} catch (HttpInternalErrorException e) {
			System.out
					.println("Problem with legend.jsp. There is value too big for integer.");
			throw (e);
		}
	}

	private void insertNewRow(long bytes, Timestamp theTime, String ip_src,
			String ip_dest) throws SQLException {
		try {
			PreparedStatement stmt = conn.prepareStatement(INSERT_DATA);
			stmt.setLong(1, bytes);
			stmt.setTimestamp(2, new Timestamp(theTime.getTime()));
			stmt.setString(3, ip_src);
			stmt.setString(4, ip_dest);
			stmt.setInt(5, 0);
			stmt.setInt(6, 0);

			// Insert the row
			// System.out.println(stmt);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	private void insertRow(String ip_src, String ip_dst, int src_port,
			int dst_port, long bytes, Timestamp t) throws SQLException {
		try {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO "
					+ TABLE_NAME + " (ip_src, ip_dst, src_port, dst_port, "
					+ "bytes, stamp_inserted) VALUES (?,?,?,?,?,?)");
			stmt.setString(1, ip_src);
			stmt.setString(2, ip_dst);
			stmt.setInt(3, src_port);
			stmt.setInt(4, dst_port);
			stmt.setLong(5, bytes);
			stmt.setTimestamp(6, t);

			// Insert the row
			// System.out.println(stmt);
			stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

	/* Prints the table in the console window, only for debug purposes */
	private void DisplayTableData() throws SQLException {
		try {
			PreparedStatement stmt = conn.prepareStatement(GET_TABLE_DATA);
			System.out.println(stmt);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				long theBytes = rs.getLong(1);
				Timestamp timest = rs.getTimestamp(2);
				String theIp_src = rs.getString(3);
				String theIp_dest = rs.getString(4);

				System.out.println(theBytes + "  " + timest.toString() + "  "
						+ theIp_src + "  " + theIp_dest);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw (e);
		}
	}

	public void testCumulativeGraph() throws Exception {
		CreateTable();
		InsertSampleData();

		JFreeChart chart = GraphFactory.stackedThroughput(t1.getTime(), t4
				.getTime());
		assertEquals("Network Throughput Per IP", chart.getTitle().getText());

		XYPlot plot = (XYPlot) chart.getPlot();
		assertEquals(PlotOrientation.VERTICAL, plot.getOrientation());
		assertEquals(0.8f, plot.getForegroundAlpha(), 0.01);
		Collection markers = plot.getRangeMarkers(Layer.FOREGROUND);
		Iterator i = markers.iterator();
		assertEquals(i.next(), new ValueMarker(0));
		assertFalse(i.hasNext());

		DateAxis xAxis = (DateAxis) plot.getDomainAxis();
		assertEquals(-0.01, xAxis.getLowerMargin());
		assertEquals(0.0, xAxis.getUpperMargin());

		NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
		assertEquals("Throughput (kb/s)", yAxis.getLabel());

		DefaultTableXYDataset dataset = (DefaultTableXYDataset) plot
				.getDataset();
		// assertEquals(24, dataset.getSeriesCount());

		String[] hosts = new String[] { "10.0.156.120", "10.0.156.110",
				"10.0.156.131", "10.0.156.132", "10.0.156.133", "10.0.156.134",
				"10.0.156.135", "10.0.156.136", "10.0.156.137", "10.0.156.138",
				"10.0.156.139", "10.0.156.140" };

		for (int n = 0; n < hosts.length; n++) {
			String str = dataset.getSeries(n << 1).getKey().toString();
			assertTrue("missing item " + hosts[n],
					dataset.getSeriesCount() > (n << 1));
			assertEquals(hosts[n] + "<down>", dataset.getSeries(n << 1)
					.getKey());
			assertEquals(hosts[n] + "<up>", dataset.getSeries((n << 1) + 1)
					.getKey());
		}

		assertEquals(hosts.length * 2, dataset.getSeriesCount());

		Map<String, XYSeries> series = new HashMap<String, XYSeries>();

		for (int n = 0; n < dataset.getSeriesCount(); n++) {
			XYSeries s = dataset.getSeries(n);
			assertEquals(4, s.getItemCount());

			assertEquals(t1.getTime(), s.getX(0));
			assertEquals(t2.getTime(), s.getX(1));
			assertEquals(t3.getTime(), s.getX(2));
			assertEquals(t4.getTime(), s.getX(3));

			assertEquals(Long.valueOf(0), s.getY(2));

			series.put(s.getKey().toString(), s);
		}

		XYSeries s = series.get("10.0.156.110<up>");
		assertEquals(Long.valueOf(-2000), s.getY(0));
		assertEquals(Long.valueOf(0), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(0), s.getY(3));

		s = series.get("10.0.156.110<down>");
		assertEquals(Long.valueOf(90), s.getY(0));
		assertEquals(Long.valueOf(80), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(70), s.getY(3));

		s = series.get("10.0.156.120<up>");
		assertEquals(Long.valueOf(0), s.getY(0));
		assertEquals(Long.valueOf(-500 * 11), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(0), s.getY(3));

		s = series.get("10.0.156.120<down>");
		assertEquals(Long.valueOf(0), s.getY(0));
		assertEquals(Long.valueOf(5500 + 50), s.getY(1));
		assertEquals(Long.valueOf(0), s.getY(2));
		assertEquals(Long.valueOf(75), s.getY(3));

		for (int n = 2; n < hosts.length; n++) {
			assertEquals("10.0.156." + (130 + n - 1), hosts[n]);

			s = series.get(hosts[n] + "<up>");
			assertEquals(Long.valueOf(0), s.getY(0));
			assertEquals(Long.valueOf(0), s.getY(1));
			assertEquals(Long.valueOf(0), s.getY(2));
			assertEquals(Long.valueOf(-100 * (12 - n) - 50), s.getY(3));

			s = series.get(hosts[n] + "<down>");
			assertEquals(Long.valueOf(0), s.getY(0));
			assertEquals(Long.valueOf(0), s.getY(1));
			assertEquals(Long.valueOf(0), s.getY(2));
			assertEquals(Long.valueOf(0), s.getY(3));
		}
	}

	// The images comparence doesn't work,
	// because of different settings
	/*
	 * ----------------------------------- /* This test tests the graph image
	 */
	/*
	 * public void testCheckGraphImage() throws Exception { CreateTable();
	 *  // Insert rows into table InsertSampleData();
	 *  // Open a graph page // Create a conversation WebConversation wc = new
	 * WebConversation();
	 *  // Obtain the upload page on web site WebRequest request = new
	 * GetMethodWebRequest(m_urlPmgraph + "?start=0&end=300000"); WebResponse
	 * response = wc.getResponse(request); // Get the image from the graph page
	 * WebImage webImg = response.getImageWithAltText("Bandwith Graph");
	 * 
	 * URL urlObj = new URL(m_urlPmgraph); urlObj = new URL(urlObj,
	 * webImg.getSource());
	 *  // Create new request for the image WebRequest requestImg = new
	 * GetMethodWebRequest(urlObj.toString()); WebResponse responseImg =
	 * wc.getResponse(requestImg);
	 *  // Get the input strem of the image InputStream inputStr =
	 * responseImg.getInputStream();
	 *  // Create BufferedImage from the input stream BufferedImage actualImg =
	 * ImageIO.read(inputStr);
	 *  // Create BufferedImage from file BufferedImage expectedImg =
	 * ImageIO.read(DataBaseTest.class.getResource("../fixtures/graphCheckGraphImageTest.png"));
	 *  // Compare the height and the width of the images assertEquals("Compare
	 * the height of the images.", expectedImg.getHeight(),
	 * actualImg.getHeight()); assertEquals("Compare the width of the images.",
	 * expectedImg.getWidth(), actualImg.getWidth());
	 *  // Compare the pixels of the two images for(int y = 0; y <
	 * expectedImg.getHeight(); y++) { for(int x = 0; x <
	 * expectedImg.getWidth(); x++) { assertEquals("Compare the image's
	 * pixels.", expectedImg.getRGB(x, y), actualImg.getRGB(x, y)); } } }
	 */

	private void InsertSampleData() throws SQLException {
		// convert all values into something nice and large in kbps
		// all values divided by 128 and 60 in GraphFactory to convert
		// bytes into kbps.
		insertRow("10.0.156.110", "10.0.156.120", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.120", "10.0.156.110", 1, 1, 9999 * 128 * 60, t1);
		insertRow("10.0.156.110", "4.2.2.2", 12300, 80, 2000 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 90 * 128 * 60, t1);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 80 * 128 * 60, t2);
		insertRow("4.2.2.2", "10.0.156.110", 80, 12300, 70 * 128 * 60, t4);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23400, 50 * 128 * 60, t2);
		insertRow("4.2.2.2", "10.0.156.120", 80, 23500, 75 * 128 * 60, t4);
		insertRow("10.0.156.120", "4.2.2.2", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.3", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.4", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.5", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.6", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.7", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.8", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.9", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.10", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.11", 90, 10000, 500 * 128 * 60, t2);
		insertRow("10.0.156.120", "4.2.2.12", 90, 10000, 500 * 128 * 60, t2);
		insertRow("4.2.2.3", "10.0.156.120", 90, 10000, 1000 * 128 * 60, t2);
		insertRow("4.2.2.4", "10.0.156.120", 90, 10000, 900 * 128 * 60, t2);
		insertRow("4.2.2.5", "10.0.156.120", 90, 10000, 800 * 128 * 60, t2);
		insertRow("4.2.2.6", "10.0.156.120", 90, 10000, 700 * 128 * 60, t2);
		insertRow("4.2.2.7", "10.0.156.120", 90, 10000, 600 * 128 * 60, t2);
		insertRow("4.2.2.8", "10.0.156.120", 90, 10000, 500 * 128 * 60, t2);
		insertRow("4.2.2.9", "10.0.156.120", 90, 10000, 400 * 128 * 60, t2);
		insertRow("4.2.2.10", "10.0.156.120", 90, 10000, 300 * 128 * 60, t2);
		insertRow("4.2.2.11", "10.0.156.120", 90, 10000, 200 * 128 * 60, t2);
		insertRow("4.2.2.12", "10.0.156.120", 90, 10000, 100 * 128 * 60, t2);
		insertRow("10.0.156.131", "4.2.2.2", 90, 10000, 1050 * 128 * 60, t4);
		insertRow("10.0.156.132", "4.2.2.2", 90, 10000, 950 * 128 * 60, t4);
		insertRow("10.0.156.133", "4.2.2.2", 90, 10000, 850 * 128 * 60, t4);
		insertRow("10.0.156.134", "4.2.2.2", 90, 10000, 750 * 128 * 60, t4);
		insertRow("10.0.156.135", "4.2.2.2", 90, 10000, 650 * 128 * 60, t4);
		insertRow("10.0.156.136", "4.2.2.2", 90, 10000, 550 * 128 * 60, t4);
		insertRow("10.0.156.137", "4.2.2.2", 90, 10000, 450 * 128 * 60, t4);
		insertRow("10.0.156.138", "4.2.2.2", 90, 10000, 350 * 128 * 60, t4);
		insertRow("10.0.156.139", "4.2.2.2", 90, 10000, 250 * 128 * 60, t4);
		insertRow("10.0.156.140", "4.2.2.2", 90, 10000, 150 * 128 * 60, t4);
	}

	public void testSorter() throws Exception {

		CreateTable();

		// Insert rows into table
		for (int i = 0; i < 100; i++) {
			// Set the values
			insertNewRow(500000, t1, "224.0.0.255", "10.0.156.10");
			insertNewRow(300000, t1, "10.0.156.10", "224.0.0.254");

			insertNewRow(100000, t1, "224.0.0.251", "10.0.156.1");
			insertNewRow(150000, t2, "10.0.156.1", "224.0.0.251");

			insertNewRow(400000, t2, "224.0.0.255", "10.0.156.120");
			insertNewRow(1140000, t2, "10.0.156.120", "224.0.0.255");
		}

		// Open a graph page
		// Create a conversation
		WebConversation wc = new WebConversation();

		// Obtain the upload page on web site
		WebRequest request = new GetMethodWebRequest(m_urlPmgraph
				+ "index.jsp?report=totals&graph=cumul&start=0&end=300000");
		WebResponse response = wc.getResponse(request);

		WebLink link = response.getLinkWithName("downloaded");

		// the default is 'sort by download DESC', the sortLink is opposite to
		// the DESC
		String sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC";

		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		request = new GetMethodWebRequest(m_urlPmgraph
				+ "index.jsp?start=0&end=300000&sortBy=downloaded&order=ASC");
		response = wc.getResponse(request);
		link = response.getLinkWithName("downloaded");
		sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=downloaded&order=DESC";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		// Get the table data from the page
		WebTable tables[] = response.getTables();
		if (tables.length != 0) {
			// Row 2
			String hostIP1 = tables[0].getCellAsText(2, 1);
			String downloaded1 = tables[0].getCellAsText(2, 2);
			String uploaded1 = tables[0].getCellAsText(2, 3);
			// Row 3
			String hostIP2 = tables[0].getCellAsText(3, 1);
			String downloaded2 = tables[0].getCellAsText(3, 2);
			String uploaded2 = tables[0].getCellAsText(3, 3);
			// Row 4
			String hostIP3 = tables[0].getCellAsText(4, 1);
			String downloaded3 = tables[0].getCellAsText(4, 2);
			String uploaded3 = tables[0].getCellAsText(4, 3);

			// Check the table data
			// .10 47 = 500000*100/1024/1024 28
			// .120 38 = 400000*100/1024/1024 108
			// .1 9 = 100000*100/1024/1024 14
			assertEquals("Check the IP Address", "10.0.156.10", hostIP3);
			assertEquals("Check the Downloaded Value, r1", "47", downloaded3);
			assertEquals("Check the Downloaded Value", "28", uploaded3);
			assertEquals("Check the IP Address", "10.0.156.120", hostIP2);
			assertEquals("Check the Downloaded Value r2", "38", downloaded2);
			assertEquals("Check the Downloaded Value", "108", uploaded2);
			assertEquals("Check the IP Address", "10.0.156.1", hostIP1);
			assertEquals("Check the Downloaded Value r3", "9", downloaded1);
			assertEquals("Check the Downloaded Value", "14", uploaded1);
		}

		request = new GetMethodWebRequest(m_urlPmgraph
				+ "index.jsp?start=0&end=300000&sortBy=uploaded&order=DESC");
		response = wc.getResponse(request);
		link = response.getLinkWithName("uploaded");
		sortLink = "/pmgraph/index.jsp?start=0&end=300000&sortBy=uploaded&order=ASC";
		assertEquals("Compare the sort link.", sortLink, link.getURLString());

		// Get the table data from the page
		WebTable tables2[] = response.getTables();
		if (tables2.length != 0) {
			// Row 2
			String hostIP1 = tables2[0].getCellAsText(2, 1);
			String downloaded1 = tables2[0].getCellAsText(2, 2);
			String uploaded1 = tables2[0].getCellAsText(2, 3);
			// Row 3
			String hostIP2 = tables2[0].getCellAsText(3, 1);
			String downloaded2 = tables2[0].getCellAsText(3, 2);
			String uploaded2 = tables2[0].getCellAsText(3, 3);
			// Row 4
			String hostIP3 = tables2[0].getCellAsText(4, 1);
			String downloaded3 = tables2[0].getCellAsText(4, 2);
			String uploaded3 = tables2[0].getCellAsText(4, 3);

			// Check the table data
			// .10 47 = 500000*100/1024/1024 28
			// .120 38 = 400000*100/1024/1024 108
			// .1 9 = 100000*100/1024/1024 14
			assertEquals("Check the IP Address", "10.0.156.120", hostIP1);
			assertEquals("Check the Downloaded Value, r1", "38", downloaded1);
			assertEquals("Check the Downloaded Value", "108", uploaded1);
			assertEquals("Check the IP Address", "10.0.156.10", hostIP2);
			assertEquals("Check the Downloaded Value r2", "47", downloaded2);
			assertEquals("Check the Downloaded Value", "28", uploaded2);
			assertEquals("Check the IP Address", "10.0.156.1", hostIP3);
			assertEquals("Check the Downloaded Value r3", "9", downloaded3);
			assertEquals("Check the Downloaded Value", "14", uploaded3);
		}

	}
	public void testDataAccesss() throws ClassNotFoundException,
	IllegalAccessException, InstantiationException, IOException, 
	SQLException{
		
		CreateTable();
		InsertSampleData();
		DataAccess dataAccess = DataAccess.getDatabase();
		ResultSet resultPerIP = dataAccess.getThroughputPerIP(0, 300000);
		ResultSet resultPerIPSort = dataAccess.getThroughputPerIP(0, 300000, "downloaded", "ASC");
		ResultSet resultPerIPPerMinute = dataAccess.getThroughputPIPPMinute(0, 300000);
		assertTrue("get result per IP", resultPerIP.next());
		assertTrue("get result per IP", resultPerIPSort.next());
		assertTrue("get result per IP", resultPerIPPerMinute.next());
		resultPerIP.close();
		resultPerIPSort.close();
		resultPerIPPerMinute.close();
		
	}

	public static Test suite() {
		return new TestSuite(DataBaseTest.class);
	}
}