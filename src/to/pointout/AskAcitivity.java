package to.pointout;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;


public class AskAcitivity extends Activity{

	public static final String ASK_ACTIVITY = "Ask Activity ";
	 private static final String LOG_TAG = "ExampleApp";

	 private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	 private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	 private static final String OUT_JSON = "/json";

	 private static final String API_KEY = "AIzaSyAHHQGTby1FWrQguyb8NnPVy0Gruoa2cSk";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
 
        
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
//        AutoCompleteTextView textView = (AutoCompleteTextView)
//                findViewById(R.id.nearField);
//        textView.setAdapter(adapter);
        
        
        
        AutoCompleteTextView autoCompView = (AutoCompleteTextView) findViewById(R.id.nearField);
        autoCompView.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.location_list_item));
        
        
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
	
    private static final String[] COUNTRIES = new String[] {
        "Belgium", "France", "Italy", "Germany", "Spain"
    };
	
	 protected void postData(View v) {
    	EditText askFor =  (EditText)findViewById(R.id.askingFor);
    	EditText phones =  (EditText)findViewById(R.id.phoneNumbers);
    	EditText emails =  (EditText)findViewById(R.id.emails);
    	EditText location =  (EditText)findViewById(R.id.nearField);
    	
    	Map<String, Object> content = new HashMap<String, Object>();
    	content.put("device", MainActivity.DEVICE_ID);
    	content.put("subject", askFor.getText().toString());
    	content.put("location", location.getText().toString());
    	String[] emailsArray = emails.getText().toString().split("[;,]");
    	content.put("recipients", emailsArray);
    	
    	HttpAsk asyncTask = new HttpAsk(content);
    	asyncTask.execute(MainActivity.httpUrl+"/request/create");
    	
	}
	 
	 


	 private ArrayList<String> autocomplete(String input) {
	     ArrayList<String> resultList = null;

	     HttpURLConnection conn = null;
	     StringBuilder jsonResults = new StringBuilder();
	     try {
	         StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	         sb.append("?sensor=false&key=" + API_KEY);
	        // sb.append("&components=country:uk");
	         sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	         URL url = new URL(sb.toString());
	         conn = (HttpURLConnection) url.openConnection();
	         InputStreamReader in = new InputStreamReader(conn.getInputStream());

	         // Load the results into a StringBuilder
	         int read;
	         char[] buff = new char[1024];
	         while ((read = in.read(buff)) != -1) {
	             jsonResults.append(buff, 0, read);
	         }
	     } catch (MalformedURLException e) {
	         Log.e(LOG_TAG, "Error processing Places API URL", e);
	         return resultList;
	     } catch (IOException e) {
	         Log.e(LOG_TAG, "Error connecting to Places API", e);
	         return resultList;
	     } finally {
	         if (conn != null) {
	             conn.disconnect();
	         }
	     }

	     try {
	         // Create a JSON object hierarchy from the results
	         JSONObject jsonObj = new JSONObject(jsonResults.toString());
	         JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	         // Extract the Place descriptions from the results
	         resultList = new ArrayList<String>(predsJsonArray.length());
	         for (int i = 0; i < predsJsonArray.length(); i++) {
	             resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	         }
	     } catch (JSONException e) {
	         Log.e(LOG_TAG, "Cannot process JSON results", e);
	     }

	     return resultList;
	 }
	 
	 
	 private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		    private ArrayList<String> resultList;

		    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		        super(context, textViewResourceId);
		    }

		    @Override
		    public int getCount() {
		        return resultList.size();
		    }

		    @Override
		    public String getItem(int index) {
		        return resultList.get(index);
		    }

		    @Override
		    public Filter getFilter() {
		        Filter filter = new Filter() {
		            @Override
		            protected FilterResults performFiltering(CharSequence constraint) {
		                FilterResults filterResults = new FilterResults();
		                if (constraint != null) {
		                    // Retrieve the autocomplete results.
		                    resultList = autocomplete(constraint.toString());

		                    // Assign the data to the FilterResults
		                    filterResults.values = resultList;
		                    filterResults.count = resultList.size();
		                }
		                return filterResults;
		            }

		            @Override
		            protected void publishResults(CharSequence constraint, FilterResults results) {
		                if (results != null && results.count > 0) {
		                    notifyDataSetChanged();
		                }
		                else {
		                    notifyDataSetInvalidated();
		                }
		            }};
		        return filter;
		    }
		}
	 
	 
}
