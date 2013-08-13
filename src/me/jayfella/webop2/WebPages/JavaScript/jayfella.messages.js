
$("#newMessageButton").click( function() { $('div#messageDialog').dialog('open'); });

$("#createMessageButton").click( function()
{
    var msgContents = $("#messageContents").val();

    $('div#messageDialog').dialog('close');

    var msgUsername = $.cookie("webop_user");

    $.get("messages.php", { state: "create", newMessageUser: msgUsername, newMessagePriority: "HIGH", newMessageText: msgContents });
});

$(document).on('click', '.alertUlClose', function (e)
{
    var foundMessageId = $(this).parent().find(".messageId").text();

    var msgIdIndex = currentMessageIds.indexOf(foundMessageId);
    currentMessageIds.splice(msgIdIndex, 1);

    $.get("messages.php", { state: "delete", deleteId: foundMessageId });

    $(e.target.parentElement).fadeOut("normal", function() { $(this).remove(); });
});

var currentMessageIds = [];

function parseNewMessageResponse(data)
{
    $("#moderatorMessages").prepend(data);
}

function parseMessageResponse(data)
{
    var delIds = [];
    var newIds = [];

    $.each(data, function(index)
    {
        var id = data[index].id;
        var state = data[index].state;

        switch (state)
        {
            case "NEW":
            {
                newIds.push(id);
                break;
            }
            case "DELETE":
            {
                delIds.push(id);
                break;
            }
        }
    });

    for (var i = 0; i < delIds.length; i++)
    {
        var arrIndex = currentMessageIds.indexOf(delIds[i]);
        currentMessageIds.splice(arrIndex, 1);

        $('#moderatorMessages > ul').each(function (index, element)
        {
            var thisMsgId = $(element).find(".messageId").text();

            if (thisMsgId === delIds[i])
            {
                $(element).fadeOut("normal", function() { $(this).remove(); });
            }

        });
    }

    for (var i = 0; i < newIds.length; i++)
    {
        currentMessageIds.push(newIds[i]);
    }

    var messagesToGet = "";

    for (var i = 0; i < newIds.length; i++)
    {
        messagesToGet += newIds[i];

        if (i < newIds.length - 1)
            messagesToGet += ",";
    }

    $.get("messages.php", { state: "retrieve", retrieveIds: messagesToGet }, parseNewMessageResponse);

    setTimeout(messagesPoller, 5000);
}

var messagesPoller = function()
{
    var messageIds = "";

    for (var i = 0; i < currentMessageIds.length; i++)
    {
        messageIds += currentMessageIds[i];

        if (i < currentMessageIds.length - 1)
            messageIds += ",";
    }

    $.getJSON("messages.php", { state: "check", currentIds: messageIds }, parseMessageResponse);
};

messagesPoller();