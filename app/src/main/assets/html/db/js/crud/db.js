
// global variables
var db;
var shortName = 'AssetSqlDB';
var version = '1.0';
var displayName = 'AssetSqlDB';
var maxSize = 65535;

// this is called when an error happens in a transaction
function errorHandler(transaction, error) {
   alert('Error: ' + error.message + ' code: ' + error.code);

}

// this is called when a successful transaction happens
function successCallBack() {
   
   //alert("DEBUGGING: success");

}

function nullHandler(){};



function ListDBValues() {

 if (!window.openDatabase) {
  alert('Databases are not supported in this browser.');
  return;
 }

// this line clears out any content in the #lbUsers element on the

// content and not just keep repeating lines
 $('#lbUsers').html('');
 $('#tableUsers').empty();
 sTable = "";

 id =  getAllUrlParams().id != "" ? getAllUrlParams().id : "";
 SQL = "SELECT * FROM User ";
 if(id){
  if(id!=""){
    SQL = SQL + " WHERE User.UserId = "+id+" ";
  }
 }
 SQL = SQL + "ORDER BY FirstName";
 db.transaction(function(transaction) {
   transaction.executeSql(SQL, [],
     function(transaction, result) {
      if (result != null && result.rows != null) {
         sTable = "<tr><td align='center'>#</td><td align='center'>No</td><td>Nama Depan</td><td>Nama Belakang</td></tr>"; 
         no = 1;
        for (var i = 0; i < result.rows.length; i++) {
          var row = result.rows.item(i);
         // $('#lbUsers').append('<br>' + row.UserId + '. ' + row.FirstName+ ' ' + row.LastName);
          sTable += '<tr><td align="center"><a href="#" onClick="edit(' + row.UserId+')"><img src="img/edit.gif"></a></td><td align="center">'+ no +'</td><td>' + row.FirstName+'</td><td>' + row.LastName+'</td></tr>';  
          no++;
        }
        $('#tableUsers').append(sTable);
      }
     },errorHandler);
 },errorHandler,nullHandler);

 return;

}


function onBodyLoad(){


 if (!window.openDatabase) {

   alert('Databases are not supported in this browser.');

   ListDBValues();
   return;
 }



 db = openDatabase(shortName, version, displayName,maxSize);

// this line will try to create the table User in the database just

 //tx.executeSql( 'DROP TABLE User',nullHandler,nullHandler);
 db.transaction(function(tx){
 //tx.executeSql( 'DROP TABLE User',nullHandler,nullHandler);
 alert("db droped 1");
   tx.executeSql( 'CREATE TABLE IF NOT EXISTS User(UserId INTEGER NOT NULL PRIMARY KEY, FirstName TEXT NOT NULL, LastName TEXT NOT NULL)',
[],nullHandler,errorHandler);
 },errorHandler,successCallBack);


ListDBValues();

}


function AddValueToDB() {

 if (!window.openDatabase) {
   alert('Databases are not supported in this browser.');
   return;
 }



 db.transaction(function(transaction) {
   transaction.executeSql('INSERT INTO User(FirstName, LastName) VALUES (?,?)',[$('#txFirstName').val(), $('#txLastName').val()],
     nullHandler,errorHandler);
   });

// this calls the function that will show what is in the User table in

 ListDBValues();

 return false;

}