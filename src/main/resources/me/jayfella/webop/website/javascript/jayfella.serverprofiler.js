$("#startServerProfilingButton").click(function()
{
    $.post("data.php", { case: 'serverprofile', action: 'start' }, function(resp)
    {
        switch (resp)
        {
            case "OK":
            {
                $("#startServerProfilingButton").attr('disabled','disabled');
                $("#stopServerProfilingButton").removeAttr('disabled');
                break;
            }
            default:
            {
                alert(resp);
            }
        }
    });
});

$("#stopServerProfilingButton").click(function()
{
    $.post("data.php", { case: 'serverprofile', action: 'stop' }, function(resp)
    {
        if (resp === "NOT_PROFILING")
        {
            alert("Server is not currently profiling.");
            
            $("#startServerProfilingButton").removeAttr('disabled');
            $("#stopServerProfilingButton").attr('disabled','disabled');
            
            return;
        }
        else if (resp === "NO_EVENTS")
        {
            $("#serverProfileResult").html("No events are taking longer than 0.1ms");
            
            $("#startServerProfilingButton").removeAttr('disabled');
            $("#stopServerProfilingButton").attr('disabled','disabled');
            
            return;
        }
        
        var eventNames = [];
        var eventCounts = [];
        var eventDurations = [];
        
        var respData = resp.split("\n");
        
        for (var i = 0; i < respData.length; i++)
        {
            var respDataSplit = respData[i].split("=");
            
            switch(respDataSplit[0])
            {
                case "eventNames":
                {
                    eventNames = respDataSplit[1].split(",");
                    break;
                }
                case "eventCounts":
                {
                    var eventCountsSplit = respDataSplit[1].split(",");
                    
                    for (var x = 0; x < eventCountsSplit.length; x++)
                    {
                        var data = parseFloat(eventCountsSplit[x], 10);
                        eventCounts.push(data);
                    }
                    
                    break;
                }
                case "eventDurations":
                {
                    var eventDurationsSplit = respDataSplit[1].split(",");
                    
                    for (var x = 0; x < eventDurationsSplit.length; x++)
                    {
                        var data = parseFloat(eventDurationsSplit[x], 10);
                        eventDurations.push(data);
                    }
                    
                    break;
                }
            }
        }
        
        // { name: "Cpu Useage", type: 'spline', data: [], color: '#448844', unit: '%' },
        
        var profileChart = new Highcharts.Chart(
        {
            chart: { renderTo: 'serverProfileResult', zoomType: 'x' },
            credits: { enabled: false },
            title: { text: 'All Events taking longer than 0.1ms'},
            xAxis: { categories: eventNames, labels: { enabled: false } },
            yAxis: { title: { text: "Time (milliseconds)" } },
            tooltip: { formatter: function() 
                { 
                    console.log(profileChart); 
                    
                    var s2Result = profileChart.series[1].yData[this.point.x];
                    return this.key + "<br/>Time Taken: " + this.y + this.series.options.unit + "<br/>Times Called: " + s2Result; 
                } 
            },
            series:[ 
                { data: eventDurations, type: 'column', name: "Average Time", unit: " milliseconds" },
                { data: eventCounts, type: 'column', name: "Times Called", unit: " times", visible: false }]
        });
        
        $("#startServerProfilingButton").removeAttr('disabled');
        $("#stopServerProfilingButton").attr('disabled','disabled');
        
    });
});

$("#clearResultsProfilingButton").click(function()
{
    $("#serverProfileResult").html("");
});