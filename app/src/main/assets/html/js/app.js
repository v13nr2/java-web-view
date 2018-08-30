var latlong;
var gambar;
var resultDiv;

        document.addEventListener("deviceready", onDeviceReady, false);

        function onDeviceReady() {
			document.querySelector("#startCam").addEventListener("touchend", startCam, false);
			resultDiv = document.querySelector("#results");
			GLocDiv = document.querySelector("#GLoc");
        }

		
function startScan() {

	cordova.plugins.barcodeScanner.scan(
		function (result) {
			var s = "Result: " + result.text + "<br/>" +
			"Format: " + result.format + "<br/>" +
			"Cancelled: " + result.cancelled;
			resultDiv.innerHTML = s;
		}, 
		function (error) {
			alert("Scanning failed: " + error);
		}
	);
	
	
}

function startCam() {

			if(confirm('Apakah yakin akan mengambil gambar?')){
            navigator.camera.getPicture(uploadPhoto,
                                        function(message) { alert('get picture failed'); },
                                        { quality: 50, 
                                        destinationType: navigator.camera.DestinationType.FILE_URI,
                                        sourceType: navigator.camera.PictureSourceType.CAMERA,
										MediaType: Camera.MediaType.ALLMEDIA}
                                        );
										
										
			$("#pesan").html("<font color='red'>Foto Sedang di Proses. Harap Tunggu Sebentar.</font>");
			
	
			}
			
}


function startVCam() {

			navigator.device.capture.captureVideo(uploadPhoto, function(message) { alert('get video failed'); }, {limit: 20});
}


function recupImage(imageURI) {
    window.resolveLocalFileSystemURI(imageURI, copiePhoto, fail);    
}

function copiePhoto(fileEntry) {
    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function(fileSys) { 
        fileSys.root.getDirectory("PKPD", {create: true, exclusive: false}, function(dir) { 
                fileEntry.copyTo(dir, fileEntry.name, onCopySuccess, fail); 
            }, fail); 
    }, fail); 
}



function movePic(file){ 
    window.resolveLocalFileSystemURI(file, resolveOnSuccess, resOnError); 
} 


function onCopySuccess(entry) {
    alert('image telah di salin ke  : ' + entry.toURL());  //nng path image
}

function fail(error) {
    alert(error.code);
}


        function fail(error) {
            alert("An error has occurred: Code = " + error.code);
            alert("upload error source " + error.source);
            alert("upload error target " + error.target);
			
        }

function win(r) {
            alert("Code = " + r.responseCode);
            alert("File dengan " + r.response + " Berhasil di Upload.");
			var str = r.response;
			ling = String(str);
			var n = str.search("TIDAK"); 
			if(r.response == " sesi yang Anda gunakan tidak bisa diupload. Silakan LOGIN terlebih dahulu. TIDAK"){
				opsi_view();
				return false;
			} else {
				localStorage.setItem("gambarStorage", r.response);
				window.location = 'isian.html'; 
			}
			gambar = r.response;
        }

        function uploadPhoto(imageURI) {
			
			var smallImage = document.getElementById('smallImage')
			smallImage.style.display = 'block';
			smallImage.src = imageURI;
			
            var options = new FileUploadOptions();
            options.fileKey="file";
            options.fileName=imageURI.substr(imageURI.lastIndexOf('/')+1);
            options.mimeType="image/jpeg";

            var params = {};
            params.kodebarang = 'Nanang R :: 08 123 540 1617';  

            options.params = params;
			
			//alert(imageURI);
			//recupImage(imageURI);
			movePic(imageURI);
            var ft = new FileTransfer();
            ft.upload(imageURI, encodeURI("http://asetkonawe.lpkpd.org/smc/upload_touch.php?latlong=qq&judul=007&keterangan=008"), win, fail, options);
			
	}

        
		
		function simpanKebersihan(){
			jenisbarang = $('#jenisbarang').val(); 
            kodebarang = $('#kodebarang').val(); 
            identitasbarang = $('#identitasbarang').val(); 
            asalusul = $('#asalusul').val(); 
            tanggal_asset = $('#tanggal').val(); 
            keterangan = $('#keterangan').val(); 
			if(!jenisbarang || !kodebarang || !identitasbarang || !asalusul || !tanggal || !keterangan){
				alert("Harus mengisi semua Field");
				return false;
			}
			$.ajax({
				type	: "POST",
				url		: "http://asetkonawe.lpkpd.org/smc/upload_text.php?gambar="+gambar,
				data	: "jenisbarang="+jenisbarang+"&kodebarang="+kodebarang+"&identitasbarang="+identitasbarang+"&asalusul="+asalusul+"&tanggal="+tanggal+"&keterangan="+keterangan,
				success	: function(data){
					alert(data);
					opsi_view();
				}
			});
		}

		function opsi_view(){
			$("#pesan").html("");
		}

		
		
		
		//Callback function when the file system uri has been resolved
function resolveOnSuccess(entry){ 
    var d = new Date();
    var n = d.getTime();
    //new file name
    var newFileName = n + ".jpg";
    var myFolderApp = "pkpd";

    window.requestFileSystem(LocalFileSystem.PERSISTENT, 0, function(fileSys) {      
    //The folder is created if doesn't exist
    fileSys.root.getDirectory( myFolderApp,
                    {create:true, exclusive: false},
                    function(directory) {
                        entry.moveTo(directory, newFileName,  successMove, resOnError);
                    },
                    resOnError);
                    },
    resOnError);
}

//Callback function when the file has been moved successfully - inserting the complete path
function successMove(entry) {
    //Store imagepath in session for future use
    // like to store it in database
    sessionStorage.setItem('imagepath', entry.fullPath);
}

function resOnError(error) {
    alert(error.code);
}