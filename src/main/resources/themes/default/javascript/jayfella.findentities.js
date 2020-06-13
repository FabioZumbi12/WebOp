$("#entitiesInChunkSpinner").spinner({ readOnly: true, min: 1 });

$("#entitiesExpand").click(function()
{
    $(this).toggleClass("ui-icon ui-icon-circle-triangle-s");
    $(this).toggleClass("ui-icon ui-icon-circle-triangle-n");

    $("#entitiesControls").slideToggle();
});

function split(val) { return val.split( /,\s*/ ); }
function extractLast( term ) { return split( term ).pop(); }

$("#entitiesInChunkEntitiesClearButton").click(function()
{
    $("#entitiesInChunkSpinner").val(10);
    $("#entitiesInChunkEntities").val("");
    $("#findEntitiesResult").html("");
});

$("#submitEntitiesInChunkButtun").click((function()
{
    var entityCount = $("#entitiesInChunkSpinner").val();
    var entities = $("#entitiesInChunkEntities").val();
    
    $.post("data.php", { case: "findEntities", count: entityCount, types: entities }, parseEntitiesResponse);
}));

function parseEntitiesResponse(message)
{
    if (message === "")
        return;
    
    var msg = "<br/>" + message;
    $("#findEntitiesResult").html(msg);
}

$("#entitiesInChunkEntities")
    .bind("keydown", function(event )
    {
        if (event.keyCode === $.ui.keyCode.TAB && $(this).data("ui-autocomplete").menu.active)
        {
            event.preventDefault();
        }
    })
    .autocomplete(
    {
        minLength: 0,
        source: function(request, response)
        {
            response($.ui.autocomplete.filter(entityTypes, extractLast(request.term)));
        },
        focus: function() { return false; },
        select: function(event, ui)
        {
            var terms = split(this.value);
            terms.pop();
            terms.push(ui.item.value);
            terms.push("");
            this.value = terms.join(", ");

            return false;
        }
    });

var entityTypes =
        [
            "Arrow",
            "Bat",
            "Blaze",
            "Boat",
            "Cave Spider",
            "Chicken",
            "Complex Part",
            "Cow",
            "Creeper",
            "Dropped Item",
            "Egg",
            "Ender Crystal",
            "Ender Dragon",
            "Ender Pearl",
            "Ender Signal",
            "Enderman",
            "Experience Orb",
            "Falling Block",
            "Fireball",
            "Firework",
            "Fishing Hook",
            "Ghast",
            "Giant",
            "Iron Golem",
            "Item Frame",
            "Lightning",
            "Magma Cube",
            "Minecart",
            "Minecart Chest",
            "Minecart Furnace",
            "Minecart Hopper",
            "Minecart Mob Spawner",
            "Minecart TNT",
            "Mushroom Cow",
            "Ocelot",
            "Painting",
            "Pig",
            "Pig Zombie",
            "Player",
            "Primed TNT",
            "Sheep",
            "Silverfish",
            "Skeleton",
            "Slime",
            "Small Fireball",
            "Snowball",
            "Snowman",
            "Spider",
            "Splash Potion",
            "Squid",
            "Thrown Exp Bottle",
            "Unknown",
            "Villager",
            "Weather",
            "Witch",
            "Wither",
            "Wither Skull",
            "Wolf",
            "Zombie"
        ];