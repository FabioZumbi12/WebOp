function parseActivityStatus(message)
{
    var user = message.substring(message.indexOf("user=")).replace("user=", "");
    var notifyElement = $("#activityNofitication");
    
    $("#activityNofiticationName").html(user);
    
    $(notifyElement).fadeIn("slow").delay(2000).fadeOut("slow");
}