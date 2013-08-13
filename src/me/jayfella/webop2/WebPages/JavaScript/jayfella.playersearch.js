
$('.playerSearchInput').on('input', function()
{
    var inputElement = this;
    var plSearchTerm = $(inputElement).val();

    if (plSearchTerm.length > 0)
    {
        $.get("playersearch.php", { partialName: plSearchTerm },
            function(value)
            {
                var foundNames = value.split(",");
                foundNames.sort();

                $(inputElement).autocomplete({ source: foundNames, delay: 300 });
            }
        );
    }
});

