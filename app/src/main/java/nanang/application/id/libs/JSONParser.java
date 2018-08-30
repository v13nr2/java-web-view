package nanang.application.id.libs;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class JSONParser {

	InputStream is;
	JSONObject jObj;
	String json;
	String get_cookies;

	// constructor
	public JSONParser() {
		is = null;
		jObj = null;
		json = "";
	}

	public String getCookies() {
		return this.get_cookies;
	}

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params,  String cookies_) {

		// Making HTTP request
		try {
			// defaultHttpClient
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost(url);

			if(cookies_ != null)
				httpPost.setHeader("Cookie", cookies_);

			if (params != null)
				httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			get_cookies = "";
			List<Cookie> cook = httpClient.getCookieStore().getCookies();
			for (int i = 0; i < cook.size(); i++) {
				get_cookies = (get_cookies.length()>0?"; ":"") + cook.get(i).getName() + "=" + cook.get(i).getValue();
			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer);
			json = writer.toString();
			Log.i("JSON_RESPON", json);

		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;

	}
}
