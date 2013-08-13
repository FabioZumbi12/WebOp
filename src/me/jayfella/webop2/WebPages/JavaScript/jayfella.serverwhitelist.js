$("#serverWhitelistList").selectable({
    stop: function()
    {
        $("#serverWhitelistRemoveSelectedSpan").val('');

        var output = '';

        $( ".ui-selected", this ).each(function()
        {
            output = output + $(this).text() + ", ";
        });

        $("#serverWhitelistRemoveSelectedSpan").text(output);
    }
});

$(document).on('click', '#whitelistModeButton', function (e)
{
    var thisButton = this;

    $(thisButton).attr('disabled','disabled');
    var btnClass = ($(thisButton).attr("class") === "smallGreenButton") ? "enable" : "disable";

    $.post("whitelist.php", { action: "toggleWhitelistMode", mode: btnClass }, function(value)
    {
        switch (value)
        {
            case "true":
            {
                $(thisButton).attr("class","smallRedButton");
                $(thisButton).html("ON - Only whitelisted players can join");
                break;
            }
            case "false":
            {
                $(thisButton).attr("class","smallGreenButton");
                $(thisButton).html("OFF - Anybody can join");
                break;
            }
        }

        $(thisButton).removeAttr('disabled');
    });
});

$("#serverWhitelistAddPlayersButton").click(function ()
{
    var usersToAdd = $("#ServerWhitelistAddPlayersInput").val();

    $.post("whitelist.php", { action: "add", listType: "server", users: usersToAdd }, function(value)
    {
        $('#serverWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#serverWhitelistList").html(value);
    });
});

$("#serverWhitelistRemoveSelectedButton").click(function()
{
    var removeNames = $("#serverWhitelistRemoveSelectedSpan").text();

    $.post("whitelist.php", { action: "remove", listType: "server", users: removeNames }, function(value)
    {
        $('#serverWhitelistList > li').each(function (index, element)
        {
            $(element).remove();
        });

        $("#serverWhitelistList").html(value);
        $("#serverWhitelistRemoveSelectedSpan").text("");
    });

});