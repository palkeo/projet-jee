var turn = 0;
function display()
{
    data = turns[turn];
    $("#game").empty();
    columns = data.length;

    for(var i = 6-1; !(i < 0); i--)
    {
        var tr = $('<tr>');
        for(var j = 0; j < columns; j++)
        {
            var td = $('<td width="14.28%" style="text-align:center">');

            if(i < data[j].length)
                td.html(data[j][i] == 1 ? 'X' : 'O');
            else
                td.html('&nbsp;');

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
