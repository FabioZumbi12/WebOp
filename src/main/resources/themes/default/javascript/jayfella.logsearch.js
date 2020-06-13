$("#logSeachSubmitButton").click(function()
{
   $.post("data.php", 
    { 
        case: "logSearch",
        term: $("#logSearchTermInput").val(),
        timeframe: $("#logSearchTimeFrame").val()
    }, processLogSearchResult);
    
    $("#logSearchAjaxLoader").css("visibility", "visible");
});

function processLogSearchResult(value)
{
    $('#logSearchResult').html(value);
    $("#logSearchAjaxLoader").css("visibility", "hidden");
}

$("#logSearchClearButton").click(function()
{
    $("#logSearchResult").html("Search results cleared.");
});