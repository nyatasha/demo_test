/**
 * Created by Natallia on 23.05.2017.
 */
// var myList=[{"name" : "abc", "age" : 50},
//     {"age" : "25", "hobby" : "swimming"},
//     {"name" : "xyz", "hobby" : "programming"}];

// Builds the HTML Table out of myList json data from Ivy restful service.
function buildHtmlTable(myList1) {
    var myList = JSON.parse(myList1);
    var columns = addAllColumnHeaders(myList);

    for (var i = 0 ; i < myList.length ; i++) {
        var row$ = $('<tr/>');
        for (var colIndex = 0 ; colIndex < columns.length ; colIndex++) {
            var cellValue = myList[i][columns[colIndex]];

            if (cellValue == null) { cellValue = ""; }
            row$.append($('<td/>').html(cellValue));
        }
        row$.append('<td><input type="button" class="btn btn-info"value="Delete Row" onclick="SomeDeleteRowFunction(this);" ></td>');
        $("#excelDataTable").append(row$);
    }
}
function SomeDeleteRowFunction(btndel) {
    if (typeof(btndel) == "object") {
        $(btndel).closest("tr").remove();
    } else {
        return false;
    }
}
// Adds a header row to the table and returns the set of columns.
// Need to do union of keys from all records as some records may not contain
// all records
function addAllColumnHeaders(myList)
{
    var columnSet = [];
    var headerTr$ = $('<tr/>');

    for (var i = 0 ; i < myList.length ; i++) {
        var rowHash = myList[i];
        for (var key in rowHash) {
            if ($.inArray(key, columnSet) == -1){
                columnSet.push(key);
                headerTr$.append($('<th/>').html(key));
            }
        }
    }
    $("#excelDataTable").append(headerTr$);

    return columnSet;
}

function saveTable() {
    var table = $('#excelDataTable').tableToJSON(); // Convert the table into a javascript object
    $.ajax({
        url:'/save',
        type:'POST',
        data: JSON.stringify(table),
        beforeSend: function(xhr) {
            xhr.setRequestHeader("Accept", "application/json");
            xhr.setRequestHeader("Content-Type", "application/json");
        }
    });
    $('#myModal').modal('show');
}
function insertRow(){
    var size = document.getElementById('excelDataTable').rows[0].cells.length;
    for (i=0;i<size;i++){
        var input = document.createElement("input");
        input.type = "text";
        input.id="cell"+i;
        input.class="myrow";
        document.getElementById('insert-body').appendChild(input);
    }
    $('#insert-body').append('<button class="btn btn-info" id="closebtn" onclick="closeModal()" style = "margin: 0 auto;" ">Готово!</button>');
    $('#insertModal').modal('show');
}

function closeModal(){
    var size = document.getElementById('excelDataTable').rows[0].cells.length;
    var table = document.getElementById('excelDataTable');
    var row$ = $('<tr/>');
    for (var colIndex = 0 ; colIndex < size ; colIndex++) {
        var cellValue =  document.getElementById("cell"+colIndex).value;
        if (cellValue == null) { cellValue = ""; }
        row$.append($('<td/>').html(cellValue));
    }
    row$.append('<td><input type="button" class="btn btn-info"value="Delete Row" onclick="SomeDeleteRowFunction(this);" ></td>');
    $("#excelDataTable").append(row$);
    $('#insertModal').modal('toggle');
    for (i=0;i<size;i++){
        document.getElementById("cell"+i).remove();
    }
    document.getElementById("closebtn").remove();
}
