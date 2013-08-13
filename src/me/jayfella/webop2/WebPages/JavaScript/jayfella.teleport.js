function processTeleportResponse(value)
{
    switch (value)
    {
        case "OK":
        {
            alert("Teleport successful!");
            break;
        }
        case "NOT_LOGGED_IN":
        {
            alert("You must be logged in to teleport!");
            break;
        }
        case "CHUNK_UNLOADED":
        {
            alert("Error: The chunk is no longer loaded. Unable to teleport.");
            break;
        }
    }
}


$(document).on('click', '.teleportButton', function ()
{

    var xDom = $(this).next("span");
    var zDom = $(xDom).next("span");
    var wDom = $(zDom).next("span");

    $.post("teleport.php",
        {
            x: $(xDom).text(),
            y: 65,
            z : $(zDom).text(),
            world: $(wDom).text()
        }, processTeleportResponse);
});