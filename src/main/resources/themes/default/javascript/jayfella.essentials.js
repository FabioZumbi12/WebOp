$(document).on('mouseover', '.essPlayer', function()
{
    var playerElem = this;
    var playerName = $(playerElem).text();
    
    $("#essPlayerData").html(" ... loading ...");
    $("#essPlayerData").show();
    
    $.post("data.php", { case: "essentials", action: "playerData", player: playerName }, function(message)
    {
        $("#essPlayerData").html(message);
    });       
});

$(document).on('mouseleave', '.essPlayer', function()
{
    $("#essPlayerData").hide();
});

