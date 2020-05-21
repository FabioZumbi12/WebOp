$('.playerSearchInput').on('input', function()
{
    var inputElement = this;
    var plSearchTerm = $(inputElement).val();

    if (plSearchTerm.length > 0)
    {
        $.post("data.php", { case: "playerNameSearch", partialName: plSearchTerm },
            function(value)
            {
                var foundNames = value.split(",");
                foundNames.sort();

                $(inputElement)
                    .autocomplete({ 
                        source: foundNames, 
                        delay: 300,
                        focus: function( event, ui ) 
                            { 
                                $(inputElement).val( $(ui.item.label).text() );
                                return false; 
                            },
                        select: function( event, ui ) {
                                $(inputElement).val($(ui.item.label).text());
                                return false;
                            }
                    })
                    .data("ui-autocomplete")._renderItem = function(ul, item)
                    {
                        return $("<li>")
                                .append("<a>" + item.value + "</a>")
                                .appendTo(ul);
                    };
            }
        );
    }
});
