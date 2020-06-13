
$(document).on("keypress", "#chatInput", function(e) 
{
    if (e.which === 13)
    {
        if ($(this).val().trim().length === 0)
            return;
         
        var msg = socketValidationString() + "&case=chat&msg=" + $(this).val().trim();
        webopSocket.send(msg);
        $(this).val("");
    }
});

function parseChatResponse(message)
{
    var user = "";
    var msg = "";
    var time = "";
    
    var chatData = message.split(";");
    
    for (var i = 0; i < chatData.length; i++)
    {
        var lineData = chatData[i].split("=");
        
        if (lineData[0] === "user")
        {
            user = lineData[1];
        }
        else if (lineData[0] === "msg")
        {
            msg = lineData[1];
        }
        else if (lineData[0] === "time")
        {
            time = lineData[1];
        }
    }
    
    msg = decodeURIComponent(msg);
    msg = msg.split("%20").join(" ");
    
    var boxedMessage = "<div class='chatBox'><span class='chatTime'>&nbsp;" + time + "&nbsp;-&nbsp;</span><span class='chatUser'>" + user + "</span> : <span class='chatMessage'>&nbsp;" + msg + "</div>";
    
    var elem = $("#chatOutput");
    
    var elemScrollHeight = elem[0].scrollHeight;
    var elemScrollTop = elem.scrollTop();
    var elemOuterHeight = elem.outerHeight();
    
    var atBottom = (elemScrollHeight - elemScrollTop <= elemOuterHeight);
    
    $("#chatOutput").append(boxedMessage);
    
    if (atBottom) $(elem).scrollTop($(elem)[0].scrollHeight);
}