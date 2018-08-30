package nanang.application.id.libs;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import nanang.application.id.iad.BuildConfig;
import nanang.application.id.iad.R;
import nanang.application.id.model.remember;
import nanang.application.id.model.user;
import nanang.application.id.model.version;

public final class CommonUtilities {
	
	public static final String TAG = "IAD";
	public static final String API_KEY ="DF8B172486A95F97FE38D4FA242948F1";
	public static final String SERVER_HOME_URL = "http://sogi-online.com/android";
	public static final String SERVER_HOME_API = "https://slc.semutsoft-trucking.id";
	public  static final String LOGIN_URL = SERVER_HOME_API + "/api/user/request_token";
	public static final String UPLOAD_URL = SERVER_HOME_URL+"/android/upload_image/upload.php";
	public static String toKEN;

	public static  String nama_file_upload = "";



	/**
	 * Function to change progress to timer
	 * @param progress -
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public static Integer progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);

		// return current duration in milliseconds
		return currentDuration * 1000;
	}


	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public static Integer getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;

		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;

		// return percentage
		return percentage.intValue();
	}

	public static boolean getCurentlyTracking(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getBoolean("currentlyTracking", false);
	}

	public static void setCurentlyTracking(Context context, boolean currentlyTracking) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putBoolean("currentlyTracking", currentlyTracking);

		editor.commit();
	}

	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public static String milliSecondsToTimer(long milliseconds) {
		String finalTimerString = "";
		String secondsString = "";

		// Convert total duration into time
		int hours = (int)( milliseconds / (1000*60*60));
		int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		// Add hours if there
		if(hours > 0){
			finalTimerString = hours + ":";
		}

		// Prepending 0 to seconds if it is one digit
		if(seconds < 10){
			secondsString = "0" + seconds;
		}else{
			secondsString = "" + seconds;}

		finalTimerString = finalTimerString + minutes + ":" + secondsString;

		// return timer string
		return finalTimerString;
	}

	public static version getAppVersion(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String version_name = prefs.getString("version_name", "");
		String version_no = prefs.getString("version_no", "");

		if(version_name.length()==0 && version_no.length()==0) {
			version_name = BuildConfig.VERSION_NAME;
			version_no = String.valueOf(BuildConfig.VERSION_CODE);

			Editor editor = prefs.edit();
			editor.putString("version_name", version_name);
			editor.putString("version_no", version_no);
		}

		return new version(version_no, version_name);
	}

	public static void setGuestId(Context context, int guest_id) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();

		editor.putInt("guest_id", guest_id);
		editor.commit();
	}

	public static int getGuestId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getInt("guest_id", 0);
	}

	public static void setRememberPassword(Context context, remember data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();

		editor.putString("email_remember", data.getEmail());
		editor.putString("password_remember", data.getPassword());
		editor.commit();
	}

	public static remember getRememberPassword(Context context) {
		SharedPreferences data = PreferenceManager.getDefaultSharedPreferences(context);
		return new remember(
				data.getString("email_remember", ""),
				data.getString("password_remember", "")
		);
	}

	public static void setLoginUser(Context context, user data) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();

		editor.putInt("id", data.getId());
		editor.putString("nama", data.getNama());
		editor.putString("email", data.getEmail());
		editor.putString("kecamatan", data.getKecamatan());
		editor.putString("kabupaten", data.getKabupaten());
		editor.putString("propinsi", data.getPropinsi());
		editor.putString("desa", data.getDesa());
		editor.putString("lokal", data.getLokal());
		editor.putString("kades", data.getKades());
		editor.putString("sekdes", data.getSekdes());
		editor.putString("pengurus", data.getPengurus());
		editor.putString("alamatdesa", data.getAlamatdesa());
		editor.putString("photo", data.getPhoto());
		editor.commit();
	}

	public static user getLoginUser(Context context) {
		SharedPreferences data_user = PreferenceManager.getDefaultSharedPreferences(context);
		return new user(
				data_user.getInt("id", 0),
				data_user.getString("nama", "Selamat Datang"),
				data_user.getString("email", ""),
				data_user.getString("kecamatan", ""),
				data_user.getString("kabupaten", ""),
				data_user.getString("propinsi", ""),
				data_user.getString("desa", ""),
				data_user.getString("lokal", ""),
				data_user.getString("kades", ""),
				data_user.getString("sekdes", ""),
				data_user.getString("pengurus", ""),
				data_user.getString("alamatdesa", ""),
				data_user.getString("photo", "")
		);
	}

	public static void setGcm_regid(Context context, String regid) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		editor.putString("gcm_regid", regid);
		editor.commit();
	}

	public static String getGcm_regid(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("gcm_regid", "");
	}

	public static File getTempFile(Context context, String ext) {
		//it will return /sdcard/image.tmp
		File path = new File(Environment.getExternalStorageDirectory()+File.separator + R.string.app_name, "temp");
		if(!path.exists()) {
			path.mkdirs();
		}
		
		//System.out.println("TEMP---> "+path.getPath());		
		return new File(path, "temp."+ext);
	}
	
	public static String getOutputPath(Context context, String dest) {
		File path = new File(Environment.getExternalStorageDirectory()+File.separator + R.string.app_name, dest);		
		if (!path.exists()) path.mkdirs();
		
		//System.out.println("PATH---> "+path.getPath());		
		return path.getPath();
	}
	
	public static void setSettingRegId(Context context, String regid) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);    	
		Editor editor = prefs.edit();
		editor.putString("regid", regid);		
		editor.commit();		
	}
	
	public static String getSettingRegId(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs.getString("regid", "");				
	}

	public static DisplayImageOptions getOptionsImage(int stubImg, int imgRes) {
		return new DisplayImageOptions.Builder()
			.showStubImage(stubImg)		    //	Display Stub Image
			.showImageForEmptyUri(imgRes)	//	If Empty image found
			.cacheInMemory()
			.cacheOnDisc().bitmapConfig(Bitmap.Config.RGB_565).build();
		
	}
	
	public static void initImageLoader(Context context) {
		int memoryCacheSize;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
			int memClass = ((ActivityManager) 
					context.getSystemService(Context.ACTIVITY_SERVICE))
					.getMemoryClass();
			memoryCacheSize = (memClass / 8) * 1024 * 1024;
		} else {
			memoryCacheSize = 2 * 1024 * 1024;
		}

		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCacheSize(memoryCacheSize)
				.memoryCache(new FIFOLimitedMemoryCache(memoryCacheSize-1000000))
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();

		ImageLoader.getInstance().init(config);
	}

	public static String getRealPathFromURI(Context context, String contentURI) {
		Uri contentUri = Uri.parse(contentURI);
		Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
		if (cursor == null) {
			return contentUri.getPath();
		} else {
			cursor.moveToFirst();
			int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
			return cursor.getString(idx);
		}
	}
	
	public static Boolean compressImage(Context context, String imageUri, String imageDes) {
		
		ImageLoadingUtils utils = new ImageLoadingUtils(context);
		String filePath = getRealPathFromURI(context, imageUri);
		Bitmap scaledBitmap = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;						
		Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
		
		int actualHeight = options.outHeight;
		int actualWidth = options.outWidth;
		float maxHeight = 816.0f;
		float maxWidth = 612.0f;
		float imgRatio = actualWidth / actualHeight;
		float maxRatio = maxWidth / maxHeight;

		if (actualHeight > maxHeight || actualWidth > maxWidth) {
			if (imgRatio < maxRatio) {
				imgRatio = maxHeight / actualHeight;
				actualWidth = (int) (imgRatio * actualWidth);
				actualHeight = (int) maxHeight;
			} else if (imgRatio > maxRatio) {
				imgRatio = maxWidth / actualWidth;
				actualHeight = (int) (imgRatio * actualHeight);
				actualWidth = (int) maxWidth;
			} else {
				actualHeight = (int) maxHeight;
				actualWidth = (int) maxWidth;     
				
			}
		}
		
		options.inSampleSize = utils.calculateInSampleSize(options, actualWidth, actualHeight);
		options.inJustDecodeBounds = false;
		options.inDither = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		options.inTempStorage = new byte[16*1024];
			
		try {	
			bmp = BitmapFactory.decodeFile(filePath,options);
		}
		catch(OutOfMemoryError exception){
			exception.printStackTrace();
			
		}
		try {
			scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
		}
		catch(OutOfMemoryError exception){
			exception.printStackTrace();
		}
						
		float ratioX = actualWidth / (float) options.outWidth;
		float ratioY = actualHeight / (float)options.outHeight;
		float middleX = actualWidth / 2.0f;
		float middleY = actualHeight / 2.0f;
			
		Matrix scaleMatrix = new Matrix();
		scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

		Canvas canvas = new Canvas(scaledBitmap);
		canvas.setMatrix(scaleMatrix);
		canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

						
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);
		
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			Log.d("EXIF", "Exif: " + orientation);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 3) {
				matrix.postRotate(180);
				Log.d("EXIF", "Exif: " + orientation);
			} else if (orientation == 8) {
				matrix.postRotate(270);
				Log.d("EXIF", "Exif: " + orientation);
			}
			scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(imageDes);
			scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
						
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	public static Bitmap getBitmap(String urls) {
		try {
			URL url = new URL(urls);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);

			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String toTitleCase(String input) {
		StringBuilder titleCase = new StringBuilder();
		boolean nextTitleCase = true;

		for (char c : input.toCharArray()) {
			if (Character.isSpaceChar(c)) {
				nextTitleCase = true;
			} else if (nextTitleCase) {
				c = Character.toTitleCase(c);
				nextTitleCase = false;
			}

			titleCase.append(c);
		}

		return titleCase.toString();
	}
}