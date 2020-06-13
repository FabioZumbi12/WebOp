var serverUtilizationChart;
var chunksAndEntitiesChart;

$(document).ready(function() 
{
    Highcharts.setOptions({ global : { useUTC : false } });
    
    serverUtilizationChart = new Highcharts.Chart(
    {
        chart: { renderTo: 'serverUtilizationGraph', animation: { duration: 2000 }, marginLeft: 20 },
        credits: { enabled: false },
        legend: { floating: true, align: 'left', verticalAlign: 'top', layout: 'vertical', backgroundColor: 'rgba(255, 255, 255, 0.8)', x: 15, y: 5, itemStyle: { fontSize: '10px' } },
        plotOptions: { spline: { marker: { enabled: false }, lineWidth: 2 } },
        title: { text: ''},
        subTitle: { text: '' },
        xAxis: { labels: { enabled: true }, type: 'datetime' },
        yAxis: { min: 0, max: 100, title: { text: '' }, tickInterval: 20, labels: { align: 'left' } },
        tooltip: { formatter: function() { return this.y + this.series.options.unit; } },
        series:[
            { name: "Cpu Usage", type: 'spline', data: [], color: '#448844', unit: '%' },
            { name: "Memory Usage", type: 'spline', data: [], color: '#ffa500', unit: '%' },
            { name: "Tick Rate", type: 'spline', data: [], color: '#ee4444', unit: 'TPS' }]
    });

    chunksAndEntitiesChart = new Highcharts.Chart(
    {
        chart: { renderTo: 'chunksAndEntitiesGraph', animation: { duration: 2000 } },
        credits: { enabled: false },
        legend: { floating: true, align: 'left', verticalAlign: 'top', layout: 'vertical', backgroundColor: 'rgba(255, 255, 255, 0.8)', x: 15, y: 5, itemStyle: { fontSize: '10px' } },
        plotOptions: { area: { marker: { enabled: false }, lineWidth: 2 } },
        title: { text: ''},
        subTitle: { text: '' },
        xAxis: { labels: { enabled: true }, type: 'datetime' },
        yAxis: { min: 0, title: { text: '' }, tickInterval: 20, labels: { enabled: false } },
        tooltip: { formatter: function() { return this.y + this.series.options.unit; } },
        series:[
            { name: "Chunks", type: 'area', data: [], color: '#65553a', unit: ' chunks' },
            { name: "Entities", type: 'area', data: [], color: '#45653a', unit: ' entities' }]
    });
    
});

function getServerUtilization()
{
    var msg = socketValidationString() + "&case=serverUtilization";
    webopSocket.send(msg);
}

var serverUtilizationIterations = 0;

function parseServerUtilization(message)
{
    var serverUtilizationResp = message.split(";");
    var shiftUp = (serverUtilizationChart.series[0].data.length > 120);
    
    var x = new Date().getTime();
    
    for (var i = 0; i < serverUtilizationResp.length; i++)
    {
        var serverUtilizationRespData = serverUtilizationResp[i].split("=");
        
        if (serverUtilizationRespData[0] === "CPU")
        {
            var y = parseFloat(serverUtilizationRespData[1], 10);
            serverUtilizationChart.series[0].addPoint([x,y], false, shiftUp);
        }
        else if (serverUtilizationRespData[0] === "MEM")
        {
            var y = parseFloat(serverUtilizationRespData[1], 10);
            serverUtilizationChart.series[1].addPoint([x, y], false, shiftUp);
        }
        else if (serverUtilizationRespData[0] === "TPS")
        {
            var y = parseFloat(serverUtilizationRespData[1], 10);
            serverUtilizationChart.series[2].addPoint([x, y], true, shiftUp);
        }
        else if (serverUtilizationRespData[0] === "CHUNKS")
        {
            var y = parseFloat(serverUtilizationRespData[1], 10);
            chunksAndEntitiesChart.series[0].addPoint([x, y], false, shiftUp);
        }
        else if (serverUtilizationRespData[0] === "ENTITIES")
        {
            var y = parseFloat(serverUtilizationRespData[1], 10);
            chunksAndEntitiesChart.series[1].addPoint([x, y], true, shiftUp);
        }
    }
    
    serverUtilizationIterations++;
    setTimeout(getServerUtilization, 1000);
}
