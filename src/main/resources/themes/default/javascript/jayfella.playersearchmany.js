function split(val) { return val.split( /,\s*/ ); }
function extractLast( term ) { return split( term ).pop(); }

$(".manyPlayersSearchInput")
    .bind("keydown", function(event)
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
            $.post("data.php", { case: "playerNameSearch", partialName: extractLast(request.term) },
                function(value)
                {
                    var foundNames = value.split(",");
                    foundNames.sort();

                    response($.ui.autocomplete.filter(foundNames, extractLast(request.term)));
                });

        },
        // focus: function() { return false; },
        
        select: function(event, ui)
        {
            var terms = split(this.value);
            terms.pop();
            terms.push($(ui.item.value).text());
            terms.push("");
            this.value = terms.join(", ");

            return false;
        }
    })
    .data("ui-autocomplete")._renderItem = function(ul, item)
    {
        return $("<li>")
                .append("<a>" + item.value + "</a>")
                .appendTo(ul);
    };