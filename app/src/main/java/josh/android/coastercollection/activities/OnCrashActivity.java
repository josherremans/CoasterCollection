package josh.android.coastercollection.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import josh.android.coastercollection.enums.IIntentExtras;
import josh.android.coastercollection.R;

public class OnCrashActivity extends AppCompatActivity {

	private final static String LOG_TAG = "ON_CRASH_ACTIVITY";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_on_crash);
		
		Log.i(LOG_TAG, "onCreate.");
        
        Intent originIntent = this.getIntent();
        
        final String stackTrace = originIntent.getStringExtra(IIntentExtras.EXTRA_STACKTRACE);
        
        // Handle button show details
        findViewById(R.id.btnOnCrashDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                
                builder.setMessage(stackTrace);
                builder.setTitle(R.string.lbl_on_crash_details_title);
                builder.setNeutralButton(R.string.btn_ok, null);
                
                builder.show();
            }
        });

        // Handle button close app
        findViewById(R.id.btnOnCrashCloseApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	finish();
            	
            	killCurrentProcess();
            }
        });
    }
	
	/**
     * INTERNAL method that kills the current process.
     * It is used after restarting or killing the app.
     */
    private void killCurrentProcess() {
		Log.i(LOG_TAG, "killCurrentProcess");
		
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
