package org.md2k.motionsense;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ActivityError extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String error=getIntent().getStringExtra("error");
        setContentView(R.layout.activity_error);
        TextView textView = (TextView) findViewById(R.id.textViewError);
        textView.setText(("error = "+error));

    }
}
