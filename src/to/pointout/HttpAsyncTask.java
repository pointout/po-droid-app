package to.pointout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.GsonBuilder;

import android.os.AsyncTask;
import android.util.Log;

public class HttpAsyncTask extends AsyncTask<String, Void, Void>{

	private Map<String, Object> payload;

	public HttpAsyncTask(Map<String, Object> payload)
	{
		this.payload = payload;
	}
	
	@Override
	protected Void doInBackground(String... urls) {
		String json = new GsonBuilder().create().toJson(payload, Map.class);
    	Log.d(CheckAcitivity.CHECK_ACTIVITY, json);
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(urls[0]);
    	
    	post.setHeader("Accept", "application/json");
    	post.setHeader("Content-type", "application/json");
    	try {
			post.setEntity(new StringEntity(json));
			HttpResponse response = client.execute(post);
			Log.i(CheckAcitivity.CHECK_ACTIVITY, response.getStatusLine().toString());
		} catch (UnsupportedEncodingException e) {
			Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
		} catch (ClientProtocolException e) {
			Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
		} catch (IOException e) {
			Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
		}
		return null;
	}
	

}
