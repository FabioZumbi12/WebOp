$("#consoleViewWhitelistList").selectable({
    stop: function()
    {
        $("#consoleViewWhitelistRemoveSelectedSpan").val('');

        var output = '';

        $( ".ui-selected", this ).each(function()
        {
            output = output + $(this).text() + ", ";
        });

        $("#consoleViewWhitelistRemoveSelectedSpan").text(output);
    }
});

$("#consoleViewWhitelistAddPlayersButton").click(function ()
{
    var usersToAdd = $("#consoleViewWhitelistAddPlayersInput").val();

    $.post("whitelist.php", { action: "add", listType: "consoleView", users: usersToAdd }, function(value)
    {
        $('#consoleViewWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#consoleViewWhitelistList").html(value);
    });
});

$("#consoleViewWhitelistRemoveSelectedButton").click(function()
{
    var removeNames = $("#consoleViewWhitelistRemoveSelectedSpan").text();

    $.post("whitelist.php", { action: "remove", listType: "consoleView", users: removeNames }, function(value)
    {
        $('#consoleViewWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#consoleViewWhitelistList").html(value);
        $("#consoleViewWhitelistRemoveSelectedSpan").text("");
    });

});