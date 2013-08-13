$("#webopWhitelistList").selectable({
    stop: function()
    {
        $("#webopWhitelistRemoveSelectedSpan").val('');

        var output = '';

        $( ".ui-selected", this ).each(function()
        {
            output = output + $(this).text() + ", ";
        });

        $("#webopWhitelistRemoveSelectedSpan").text(output);
    }
});

$("#webopWhitelistAddPlayersButton").click(function ()
{
    var usersToAdd = $("#webopWhitelistAddPlayersInput").val();

    $.post("whitelist.php", { action: "add", listType: "webop", users: usersToAdd }, function(value)
    {
        $('#webopWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#webopWhitelistList").html(value);
    });
});

$("#webopWhitelistRemoveSelectedButton").click(function()
{
    var removeNames = $("#webopWhitelistRemoveSelectedSpan").text();

    $.post("whitelist.php", { action: "remove", listType: "webop", users: removeNames }, function(value)
    {
        $('#webopWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#webopWhitelistList").html(value);
        $("#webopWhitelistRemoveSelectedSpan").text("");
    });

});