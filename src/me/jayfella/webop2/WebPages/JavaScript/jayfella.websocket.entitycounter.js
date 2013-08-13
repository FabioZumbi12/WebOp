function processSocketEntityResponse(value)
{
    value = value.replace("ENTITIES : ", "");
    var entityTypes = value.split(",");

    for (var i = 0; i < entityTypes.length; i++)
    {
        entityTypes[i] = entityTypes[i].trim();

        if (entityTypes[i] === "")
            continue;

        var entityName = entityTypes[i].substring(0, entityTypes[i].indexOf("=")).trim();
        var entityCount = entityTypes[i].substring(entityTypes[i].indexOf("=") + 1).trim();

        switch(entityName)
        {
            case "ARROW":
            {
                $('#arrowVal').text(entityCount);
                break;
            }
            case "BAT":
            {
                $('#batVal').text(entityCount);
                break;
            }
            case "BLAZE":
            {
                $('#blazeVal').text(entityCount);
                break;
            }
            case "BOAT":
            {
                $('#boatVal').text(entityCount);
                break;
            }
            case "CAVE_SPIDER":
            {
                $('#cavespiderVal').text(entityCount);
                break;
            }
            case "CHICKEN":
            {
                $('#chickenVal').text(entityCount);
                break;
            }
            case "COW":
            {
                $('#cowVal').text(entityCount);
                break;
            }
            case "CREEPER":
            {
                $('#creeperVal').text(entityCount);
                break;
            }
            case "DROPPED_ITEM":
            {
                $('#droppeditemVal').text(entityCount);
                break;
            }
            case "EGG":
            {
                $('#eggVal').text(entityCount);
                break;
            }
            case "ENDER_DRAGON":
            {
                $('#enderdragonVal').text(entityCount);
                break;
            }
            case "ENDERMAN":
            {
                $('#endermanVal').text(entityCount);
                break;
            }
            case "ENDER_PEARL":
            {
                $('#enderpearlVal').text(entityCount);
                break;
            }
            case "EXPERIENCE_ORB":
            {
                $('#experienceorbVal').text(entityCount);
                break;
            }
            case "GHAST":
            {
                $('#ghastVal').text(entityCount);
                break;
            }
            case "HORSE":
            {
                $('#horseVal').text(entityCount);
                break;
            }
            case "IRON_GOLEM":
            {
                $('#irongolemVal').text(entityCount);
                break;
            }
            case "ITEM_FRAME":
            {
                $('#itemframeVal').text(entityCount);
                break;
            }
            case "MAGMA_CUBE":
            {
                $('#magmacubeVal').text(entityCount);
                break;
            }
            case "MINECART":
            {
                $('#minecartVal').text(entityCount);
                break;
            }
            case "MINECART_CHEST":
            {
                $('#minecartchestVal').text(entityCount);
                break;
            }
            case "MINECART_FURNACE":
            {
                $('#minecartfurnaceVal').text(entityCount);
                break;
            }
            case "MINECART_HOPPER":
            {
                $('#minecarthopperVal').text(entityCount);
                break;
            }
            case "MINECART_MOB_SPAWNER":
            {
                $('#minecartmobspawnerVal').text(entityCount);
                break;
            }
            case "MINECART_TNT":
            {
                $('#minecarttntVal').text(entityCount);
                break;
            }
            case "PAINTING":
            {
                $('#paintingVal').text(entityCount);
                break;
            }
            case "UNKNOWN":
            {
                $('#unknownVal').text(entityCount);
                break;
            }
            case "MUSHROOM_COW":
            {
                $('#mushroomcowVal').text(entityCount);
                break;
            }
            case "OCELOT":
            {
                $('#ocelotVal').text(entityCount);
                break;
            }
            case "PIG":
            {
                $('#pigVal').text(entityCount);
                break;
            }
            case "PIG_ZOMBIE":
            {
                $('#pigzombieVal').text(entityCount);
                break;
            }
            case "SKELETON":
            {
                $('#skeletonVal').text(entityCount);
                break;
            }
            case "SHEEP":
            {
                $('#sheepVal').text(entityCount);
                break;
            }
            case "SILVERFISH":
            {
                $('#silverfishVal').text(entityCount);
                break;
            }
            case "SNOWMAN":
            {
                $('#snowmanVal').text(entityCount);
                break;
            }
            case "SLIME":
            {
                $('#slimeVal').text(entityCount);
                break;
            }
            case "SQUID":
            {
                $('#squidVal').text(entityCount);
                break;
            }
            case "SPIDER":
            {
                $('#spiderVal').text(entityCount);
                break;
            }
            case "VILLAGER":
            {
                $('#villagerVal').text(entityCount);
                break;
            }
            case "WITCH":
            {
                $('#witchVal').text(entityCount);
                break;
            }
            case "WITHER":
            {
                $('#witherVal').text(entityCount);
                break;
            }
            case "WOLF":
            {
                $('#wolfVal').text(entityCount);
                break;
            }
            case "ZOMBIE":
            {
                $('#zombieVal').text(entityCount);
                break;
            }
        }
    }
}

$("#countEntitiesButton").click( function()
{
    $('#entitiCountContainer').css('visibility', 'visible');
    $('#entitiCountContainer').css('padding-top', '10px');

    ws.send(constructSocketMessage("ENTITIES"));
});

$("#clearEntityCountButton").click( function()
{
    $('#entitiCountContainer').css('visibility', 'collapse');
    $('#entitiCountContainer').css('padding-top', '0px');
});