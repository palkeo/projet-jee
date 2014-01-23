var size = 5;
var turn = 0;
function display()
{
    data = turns[turn];
    $("#game").empty();

    for(var i = 0; i < size; i++)
    {
        var tr = $('<tr>');
        for(var j = 0; j < size; j++)
        {
            var td = $('<td style="text-align:center">');

            switch(data[i*size+j]) {
                case -1:
                    td.html('&nbsp;');
                    break;
                case 0:
                    td.html('X');
                    break;
                case 1:
                    td.html('X');
                    break;
            }

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
