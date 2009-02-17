<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC 
    "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ page import="java.util.Date" %>

<%
    // Graph parameters
    String param;
    String report = (param = request.getParameter("report")) != null ? param : "totals";
    String graph = (param = request.getParameter("graph")) != null ? param : "cumul";
    long now = new Date().getTime();
    long startTime = (param = request.getParameter("start")) != null ? Long.parseLong(param) : now - 240 * 60000;
    long endTime = (param = request.getParameter("end")) != null ? Long.parseLong(param) : now;
    
    long scrollAmount = (endTime - startTime) / 2;  
    long zoomAmount = (endTime - startTime) / 2; 
    
    long newZoomInStart = ((startTime + zoomAmount/2) / 6000);
    long newZoomInEnd = ((endTime - zoomAmount/2) / 6000);
    
    long newZoomOutStart = ((startTime - zoomAmount) / 6000);
    long newZoomOutEnd = ((endTime + zoomAmount) / 6000);
        
         //the sort parameters
    //sortBy: bytes_total | downloaded | uploaded
	String sortBy = (param = request.getParameter("sortBy")) != null ? param : "bytes_total";
	//order: DESC | ASC
	String order = (param = request.getParameter("order")) != null ? param : "DESC";
                       		
    // URLs to resources requiring further parameters
    String indexURL = "/pmgraph/index.jsp";
    String servletURL = "/pmgraph/graphservlet";
    String legendURL = "/include/legend.jsp";
    
