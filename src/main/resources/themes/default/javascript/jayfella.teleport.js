$(document).on('click', '.teleportButton', function()
{
    var elemClass = $(this).attr('class');
    
    var xDom = $(this).nextAll(".xCoord").text();
    var yDom = $(this).nextAll(".yCoord").text();
    var zDom = $(this).nextAll(".zCoord").text();
    var wDom = $(this).nextAll(".wCoord").text();
    
    if (yDom === "") yDom = "65";
    
    var msg = (elemClass.indexOf("mcPlayer") === -1)
        ? socketValidationString() + "&case=teleport&action=coord&x=" + xDom + "&y=" + yDom + "&z=" + zDom + "&w=" + wDom
        : "&case=teleport&action=player&to=" + $(this).text();
    
    webopSocket.send(socketValidationString() + msg);
});
