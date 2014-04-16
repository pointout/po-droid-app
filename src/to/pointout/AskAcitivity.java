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
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

public class AskAcitivity extends Activity implements OnItemClickListener, OnItemSelectedListener{

	public static final String ASK_ACTIVITY = "Ask Activity ";
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyAHHQGTby1FWrQguyb8NnPVy0Gruoa2cSk";

	MultiAutoCompleteTextView textView=null;
	private ArrayAdapter<String> adapter;

	// Store contacts values in these arraylist
	public static ArrayList<String> phoneValueArr = new ArrayList<String>();
	public static ArrayList<String> nameValueArr = new ArrayList<String>();
	public static ArrayList<String> emailValueArr = new ArrayList<String>();
	EditText toNumber=null;
	String toNumberValue="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ask);

		loadLocationAutocomplete();
		loadContactsAutocomplete();
	}

	private void loadLocationAutocomplete(){
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

	private void loadContactsAutocomplete(){
		//final Button Send = (Button) findViewById(R.id.Send);

		// Initialize AutoCompleteTextView values

		textView = (MultiAutoCompleteTextView) findViewById(R.id.emailContacts);

		//Create adapter    
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
		textView.setThreshold(1);

		//Set adapter to AutoCompleteTextView
		textView.setAdapter(adapter);
		textView.setOnItemSelectedListener(this);
		textView.setOnItemClickListener(this);
		textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

		// Read contact data and add data to ArrayAdapter
		// ArrayAdapter used by AutoCompleteTextView

		readContactEmailData();
		//Send.setOnClickListener(BtnAction(textView));
	}

	protected void postData(View v) {
		EditText askFor =  (EditText)findViewById(R.id.askingFor);
		//EditText phones =  (EditText)findViewById(R.id.phoneNumbers);
		EditText emails =  (EditText)findViewById(R.id.emailContacts);
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

	private ArrayList<String> locationAutocompleteData(String input) {
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


	private OnClickListener BtnAction(final AutoCompleteTextView toNumber) {
		return new OnClickListener() {

			public void onClick(View v) {

				String NameSel = "";
				NameSel = toNumber.getText().toString();
				final String ToNumber = toNumberValue;
				if (ToNumber.length() == 0 ) {
					Toast.makeText(getBaseContext(), "Please fill phone number",
							Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(getBaseContext(), NameSel+" : "+toNumberValue,
							Toast.LENGTH_LONG).show();
				}
			}
		};
	}   


	// Read phone contact name and phone numbers 

	private void readContactData() {

		try {

			/*********** Reading Contacts Name And Number **********/

			String phoneNumber = "";
			ContentResolver cr = getBaseContext().getContentResolver();

			//Query to get contact name

			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

			// If data data found in contacts 
			if (cur.getCount() > 0) {

				Log.i("AutocompleteContacts", "Reading   contacts........");

				int k=0;
				String name = "";

				while (cur.moveToNext()) 
				{

					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					//Check contact have phone number
					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
					{

						//Create query to get phone number by contact id
						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
										new String[] { id },null);
						int j=0;

						while (pCur.moveToNext()) 
						{
							// Sometimes get multiple data 
							if(j==0)
							{
								// Get Phone number
								phoneNumber =""+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

								// Add contacts names to adapter
								adapter.add(name);

								// Add ArrayList names to adapter
								phoneValueArr.add(phoneNumber.toString());
								nameValueArr.add(name.toString());

								j++;
								k++;
							}
						}  // End while loop
						pCur.close();
					} // End if

				}  // End while loop

			} // End Cursor value check
			cur.close();
		} catch (Exception e) {
			Log.i("AutocompleteContacts","Exception : "+ e);
		}
	}

	private void readContactEmailData() {
		

	        

		try {

			/*********** Reading Contacts Name And Number **********/

			String phoneNumber = "";
			ContentResolver cr = getBaseContext().getContentResolver();

			//Query to get contact name

			Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

			// If data data found in contacts 
			if (cur.getCount() > 0) {

				Log.i("AutocompleteContacts", "Reading   contacts........");

				int k=0;
				String name = "";

				while (cur.moveToNext()) 
				{

					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					//Check contact have phone number
					//if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
					{

						
			             String id_email = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
			                Cursor pCur = cr.query( 
			                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
			                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
			                                new String[]{id_email}, null); 
			                

						int j=0;

						while (pCur.moveToNext()) 
						{
							// Sometimes get multiple data 
							if(j==0)
							{
								// Get Phone number
								//phoneNumber =""+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String email = ""+pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
								// Add contacts names to adapter
								adapter.add(name);

								// Add ArrayList names to adapter
								emailValueArr.add(email.toString());
								nameValueArr.add(name.toString());

								j++;
								k++;
							}
						}  // End while loop
						pCur.close();
					} // End if

				}  // End while loop

			} // End Cursor value check
			cur.close();
		} catch (Exception e) {
			Log.i("AutocompleteContacts","Exception : "+ e);
		}
	}
	
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		//Log.d("AutocompleteContacts", "onItemSelected() position " + position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

		InputMethodManager imm = (InputMethodManager) getSystemService(
				INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		// Get Array index value for selected name
		int i = nameValueArr.indexOf(""+arg0.getItemAtPosition(arg2));

		// If name exist in name ArrayList
		if (i >= 0) {

			// Get Phone Number
			//toNumberValue = phoneValueArr.get(i);
			toNumberValue = emailValueArr.get(i);
			InputMethodManager imm = (InputMethodManager) getSystemService(
					INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

			// Show Alert       
			Toast.makeText(getBaseContext(), 
					"Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue,
					Toast.LENGTH_LONG).show();

			Log.d("AutocompleteContacts", 
					"Position:"+arg2+" Name:"+arg0.getItemAtPosition(arg2)+" Number:"+toNumberValue);

		}

	}

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
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
						resultList = locationAutocompleteData(constraint.toString());

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
