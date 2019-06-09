package com.example.triagesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

import static java.lang.String.valueOf;

public class AcceptedVictimsActivity extends AppCompatActivity {

    TextView tvView;

    TextView quantity;

    int color = 0;

    int counterGreen = 0;
    int counterRed = 0;
    int counterYellow = 0;
    int counterBlack = 0;

    Button btnSave, btnLoad;

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_text";
    final String savedBLACK = "savedBlack";
    final String savedGREEN = "savedGreen";
    final String savedYELLOW = "savedYellow";
    final String savedRED = "savedRed";

    TextView green;
    TextView red;
    TextView yellow;
    TextView black;




    String[] mobileArray = {"Android", "IPhone", "WindowsMobile", "Blackberry",
            "WebOS", "Ubuntu", "Windows7", "Max OS X"};
    ArrayList<String> victims = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_victims);
        Objects.requireNonNull(getSupportActionBar()).hide(); //<< this

        tvView = (TextView) findViewById(R.id.color);
        quantity = (TextView) findViewById(R.id.quantity);

        green = (TextView)findViewById(R.id.colorGreen);
        red = (TextView)findViewById(R.id.colorRed);
        yellow = (TextView)findViewById(R.id.colorYellow);
        black = (TextView)findViewById(R.id.colorBlack);


        Intent intent = getIntent();

        String colorInt = intent.getStringExtra("color");
        Integer victimNumber = intent.getIntExtra("numberVictim", 0);
        color = Integer.parseInt(colorInt);
        tvView.setBackgroundColor(color);
        loadText();
        switch (color) {
            case Color.GREEN:
                counterGreen++;
                break;
            case Color.BLACK:
                counterBlack++;
                break;
            case Color.RED:
                counterRed++;
                break;
            case Color.YELLOW:
                counterYellow++;
                break;
        }
    //tvView.setText(valueOf(counterGreen));
        quantity.setText(valueOf(counterBlack)+","+valueOf(counterGreen)+","+valueOf(counterYellow)+","+valueOf(counterRed));

        green.setText(valueOf(counterGreen));
        red.setText(valueOf(counterRed));
        yellow.setText(valueOf(counterYellow));
        black.setText(valueOf(counterBlack));
               
    }

    void saveText() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(savedBLACK, counterBlack);
        ed.putInt(savedGREEN,counterGreen);
        ed.putInt(savedRED,counterRed);
        ed.putInt(savedYELLOW,counterYellow);

        ed.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        //String savedText = sPref.getString(SAVED_TEXT, "");
        Integer savedBlack = sPref.getInt(savedBLACK,0);
        Integer savedGreen = sPref.getInt(savedGREEN,0);
        Integer savedRed = sPref.getInt(savedRED,0);
        Integer savedYellow = sPref.getInt(savedYELLOW,0);

        counterBlack = savedBlack;
        counterGreen = savedGreen;
        counterRed = savedRed;
        counterYellow = savedYellow;

        quantity.setText(valueOf(savedBlack)+","+valueOf(savedGreen));
        Toast.makeText(this, "Text loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveText();
    }

}