function processProfilerResponse(value)
{
    $('#pluginTimingsResp').append(value);
}

$("#startEventProfilingButton").click(function()
{
    $('#startEventProfilingButton').attr('disabled','disabled');
    $('#clearEventProfilingButton').attr('disabled','disabled');
    $('#stopEventProfilingButton').removeAttr('disabled');
    $('#profilerRunningImage').css('visibility', 'visible');
    $('#pluginTimingsResp').css('padding-top', '10px');

    // $.get("profileplugins.php?state=start", processProfilerResponse);
    $.post("profileplugins.php", { state: "start" }, processProfilerResponse);
});

$("#stopEventProfilingButton").click(function()
{
    $('#stopEventProfilingButton').attr('disabled','disabled');
    $('#startEventProfilingButton').removeAttr('disabled');
    $('#clearEventProfilingButton').removeAttr('disabled');
    $('#profilerRunningImage').css('visibility', 'hidden');

    // $.get("profileplugins.php?state=stop", processProfilerResponse);
    $.post("profileplugins.php", { state: "stop" }, processProfilerResponse);
});

$("#clearEventProfilingButton").click(function()
{
    $('#pluginTimingsResp').html("");
    $('#pluginTimingsResp').css('padding-top', '0px');
    $('#clearEventProfilingButton').attr('disabled','disabled');
});

$("#stopEveryoneProfilingButton").click(function()
{
    $.post("profileplugins.php", { state: "forceabort" }, processProfilerResponse);
});

function processTasksResponse(value)
{
    $('#eventsResults').html(value);
}

$("#viewEventsButton").click(function()
{
    $.post("profileplugins.php", { state: "tasks" }, processTasksResponse);
    $('#eventsResults').css('padding-top', '10px');
    $('#clearEventsButton').removeAttr('disabled');
});

$("#clearEventsButton").click(function()
{
    $('#eventsResults').html("");
    $('#eventsResults').css('padding-top', '0px');
    $('#clearEventsButton').attr('disabled','disabled');
});