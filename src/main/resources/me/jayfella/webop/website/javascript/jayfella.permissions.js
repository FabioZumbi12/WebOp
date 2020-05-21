
$("#accessWhitelist").selectable();

$("#accessRemovePlayersButton").click(function()
{
    var playernames = "";
    
    $(".ui-selected", "#accessWhitelist").each(function()
    {
        playernames += $(this).text() + ",";
    });
    
    $.post("permissions.php", { case: "webopaccess", action: "remove", players: playernames }, parseAccessResponse);
});

$("#accessAddPlayersButton").click(function()
{
    var playernames = $("#accessAddPlayersInput").val();
    $.post("permissions.php", { case: "webopaccess", action: "add", players: playernames }, parseAccessResponse);
});

function parseAccessResponse(resp)
{
    $("#accessWhitelist").html(resp);
}



$( "#consoleViewWhitelist" ).selectable();

$("#consoleViewRemovePlayersButton").click(function()
{
    var playernames = "";
    
    $(".ui-selected", "#consoleViewWhitelist").each(function()
    {
        playernames += $(this).text() + ",";
    });
    
    $.post("permissions.php", { case: "consoleView", action: "remove", players: playernames }, parseConsoleViewResponse);
});

$("#consoleViewAddPlayersButton").click(function()
{
    var playernames = $("#consoleViewAddPlayersInput").val();
    $.post("permissions.php", { case: "consoleView", action: "add", players: playernames }, parseConsoleViewResponse);
});

function parseConsoleViewResponse(resp)
{
    $("#consoleViewWhitelist").html(resp);
}



$( "#consoleAsOpWhitelist" ).selectable();

$("#consoleAsOpRemovePlayersButton").click(function()
{
    var playernames = "";
    
    $(".ui-selected", "#consoleAsOpWhitelist").each(function()
    {
        playernames += $(this).text() + ",";
    });
    
    $.post("permissions.php", { case: "consoleAsOp", action: "remove", players: playernames }, parseConsoleAsOpResponse);
});

$("#consoleAsOpAddPlayersButton").click(function()
{
    var playernames = $("#consoleAsOpAddPlayersInput").val();
    $.post("permissions.php", { case: "consoleAsOp", action: "add", players: playernames }, parseConsoleAsOpResponse);
});

function parseConsoleAsOpResponse(resp)
{
    $("#consoleAsOpWhitelist").html(resp);
}