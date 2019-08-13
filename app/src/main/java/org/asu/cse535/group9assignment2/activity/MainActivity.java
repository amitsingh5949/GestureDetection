package org.asu.cse535.group9assignment2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.asu.cse535.group9assignment2.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button finalButton = (Button) findViewById(R.id.finalButton);
        finalButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FinalActivity.class);
                startActivity(intent);
            }
        });

        Button assignment2Button = (Button) findViewById(R.id.assignment2Button);
        assignment2Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Assignment2Activity.class);
                startActivity(intent);
            }
        });
    }
}
