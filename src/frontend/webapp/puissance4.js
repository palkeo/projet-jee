var turn = 0;
function display()
{
    data = turns[turn];
    $("#game").empty();
    columns = data.length;
    lines = Math.max.apply(null, $.map(data, function(i) {return i.length}));

    for(var i = lines - 1; !(i < 0); i--)
    {
        var tr = $('<tr>');
        for(var j = 0; j < columns; j++)
        {
            var td = $('<td>');
            td.html((i < data[j].length) ? data[j][i] : '');
            tr.append(td);
        }
        $("#game").append(tr);
    }
}
function previous()
{
    if(turn > 0)
        turn = turn - 1;
    display();
}
function next()
{
    if(turn < turns.length - 1)
        turn = turn + 1;
    display();
}
$(display);
