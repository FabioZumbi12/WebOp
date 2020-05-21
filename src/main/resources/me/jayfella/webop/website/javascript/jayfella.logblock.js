$("#logBlockExpand").click(function()
{
    $(this).toggleClass("ui-icon ui-icon-circle-triangle-s");
    $(this).toggleClass("ui-icon ui-icon-circle-triangle-n");

    $("#logBlockControls").slideToggle();
});
    
$("#logBlockAllPlayersGeneralButton").click(function()
{
    var selectedOre = $("#logBlockAllPlayersGeneralBlock option:selected").text();
    var sinceTime = $("#logBlockAllPlayersGeneralSince").val();
    var playerName = $("#logBlockAllPlayersGeneralPlayer").val();

    $("#logBlockAllPlayersGeneralAjaxLoader").css("visibility", "visible");
    $.post("data.php", { case: "logblock", ore: selectedOre, player: playerName, since: sinceTime }, parseLogBlockResponse);

});

function parseLogBlockResponse(message)
{
    if (message.trim() === "")
        message = "No results found.";
            
    message = "<br/>" + message;
            
    $("#logBlockAllPlayersGeneralAjaxLoader").css("visibility", "hidden");
    $("#logBlockAllPlayersGeneralOutput").html(message);
}

$("#logBlockAllPlayersGeneralClear").click(function()
{
    $("#logBlockAllPlayersGeneralPlayer").val("");
    $("#logBlockAllPlayersGeneralOutput").html("");
});