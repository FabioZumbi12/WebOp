function processLogSearchResult(value)
{
    $('#logSearchTerm').removeAttr('disabled');
    $('#logSearchTimeFrame').removeAttr('disabled');
    $('#logSearchButton').removeAttr('disabled');
    $('#ajaxloader').css('visibility', 'hidden');

    $('#searchOutput').html(value);
}

$("#logSearchButton").click(function()
{
    $('#logSearchTerm').attr('disabled','disabled');
    $('#logSearchTimeFrame').attr('disabled','disabled');
    $('#logSearchButton').attr('disabled','disabled');
    $('#ajaxloader').css('visibility', 'visible');

    var logSearchData = "searchTerm=" + $("#logSearchTerm").val() + "&" + "timeFrame=" + $("#logSearchTimeFrame").val();

    $.get("logsearch.php?" + logSearchData, processLogSearchResult);
});

$("#clearLogSearchButton").click(function()
{
    $('#searchOutput').html("");
    $('#logSearchTerm').val("");
});