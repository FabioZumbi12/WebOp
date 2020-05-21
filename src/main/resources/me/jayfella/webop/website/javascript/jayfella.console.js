function subscribeToConsole()
{
    var msg = socketValidationString() + "&case=subscribeConsole";
    webopSocket.send(msg);
}

var atBottom = true;
$("#consoleBottom").change( function()
{
    atBottom = $(this).is(':checked');
});

function parseConsoleResponse(message)
{
    var msg = message.replace("case=consoleData;", "");
    var elem = $("#consoleOutput");

    var atBottom2 = (elem[0].scrollHeight - elem.scrollTop() === elem.outerHeight());
        if (atBottom) {
            atBottom2 = atBottom;
        } else {
            atBottom2 = (elem[0].scrollHeight - elem.scrollTop() === elem.outerHeight());
        }
    $("#consoleOutput").append(msg);
    if (atBottom2) $(elem).scrollTop($(elem)[0].scrollHeight);
    
    var currentLines = document.getElementsByClassName('consoleLine');
    var maxLines = 250;
    
    if (currentLines.length > maxLines)
    {
        var amountToRemove = currentLines.length - maxLines;
        var amountRemoved = 0;

        for (var i = 0; i < currentLines.length; i++)
        {
            $(currentLines[i]).remove();
            amountRemoved++;
            if (amountRemoved === amountToRemove)
            {
                return false;
            }
        };
    }
}

$( "#consoleHeightSlider" ).slider(
{
    range: "max",
    min: 150,
    max: 1000,
    value: 300,
    step: 5,
    slide: function( event, ui ) { $("#consoleOutput").css("height", ui.value); }
});

$(document).on("keypress", "#consoleInput", function(e) 
{
    if (e.which === 13)
    {
        if ($(this).val().trim().length === 0)
            return;
        
        var asConsole = $("#consoleCheckbox").is(':checked');
        
        var msg = socketValidationString() + "&case=consoleCommand&asConsole=" + asConsole + "&command=" + $(this).val().trim();
        
        webopSocket.send(msg);
        $(this).val("");
    }
});

$('#consoleCheckbox').change(function() {
    
    if ($(this).is(":checked"))
    {
        $("#consoleInput").attr("placeholder", "Console command. e.g. /gamemode 1 FabioZumbi12");
    }
    else
    {
        $("#consoleInput").attr("placeholder", "Player command. e.g. /gamemode 1");
    }
});