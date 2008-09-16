<!-- Div which includes select for report type and radios for graph type -->

<div id="params">
    <form id="graphparams" action="/pmgraph/index.jsp" method="get">
        <p>
        <label for="report">Report type:</label> 
        <select id="report" name="report" disabled="disabled">
            <option value="totals">Totals</option>
            <option value="toplocalhosts">Top local hosts</option>
	        <option value="topremotehosts">Top remote hosts</option>
	        <option value="toplocalports">Top local ports</option>
            <option value="topremoteports">Top remote ports</option>
        </select> 
        <label for="period">From</label> 
        <input id="period" type="text" name="period" size="3" value="0" disabled="disabled" /> 
        <select name="scale" disabled="disabled">
	        <option value="1">minutes</option>
	        <option value="60">hours</option>
	        <option value="1440">days</option>
        </select> 
        
        ago to now 
        
        <input type="submit" id="go" value="Update" disabled="disabled" />
        </p>

        <p class="radios">
        <label for="cumul">Cumulative</label> 
        <input id="cumul" type="radio" name="style" value="cumul" checked="checked"	disabled="disabled" /> 
        <label for="line">Line</label> 
        <input id="line" type="radio" name="style" value="line"	disabled="disabled" /> 
	    <label for="pie">Pie</label> 
	    <input id="pie"	type="radio" name="style" value="pie" disabled="disabled" /></p>
    </form>
</div>