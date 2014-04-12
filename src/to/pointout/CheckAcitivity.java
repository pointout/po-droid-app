package to.pointout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.GsonBuilder;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class CheckAcitivity extends Activity{

	public static final String CHECK_ACTIVITY = "Check Activity ";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);
 
        // get action bar   
        ActionBar actionBar = getActionBar();
 
        // Enabling Up / Back navigation
        actionBar.setDisplayHomeAsUpEnabled(true);
        collectResponses();
    }
	
	 private void collectResponses() {
    	Map<String, Object> content = new HashMap<String, Object>();
    	content.put("device", MainActivity.DEVICE_ID);
    	
    	
    	HttpCheck asyncTask = new HttpCheck(content);
    	asyncTask.execute(MainActivity.httpUrl+"/request/list");
	}
	 
	 class HttpCheck extends AsyncTask<String, Void, Void>{

			private Map<String, Object> payload;
			
			private String responseContent;
			TextView uiUpdate = (TextView) findViewById(R.id.placeHolder);
			public HttpCheck(Map<String, Object> payload)
			{
				this.payload = payload;
			}
			
			protected void onPreExecute() {
		        uiUpdate.setText("Downloading source.. ");
		    }
			
			@Override
			protected Void doInBackground(String... urls) {
				String json = new GsonBuilder().create().toJson(payload, Map.class);
		    	Log.d(CheckAcitivity.CHECK_ACTIVITY, "the map is " +json);
		    	HttpClient client = new DefaultHttpClient();
		    	HttpPost post = new HttpPost(urls[0]);
		    	
		    	post.setHeader("Accept", "application/json");
		    	post.setHeader("Content-type", "application/json");
		    	try {
					post.setEntity(new StringEntity(json));
					HttpResponse response = client.execute(post);
					int statusCode = response.getStatusLine().getStatusCode();
					Log.i(CheckAcitivity.CHECK_ACTIVITY, "the http status = "+statusCode);
					if(statusCode == 200)
					{
						ResponseHandler<String> handler = new BasicResponseHandler();
						String responseString = handler.handleResponse(response);
						Log.i(CheckAcitivity.CHECK_ACTIVITY, responseString);
						responseContent = responseString;

					}else{
						responseContent = "some error";
					}
				} catch (UnsupportedEncodingException e) {
					responseContent = "some error";
					Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
				} catch (ClientProtocolException e) {
					responseContent = "some error";
					Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
				} catch (IOException e) {
					responseContent = "some error";
					Log.e(CheckAcitivity.CHECK_ACTIVITY, "error in posting", e);
				}
				return null;
			}
			
			protected void onPostExecute(Void unused) {
				uiUpdate.setText("Output");
				Map<String, Object> responseObj = new GsonBuilder().create().fromJson(responseContent, Map.class);
				
				ArrayList<Map<String,String>> requestList = (ArrayList<Map<String,String>>) responseObj.get("response");
				
				ListView listView = (ListView)findViewById(R.id.listOfRequests);
				SimpleAdapter listAdapter = new SimpleAdapter(CheckAcitivity.this, requestList
						, R.layout.listview_row
						, new String[] {"subject", "location"}
						, new int[] {R.id.subjectCol, R.id.locationCol});
				
				listView.setAdapter(listAdapter);
		    }

			
		}
}
