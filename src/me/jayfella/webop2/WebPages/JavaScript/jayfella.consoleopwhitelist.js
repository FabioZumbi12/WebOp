$("#consoleOpWhitelistList").selectable({
    stop: function()
    {
        $("#consoleOpWhitelistRemoveSelectedSpan").val('');

        var output = '';

        $( ".ui-selected", this ).each(function()
        {
            output = output + $(this).text() + ", ";
        });

        $("#consoleOpWhitelistRemoveSelectedSpan").text(output);
    }
});

$("#consoleOpWhitelistAddPlayersButton").click(function ()
{
    var usersToAdd = $("#consoleOpWhitelistAddPlayersInput").val();

    $.post("whitelist.php", { action: "add", listType: "consoleOp", users: usersToAdd }, function(value)
    {
        $('#consoleOpWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#consoleOpWhitelistList").html(value);
    });
});

$("#consoleOpWhitelistRemoveSelectedButton").click(function()
{
    var removeNames = $("#consoleOpWhitelistRemoveSelectedSpan").text();

    $.post("whitelist.php", { action: "remove", listType: "consoleOp", users: removeNames }, function(value)
    {
        $('#consoleOpWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#consoleOpWhitelistList").html(value);
        $("#consoleOpWhitelistRemoveSelectedSpan").text("");
    });

});