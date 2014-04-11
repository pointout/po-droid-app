package to.pointout;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CheckAcitivity extends Activity{

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
    	
    	String textForDebug = askFor.getText() + " || " + phones.getText() + " || " + emails.getText();
    	Log.d("Check Activity ", textForDebug);
		
	}
}
