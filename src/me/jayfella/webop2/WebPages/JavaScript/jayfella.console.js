$( "#consoleHeightSlider" ).slider(
{
    range: "max",
    min: 150,
    max: 1000,
    value: 300,
    step: 5,
    slide: function( event, ui ) { $("#consoleData").css("height", ui.value); }
});

$("#consoleSumbitButton").click( function()
{
    var executeAsConsole = $('#consoleCommandType').is(':checked') ? "true" : "false";
    $.post("console.php", { command: $("#consoleText").val(), asConsole: executeAsConsole });
});

function processConsoleResponse(value)
{
    var elem = $("#consoleData");

    if (value === "NOT_ALLOWED")
    {
        $(elem).parent().remove();
        return;
    }

    var atBottom = (elem[0].scrollHeight - elem.scrollTop() === elem.outerHeight());
    $("#consoleData").append(value);
    if (atBottom) $("#consoleData").scrollTop($("#consoleData")[0].scrollHeight);

    setTimeout(consolePoller, 1000);
}

var consolePoller = function()
{
    $.get("console.php", processConsoleResponse);
};

consolePoller();
