function parseMessageResponse(message)
{
    var messageData = message.split(";");
    
    var action = "";
    
    for (var i = 0; i < messageData.length; i++)
    {
        var messageDataSplit = messageData[i].split("=");
        
        if (messageDataSplit[0] === "action")
        {
            action = messageDataSplit[1];
            break;
        }
    }
    
    switch(action)
    {
        case "new":
        {
            var msgId = "";
            var msgUser = "";
            var msgTime = "";
            var msgPriority = "";
            var msgMessage = "";
            
            for (var i = 0; i < messageData.length; i++)
            {
                var dataSplit = messageData[i].split("=");
                
                if (dataSplit[0] === "id")
                    msgId = dataSplit[1];
                else if (dataSplit[0] === "user")
                    msgUser = dataSplit[1];
                else if (dataSplit[0] === "time")
                    msgTime = dataSplit[1];
                else if (dataSplit[0] === "priority")
                    msgPriority = dataSplit[1];
                else if (dataSplit[0] === "message")
                    msgMessage = dataSplit[1];
            }
            
            var priorityClass = "";
            
            if (msgPriority === "Low")
                priorityClass = "msgLowPriority";
            else if (msgPriority === "Medium")
                priorityClass = "msgMediumPriority";
            else if (msgPriority === "High")
                priorityClass = "msgHighPriority";
            
            msgMessage = decodeURIComponent(msgMessage);
            msgMessage = msgMessage.split("%20").join(" ");
            
            var msgBox = "<div class='msgBox " + priorityClass + "'><div class='msgDelete' title='permanently delete message'></div><span class='msgId'>" + msgId + "</span>Written by <span class='msgUser'>" + msgUser + "</span> on <span class='msgDate'>" + msgTime + "</span><br/><span class='msgMsg'>" + msgMessage + "</div>";
            
            $("#messageOutput").prepend(msgBox);
        }
        case "delete":
        {
            var msgId = "";
            
            for (var i = 0; i < messageData.length; i++)
            {
                var dataSplit = messageData[i].split("=");
                
                if (dataSplit[0] === "msgId")
                    msgId = dataSplit[1];
            }
            
            var foundElement;
            
            $(".msgId").each(function(index, element)
            {
                if ($(element).text() === msgId)
                {
                    foundElement = element;
                    return;
                }
            });
            
            $(foundElement).parent().remove();
        }
    }
}

function retrieveAllMessages()
{
    webopSocket.send(socketValidationString() + "&case=message&action=retrieve");
}

$("#messageSubmitButton").click(function()
{
    if ($("#messageInput").val().trim().length < 1)
        return;
    
    var validationString = socketValidationString();
    var msgPriority = $("#messagePriority option:selected").text();
    var msgText = $("#messageInput").val();
    
    var msg = validationString + "&case=message&action=create&msg=" + msgText + "&priority=" + msgPriority;
    
    webopSocket.send(msg);
    $("#messageInput").val("");
    $("#messagePriority").val('0');
});

$(document).on('click', '.msgDelete', function (e)
{
    var foundMessageId = $(this).parent().find(".msgId").text();

    var msg = socketValidationString() + "&case=message&action=delete&msgId=" + foundMessageId;
    webopSocket.send(msg);
});