%>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
    <head>
        <title>pmGraph</title>
        <link rel="Stylesheet" href="styles/main.css" type="text/css" />
        <script type="text/javascript">
			var dragging = false, drag_start_x = 0, drag_start_y = 0;
			function getevent(e)
			{
				if (!e)
				{
					e = window.event;
				}

				return e;
			}
			function x(e)
			{
				return e.pageX; // event.x;
			}
			function y(e)
			{
				return e.pageY; // event.y;
			}
			function target(e)
			{
				if (e.target)
				{
				  targ = e.target;
				}
				else if (e.srcElement)
				{
				  targ = e.srcElement;
				}
				if (targ.nodeType == 3) // defeat Safari bug
				{
				  targ = targ.parentNode;
				}
				return targ;
			}
			function mousedown(e)
			{
				e = getevent(e);
				dragging = true;
				drag_start_x = x(e);
				drag_start_y = y(e);
				return false; // stop drag
			}
			function mousemove(e)
			{
				e = getevent(e);

				if (dragging)
				{
					target(e).style.position = "relative";
					target(e).style.left = x(e) - drag_start_x;
				}
			}
			function mouseup(e)
			{
				e = getevent(e);
				if (!dragging) return true;
				var off_x = x(e) - drag_start_x;
				var off_y = y(e) - drag_start_y;
				//alert(off_x + "," + off_y);
				dragging = false;
				return true;
			}
			
			function dateAndTimeValidation(fromDateValue, fromTimeValue, toDateValue, toTimeValue)
			{
			alert("Valid Data000");
				if((isDate(fromDateValue,"dd/MM/yyyy") == false) || (isDate(toDateValue,"dd/MM/yyyy")== false))
				{
					alert("The date format should be : dd/mm/yyyy");
					return false;
				}
				
				if((isDate(fromTimeValue,"hh:mm:ss") == false) || (isDate(toTimeValue,"hh:mm:ss")== false))
				{
					alert("The time format should be : hh:mm:ss");
					return false;
				}
				alert("Valid Data0");
				return true;
			}
			
			function onClickGoButton()
			{
				alert("This feature is under construction, it's coming soon");
				
				// Date Validation
				var theFromDate = document.forms['SetDateAndTime'].elements['fromDate'].value;
				var theFromTime = document.forms['SetDateAndTime'].elements['fromTime'].value;
				var thetoDate = document.forms['SetDateAndTime'].elements['toDate'].value;			
				var theToTime = document.forms['SetDateAndTime'].elements['toTime'].value;
				
				//var validData = dateAndTimeValidation(theFromDate, theFromTime, thetoDate, thetoTime);
				
				
				if((isDate(fromDateValue,"dd/MM/yyyy") == false) || (isDate(toDateValue,"dd/MM/yyyy")== false))
				{
					alert("The date format should be : dd/mm/yyyy");
					validData = false;
				}
				
				if((isDate(fromTimeValue,"hh:mm:ss") == false) || (isDate(toTimeValue,"hh:mm:ss")== false))
				{
					alert("The time format should be : hh:mm:ss");
					validData = false;
				}
				
				// Load the page
				if(validData == true)
				{
					alert("Valid Data1");
				}
				
			}
			
			
			function onLoad()
			{
				// Set focus
				document.forms['SetDateAndTime'].elements['fromDate'].focus();
				
				// Init text boxes
				var theDate = new Date();				
				document.forms['SetDateAndTime'].elements['toDate'].value = theDate.toLocaleDateString();
				document.forms['SetDateAndTime'].elements['toTime'].value = theDate.toLocaleTimeString();
				
				theDate.setDate(theDate.getDate()-1);
				document.forms['SetDateAndTime'].elements['fromDate'].value = theDate.toLocaleDateString();
				document.forms['SetDateAndTime'].elements['fromTime'].value = theDate.toLocaleTimeString();
			}
			
			////////////////////////////////////////
			
			var MONTH_NAMES=new Array('January','February','March','April','May','June','July','August','September','October','November','December','Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');
			var DAY_NAMES=new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sun','Mon','Tue','Wed','Thu','Fri','Sat');
			function LZ(x) {return(x<0||x>9?"":"0")+x}

			// ------------------------------------------------------------------	
			// isDate ( date_string, format_string )
			// Returns true if date string matches format of format string and
			// is a valid date. Else returns false.
			// It is recommended that you trim whitespace around the value before
			// passing it to this function, as whitespace is NOT ignored!
			// ------------------------------------------------------------------
			function isDate(val,format) 
			{
				var date = getDateFromFormat(val,format);
				if (date==0) 
				{ 
					return false; 
				}
				return true;
			}

			// -------------------------------------------------------------------
			// compareDates(date1,date1format,date2,date2format)
			//   Compare two date strings to see which is greater.
			//   Returns:
			//   1 if date1 is greater than date2
			//   0 if date2 is greater than date1 of if they are the same
			//  -1 if either of the dates is in an invalid format
			// -------------------------------------------------------------------
			function compareDates(date1,dateformat1,date2,dateformat2) 
			{
				var d1=getDateFromFormat(date1,dateformat1);
				var d2=getDateFromFormat(date2,dateformat2);
				if (d1==0 || d2==0) 
				{
					return -1;
				}
				else if (d1 > d2) 
				{
					return 1;
				}
				return 0;
			}
			
			// ------------------------------------------------------------------
			// getDateFromFormat( date_string , format_string )
			//
			// This function takes a date string and a format string. It matches
			// If the date string matches the format string, it returns the 
			// getTime() of the date. If it does not match, it returns 0.
			// ------------------------------------------------------------------
			function getDateFromFormat(val,format) 
			{
				val=val+"";
				format=format+"";
				var i_val=0;
				var i_format=0;
				var c="";
				var token="";
				var token2="";
				var x,y;
				var now=new Date();
				var year=now.getYear();
				var month=now.getMonth()+1;
				var date=1;
				var hh=now.getHours();
				var mm=now.getMinutes();
				var ss=now.getSeconds();
				var ampm="";
	
				while (i_format < format.length)
				{
					// Get next token from format string
					c=format.charAt(i_format);
					token="";
					while ((format.charAt(i_format)==c) && (i_format < format.length)) 
					{
						token += format.charAt(i_format++);
					}
					// Extract contents of value based on format token
					if (token=="yyyy" || token=="yy" || token=="y") 
					{
						if (token=="yyyy") { x=4;y=4; }
						if (token=="yy")   { x=2;y=2; }
						if (token=="y")    { x=2;y=4; }
						year=_getInt(val,i_val,x,y);
						if (year==null) { return 0; }
						i_val += year.length;
						if (year.length==2) 
						{
							if (year > 70) { year=1900+(year-0); }
							else { year=2000+(year-0); }
						}
					}
					else if (token=="MMM"||token=="NNN")
					{
						month=0;
						for (var i=0; i<MONTH_NAMES.length; i++) 
						{
							var month_name=MONTH_NAMES[i];
							if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) 
							{
								if (token=="MMM"||(token=="NNN"&&i>11)) 
								{
									month=i+1;
									if (month>12) { month -= 12; }
									i_val += month_name.length;
									break;
								}
							}
						}
						if ((month < 1)||(month>12)){return 0;}
					}
					else if (token=="EE"||token=="E")
					{
						for (var i=0; i<DAY_NAMES.length; i++) 
						{
							var day_name=DAY_NAMES[i];
							if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) 
							{
								i_val += day_name.length;
								break;
							}
						}
					}
					else if (token=="MM"||token=="M") 
					{
						month=_getInt(val,i_val,token.length,2);
						if(month==null||(month<1)||(month>12)){return 0;}
						i_val+=month.length;
					}
					else if (token=="dd"||token=="d") 
					{
						date=_getInt(val,i_val,token.length,2);
						if(date==null||(date<1)||(date>31)){return 0;}
						i_val+=date.length;
					}
					else if (token=="hh"||token=="h") 
					{
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<1)||(hh>12)){return 0;}
						i_val+=hh.length;
					}
					else if (token=="HH"||token=="H") 
					{
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<0)||(hh>23)){return 0;}
						i_val+=hh.length;
					}
					else if (token=="KK"||token=="K")
					{
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<0)||(hh>11)){return 0;}
						i_val+=hh.length;
					}
					else if (token=="kk"||token=="k") 
					{
						hh=_getInt(val,i_val,token.length,2);
						if(hh==null||(hh<1)||(hh>24)){return 0;}
						i_val+=hh.length;hh--;
					}
					else if (token=="mm"||token=="m") 
					{
						mm=_getInt(val,i_val,token.length,2);
						if(mm==null||(mm<0)||(mm>59)){return 0;}
						i_val+=mm.length;
					}
					else if (token=="ss"||token=="s") 
					{
						ss=_getInt(val,i_val,token.length,2);
							if(ss==null||(ss<0)||(ss>59)){return 0;}
							i_val+=ss.length;
					}
					else if (token=="a") 
					{
						if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
						else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
						else {return 0;}
						i_val+=2;
					}
					else 
					{
						if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
						else {i_val+=token.length;}
					}
				}
				// If there are any trailing characters left in the value, it doesn't match
				if (i_val != val.length) { return 0; }
				// Is date valid for month?
				if (month==2) 
				{
					// Check for leap year
					if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) 
					{ // leap year
						if (date > 29){ return 0; }
					}
					else { if (date > 28) { return 0; } }
				}
				if ((month==4)||(month==6)||(month==9)||(month==11)) 
				{
					if (date > 30) { return 0; }
				}
				// Correct hours value
				if (hh<12 && ampm=="PM") { hh=hh-0+12; }
				else if (hh>11 && ampm=="AM") { hh-=12; }
				var newdate=new Date(year,month-1,date,hh,mm,ss);
				return newdate.getTime();
			}
			
			// ------------------------------------------------------------------
			// Utility functions for parsing in getDateFromFormat()
			// ------------------------------------------------------------------
			function _isInteger(val) 
			{
				var digits="1234567890";
				for (var i=0; i < val.length; i++) 
				{
					if (digits.indexOf(val.charAt(i))==-1) 
					{ 
						return false; 
					}
				}
				return true;
			}

			function _getInt(str,i,minlength,maxlength) 
			{
				for (var x=maxlength; x>=minlength; x--) 
				{
					var token=str.substring(i,i+x);
					if (token.length < minlength) 
					{ 
						return null; 
					}
					if (_isInteger(token)) 
					{ 
						return token; 
					}
				}
				return null;
			}
	
			
			
			/////////////////////////////////////////
			
        </script>
    </head>
  
    <body onload="onLoad()">
        <div id="container">
            <div id="header">
                <img alt="Logo Banner" src="images/header.png" width="780" height="75" />
            </div>
            
            <div id="main">
                <!-- Graph parameter controls not yet functional -->
                <!-- <jsp:include page="/include/params.jsp" /> -->
                
                <div id="graph">
                    <img id="graphimage" alt="Bandwidth Graph" 
                            src="<%=servletURL +
                                    "?graph=" + graph +
                                    "&start=" + startTime +
                                    "&end=" + endTime +
                                    "&width=780" +
                                    "&height=350"%>"
                            width="760" height="350"
                            onmousedown="return mousedown(event);"
                            onmousemove="return mousemove(event);"
                            onmouseup="return mouseup(event);"
                            />
                </div>
                
                <!-- Move back/forward or zoom in/out -->
                <div id="controls">
                    <a name="prev"
                       href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime - scrollAmount) +
                                "&end=" + (endTime - scrollAmount)%>" class="control">Prev.</a>
                    <div id="controlscenter">
                    
                    	<%if ((newZoomOutEnd - newZoomOutStart) < 357920) {%>
                        	<a name="zoomOut"
                        	   href="<%=indexURL +
                                    "?report=" + report +
                                    "&graph=" + graph +
                                    "&start=" + (startTime - zoomAmount) +
                                    "&end=" + (endTime + zoomAmount)%>" class="control">Zoom -</a>     
                       	<%}%>
                       		
                       	<%if ((newZoomInEnd - newZoomInStart) > 15) {%>
                        	<a	name="zoomIn"
                        		href="<%=indexURL +
                                     "?report=" + report +
                                     "&graph=" + graph +
                                     "&start=" + (startTime + zoomAmount/2) +
                                     "&end=" + (endTime - zoomAmount/2)%>" class="control">Zoom +</a>
                      	<%}%>             
                    </div>
                    <a name="next" 
                       href="<%=indexURL +
                                "?report=" + report +
                                "&graph=" + graph +
                                "&start=" + (startTime + scrollAmount) +
                                "&end=" + (endTime + scrollAmount)%>" class="control">Next</a>
                </div>    
    
                <div id="legend">
                    <jsp:include page="<%=legendURL + "?start=" + startTime + "&end=" + endTime + 
                                    "&sortBy=" + sortBy +
                                    "&order=" + order%>" />
                </div>
            </div>
            
            <div style = "position: absolute; left: 44%; top: 1%; width: 50%; height: 50%;">
            	<form id="SetDateAndTime" action="set time" style = "font-family: sans-serif; size = 1" >
            		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            		From &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            		To <br />
            		Date (dd/mm/yyyy) 
            			 <input type="text" id="fromDate" name="fromDate" size=8 /> <tab />
            			 <input type="text" id="toDate"   name="toDate"   size=8 />  <br />
					Time (hh/mm/ss)&nbsp;&nbsp;&nbsp;&nbsp; 
						 <input type="text" id="fromTime" name="fromTime" size=8 /> <tab />
					     <input type="text" id="toTime"   name="toTime"   size=8 />  <br /> 
					     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					     &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					     <input type="button" value="Go" id ="Go" name="Go" onclick="onClickGoButton()"/>
				</form>
            </div>
            
           
            
            
            
            
            
            
            
            
            
            
            <!-- <div id="footer"></div> -->
        </div>
    </body>
</html>