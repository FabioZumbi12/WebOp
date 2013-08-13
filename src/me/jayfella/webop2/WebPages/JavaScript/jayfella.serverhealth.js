
var maxHealthHistory = 60;
var healthIterations = 0;

var healthCpuLoad = [];
var healthFreeMem = [];
var healthTps = [];

var healthGraphOptions = {
    legend: { position: "sw", labelBoxBorderColor: '#000000', backgroundOpacity: 0.5 },
    grid: { backgroundColor: '#fafafa', borderWidth: 1 },
    yaxis: { min: 0, max: 109, tickSize:20, tickDecimals:0, position: "right" },
    xaxis: { ticks: false },
    'lines': { 'show': true, lineWidth: 2 },
    'points': { 'show': false },
    colors: ["#448844", "#ffa500", "#ee4444"]
    };

    var cpuLabel;
    var memLabel;
    var tpsLabel;

function processHealthResponse(value)
{
    var healthResp = value.split(";");

    for (var i = 0; i < healthResp.length; i++)
    {
        var healthRespData = healthResp[i].split("=");

        if (healthRespData[0] === "CPU")
        {
            healthCpuLoad.push([healthIterations, healthRespData[1]]);
            cpuLabel = "CPU Load (" + healthRespData[1] + "%)";
        }
        else if (healthRespData[0] === "MEM")
        {
            healthFreeMem.push([healthIterations, healthRespData[1]]);
            memLabel = "Used Memory (" + healthRespData[1] + "%)";
        }
        else if (healthRespData[0] === "TPS")
        {
            healthTps.push([healthIterations, healthRespData[1]]);
            tpsLabel = "TPS (" + healthRespData[1] + ")";
        }
    }

    while (healthCpuLoad.length > maxHealthHistory)
        healthCpuLoad.splice(0, 1);

    while (healthFreeMem.length > maxHealthHistory)
        healthFreeMem.splice(0, 1);

    while (healthTps.length > maxHealthHistory)
        healthTps.splice(0, 1);

    var cpuDataAxis = { label: cpuLabel , data: healthCpuLoad, shadowSize: 0 };
    var memDataAxis = { label: memLabel, data: healthFreeMem, shadowSize: 0 };
    var tpsDataAxis = { label: tpsLabel, data: healthTps, shadowSize: 0 };

    $.plot('#tpsGraph', [cpuDataAxis, memDataAxis, tpsDataAxis], healthGraphOptions);

    healthIterations++;
    setTimeout(serverHealthPoller, 1000);
}

function serverHealthPoller()
{
    $.ajax({
        type: "GET",
        url: "serverhealth.php",
        success: processHealthResponse,
        dataType: "text"
    });
};

serverHealthPoller();