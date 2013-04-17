package com.tau.activities;

import android.app.Activity;
import android.os.Bundle;
import com.tau.R;

/**
 * Created with IntelliJ IDEA.
 * User: Roi
 * Date: 16/04/13
 */
public class PhotoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.photo);
    }
}
