package to.pointout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.GsonBuilder;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        
        Button askButton = (Button)findViewById(R.id.askButton);
        askButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                 postData(v);

            }
        });
    }
	
	 protected void postData(View v) {
    	EditText askFor =  (EditText)findViewById(R.id.askingFor);
    	EditText phones =  (EditText)findViewById(R.id.phoneNumbers);
    	EditText emails =  (EditText)findViewById(R.id.emails);
    	EditText location =  (EditText)findViewById(R.id.nearField);
    	
    	Map<String, Object> content = new HashMap<String, Object>();
    	content.put("device", "TEST-DEVICE");
    	content.put("subject", askFor.getText().toString());
    	content.put("location", location.getText().toString());
    	String[] emailsArray = emails.getText().toString().split("[;,]");
    	content.put("recipients", emailsArray);
    	
    	HttpAsyncTask asyncTask = new HttpAsyncTask(content);
    	asyncTask.execute(MainActivity.httpUrl+"/request/create");
    	
	}
}
