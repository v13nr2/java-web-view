//First step check parameters mismatch and checking network connection if available call    download function
function DownloadFile(URL, Folder_Name, File_Name) {
//Parameters mismatch check
if (URL == null && Folder_Name == null && File_Name == null) {
	alert("error 1.");
    return;
}
else {
    //checking Internet connection availablity
    var networkState = navigator.connection.type;
    if (networkState == Connection.NONE) {
		alert("error 2.");
        return;
    } else {
		
		alert("step download.");
        download(URL, Folder_Name, File_Name); //If available download function call
    }
  }
}


//2
function download(URL, Folder_Name, File_Name) {
//step to request a file system 
    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, fileSystemSuccess, fileSystemFail);

function fileSystemSuccess(fileSystem) {
    var download_link = encodeURI(URL);
    ext = download_link.substr(download_link.lastIndexOf('.') + 1); //Get extension of URL

    var directoryEntry = fileSystem.root; // to get root path of directory
    directoryEntry.getDirectory(Folder_Name, { create: true, exclusive: false }, onDirectorySuccess, onDirectoryFail); // creating folder in sdcard
    var rootdir = fileSystem.root;
    var fp = rootdir.fullPath; // Returns Fulpath of local directory

    fp = fp + "/" + Folder_Name + "/" + File_Name + "." + ext; // fullpath and name of the file which we want to give
    // download function call
    filetransfer(download_link, fp);
}

function onDirectorySuccess(parent) {
    // Directory created successfuly
	alert("Sukses membuat direktori");
}

function onDirectoryFail(error) {
    //Error while creating directory
    alert("Unable to create new directory: " + error.code);
}

  function fileSystemFail(evt) {
    //Unable to access file system
    alert(evt.target.error.code);
 }
}