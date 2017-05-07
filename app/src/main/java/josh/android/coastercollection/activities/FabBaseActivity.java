package josh.android.coastercollection.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Jos on 7/05/2017.
 */

public class FabBaseActivity extends AppCompatActivity {

    protected FloatingActionButton fab;
    private Handler fabHandler = new Handler();
    private FabRunnable fabRunnable = new FabRunnable();

    private final static int FAB_VISIBLE_TIMEOUT = 5000; // msec

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setFabVisible() {
        fab.setVisibility(View.VISIBLE);

        fabHandler.removeCallbacks(fabRunnable);

        fabHandler.postDelayed(fabRunnable, FAB_VISIBLE_TIMEOUT);
    }

    /*
    ** INNERCLASS: FabOnClickListener
     */
    private class FabRunnable implements Runnable {
        @Override
        public void run() {
            fab.setVisibility(View.GONE);
        }
    }
}
