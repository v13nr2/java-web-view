package nanang.application.id.iad;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import nanang.application.id.customfonts.MyTextView;
import nanang.application.id.libs.CommonUtilities;
import nanang.application.id.libs.Preview;

public class AmbilFotoActivity extends AppCompatActivity {

	Context context;
	static Camera mCamera;
	Preview preview;
	FrameLayout layout;
	SurfaceView surface;
	static int current_camera;
	static int nums_of_camera;
	ImageButton btn_capture, btn_rotate;

	Dialog dialog_informasi;
	MyTextView btn_ok;
	MyTextView text_title;
	MyTextView text_informasi;

	Typeface fonts1,fonts2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ambil_foto);

		context = AmbilFotoActivity.this;

		dialog_informasi = new Dialog(context);
		dialog_informasi.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog_informasi.setCancelable(true);
		dialog_informasi.setContentView(R.layout.informasi_dialog);

		btn_ok = (MyTextView) dialog_informasi.findViewById(R.id.btn_ok);
		btn_ok.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog_informasi.dismiss();
				finish();
			}
		});
		btn_ok.setTypeface(fonts2);

		text_title = (MyTextView) dialog_informasi.findViewById(R.id.text_title);
		text_informasi = (MyTextView) dialog_informasi.findViewById(R.id.text_dialog);
		text_informasi.setTypeface(fonts1);

		fonts1 =  Typeface.createFromAsset(getAssets(),	"fonts/OpenSans-Regular.ttf");
		fonts2 =  Typeface.createFromAsset(getAssets(),	"fonts/OpenSans-Semibold.ttf");

		layout = (FrameLayout) findViewById(R.id.layout);
		surface = (SurfaceView) findViewById(R.id.surfaceView);
		preview = new Preview(context, surface);

		layout.addView(preview);
		preview.setKeepScreenOn(true);

		btn_capture = (ImageButton) findViewById(R.id.btnCapture);
		btn_capture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
			}
		});

		btn_rotate = (ImageButton) findViewById(R.id.btnRotate);
		btn_rotate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				current_camera = current_camera==0?1:0;
				preview.destroyHolder();
				releaseCameraAndPreview();
				getCamera();
				preview.surfaceChanged(preview.getPreviewHolder(), 0, preview.getSurfaceView().getWidth(), preview.getSurfaceView().getHeight());
			}
		});

		current_camera = 0;
	}

	private void releaseCameraAndPreview() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();

		nums_of_camera = Camera.getNumberOfCameras();
		if(nums_of_camera==0) {
			text_informasi.setText("Kamera tidak ditemukan.");
			text_title.setText("Kesalahan");
			dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialog_informasi.show();
		}

		btn_rotate.setVisibility(nums_of_camera>1?View.VISIBLE:View.INVISIBLE);
		getCamera();
	}

	private void getCamera() {
		releaseCameraAndPreview();

		try {
			mCamera = Camera.open(current_camera);
			mCamera.setPreviewDisplay(preview.getPreviewHolder());
			mCamera.startPreview();
			preview.setCamera(mCamera);

		} catch (RuntimeException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onPause() {

		super.onPause();

	}

	private void refreshGallery(File file) {
		Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(Uri.fromFile(file));
		sendBroadcast(mediaScanIntent);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {

		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {

			new SaveImageTask().execute(data);
		}
	};

	private class SaveImageTask extends AsyncTask<byte[], Void, String> {

		@Override
		protected String doInBackground(byte[]... data) {

			Bitmap storedBitmap = BitmapFactory.decodeByteArray(data[0], 0, data[0].length, null);

			int width = storedBitmap.getWidth();
			int height = storedBitmap.getHeight();

			float aspectRatio = storedBitmap.getWidth() / (float) storedBitmap.getHeight();
			int newWidth_temps = 800;
			int newHeight_temps = Math.round(newWidth_temps / aspectRatio);

			//IF FROM HEIGHT
			int degrees = preview.getDegrees();
			if(degrees==0 || degrees==180) {
				newHeight_temps = 800;
				newWidth_temps = Math.round(newHeight_temps * aspectRatio);
			}
			int newWidth = newWidth_temps;
			int newHeight = newHeight_temps;

			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			matrix.postRotate((current_camera==1?-1:1)*degrees);

			try {
				storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, width, height, matrix, true);
				FileOutputStream outStream;
				File dir = new File (CommonUtilities.getOutputPath(context, "images"));
				String fileName = String.format("%d.jpg", System.currentTimeMillis());
				File outFile = new File(dir, "temps_"+fileName);

				outStream = new FileOutputStream(outFile);
				storedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				if (storedBitmap != null) {
					storedBitmap.recycle();
					storedBitmap = null;
				}

				outStream.flush();
				outStream.close();

				String selectedPath = outFile.getAbsolutePath();
				String dest = CommonUtilities.getOutputPath(context, "images")+File.separator+fileName;
				CommonUtilities.compressImage(context, selectedPath, dest);
				outFile.delete();
				outFile = null;
				selectedPath = dest;

				refreshGallery(new File(selectedPath));

				return selectedPath;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			preview.destroyHolder();
			releaseCameraAndPreview();

			Intent intent = new Intent();
			intent.putExtra("path", result);
			setResult(RESULT_OK, intent);
			finish();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("current_camera", current_camera);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
		current_camera = savedInstanceState.getInt("current_camera");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
