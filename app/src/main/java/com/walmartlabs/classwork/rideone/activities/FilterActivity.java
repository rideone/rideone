package com.walmartlabs.classwork.rideone.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.walmartlabs.classwork.rideone.R;
import com.walmartlabs.classwork.rideone.models.Filter;

public class FilterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setupSpinners();
    }

    public void setupSpinners() {
        Spinner spSpots = createSpinnerFromResource(R.id.spSpots, R.array.spots, Integer.toString(Filter.getSpots()));
        Spinner spStartLoc = createSpinnerFromResource(R.id.spStartLoc, R.array.locations, Filter.getStart());
        Spinner spDestination = createSpinnerFromResource(R.id.spDestination, R.array.locations, Filter.getDestination());
        Spinner spTimeRange = createSpinnerFromResource(R.id.spTimeRange, R.array.timerange, Filter.getTimeRange());

        spSpots.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Filter.setSpots(Integer.valueOf((String) parent.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStartLoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Filter.setStart((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Filter.setDestination((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spTimeRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Filter.setTimeRange((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public Spinner createSpinnerFromResource(int id, int resource, String filter) {
        Spinner spinner = (Spinner) findViewById(id);
        ArrayAdapter<CharSequence> aValues = ArrayAdapter.createFromResource(this,
                resource, android.R.layout.simple_spinner_item);

        aValues.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aValues);
        int position = aValues.getPosition(filter);
        spinner.setSelection(position);

        return spinner;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void onSave(View view) {
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }
}