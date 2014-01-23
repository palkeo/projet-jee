var size = 5;
var width = 100/size;
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
            var td = $('<td width="' + width + '%" style="text-align:center">');

            switch(data[i][j]) {
                case 0:
                    td.html('O');
                    break;
                case 1:
                    td.html('X');
                    break;
                default:
                    td.html('&nbsp;');
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
