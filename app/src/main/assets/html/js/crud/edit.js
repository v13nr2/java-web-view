function edit(p,f1,f2){
 // alert('id : '+p);
  window.location.href = "./views/user/edit.html?cmd=edit&id="+p;
  return false;
}

function back(){
	window.location.href = '../../lokal.html';
}

function update(){	
  
 if (!window.openDatabase) {
  alert('Databases are not supported in this browser.');
  return;
 }



 db = openDatabase(shortName, version, displayName,maxSize);



 id =  getAllUrlParams().id != "" ? getAllUrlParams().id : "";
 SQL = "UPDATE User SET FirstName = '" + $('#namadepan').val() + "', LastName = 'blob:file:///c7256d80-707c-48d7-a252-f83bce82cb8d'";
    SQL = SQL + " WHERE User.UserId = "+id+" ";
	alert("Update Berhasil");
	
 db.transaction(function(tx){


   tx.executeSql( SQL,
[],nullHandler,errorHandler);
 },errorHandler,successCallBack);
	return false;


}

function camera(p,f1,f2){
 // alert('id : '+p);
  window.location.href = "./gallery.html?cmd=camera&id="+p;
  return false;
}


function ListDBValuesEdit() {

  
 if (!window.openDatabase) {
  alert('Databases are not supported in this browser.');
  return;
 }



 db = openDatabase(shortName, version, displayName,maxSize);



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
         sTable = "<tr><td align='center'>#</td><td>Nama Depan</td><td>Nama Belakang</td></tr>"; 
        for (var i = 0; i < result.rows.length; i++) {
          var row = result.rows.item(i);
          sTable += '<tr><td align="center"><a href="#" onClick="back()"><img src="../../img/back.png" height="23px"></a>&nbsp;<a href="#" onClick="update(' + row.UserId+')"><img src="../../img/save.png" height="23px"></a>&nbsp;&nbsp;<a href="#" onClick="delete(' + row.UserId+')"><img src="../../img/delete.png" height="23px"></a>&nbsp;<a href="#" onClick="camera(' + row.UserId+')"><img src="../../img/camera.gif" height="25px"></a></td><td><input type="text" style="width: 100%;" id="namadepan"  value="' + row.FirstName+'"></td><td><img style="width: 100%;"   src="' + row.LastName+'"></td></tr>';  
        }
        $('#tableUsers').append(sTable);
      }
     },errorHandler);
 },errorHandler,nullHandler);

 return;

}