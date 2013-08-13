$("#entitiesInChunkSpinner").spinner({ readOnly: true, min: 1 });

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

function split(val) { return val.split( /,\s*/ ); }
function extractLast( term ) { return split( term ).pop(); }

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

function processEntitiesInChunkResponse(value)
{
    $("#entitiesInChunkResult").html(value);
    $('#entitiesInChunkResult').css('padding-top', '10px');
}

$("#entitiesInChunkEntitiesClearButton").click(function()
{
    $("#entitiesInChunkResult").html("");
    $('#entitiesInChunkResult').css('padding-top', '0px');
});

$("#entitiesInChunkAllEntities").click(function()
{
    var thisCheck = $(this);

    if (thisCheck.is(':checked'))
    {
        $('#entitiesInChunkEntities').attr('disabled','disabled');
        $('#entitiesInChunkEntities').val("");
    }
    else
    {
        $('#entitiesInChunkEntities').removeAttr('disabled');
    }
});

// submitEntitiesInChunkButtun
$("#submitEntitiesInChunkButtun").click(function()
{
    $.post("profileentities.php",
        {
            all: $('#entitiesInChunkAllEntities').is(':checked') ? "true" : "false",
            entities: $("#entitiesInChunkEntities").val(),
            type : "findChunks",
            amount: $("#entitiesInChunkSpinner").val()
        }, processEntitiesInChunkResponse);
});
