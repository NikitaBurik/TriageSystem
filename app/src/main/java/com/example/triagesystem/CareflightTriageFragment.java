package com.example.triagesystem;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static java.lang.String.valueOf;

public class CareflightTriageFragment extends Fragment implements View.OnClickListener {

    TextView quest;
    TextView quest1;
    TextView quest2;
    TextView quest3;
    TextView quest4;

    Button tak;
    Button nie;
    Button tak1;
    Button nie1;
    Button tak2;
    Button nie2;
    Button tak3;
    Button nie3;
    Button tak4;
    Button nie4;

    Button starter;

    TextView color;
    Button akceptacja;

    int colorInt = 0;
    private String[] questions = {"Wykonuje polecenia?",
            "Tętno na tętnice promieniowej?","Oddech przy udrożnionych drogach oddechowych??"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View contentView = inflater.inflate(R.layout.fragment_careflight_triage, container, false);


        quest = contentView.findViewById(R.id.question);
        quest1 =  contentView.findViewById(R.id.question1);
        quest2 =  contentView.findViewById(R.id.question2);
        quest3 =  contentView.findViewById(R.id.question3);
        quest4 =  contentView.findViewById(R.id.question4);
//        quest = findViewById(R.id.question);
//        quest1 = findViewById(R.id.question1);
//        quest2 = findViewById(R.id.question2);
//        quest3 = findViewById(R.id.question3);
//        quest4 = findViewById(R.id.question4);
        color =  contentView.findViewById(R.id.color);
        starter =  contentView.findViewById(R.id.start);
        tak =   contentView.findViewById(R.id.tak);
        nie =   contentView.findViewById(R.id.nie);
        tak1 =  contentView.findViewById(R.id.tak1);
        nie1 =  contentView.findViewById(R.id.nie1);
        nie2 =  contentView.findViewById(R.id.nie2);
        tak2 =  contentView.findViewById(R.id.tak2);
        nie3 =  contentView.findViewById(R.id.nie3);
        tak3 =  contentView.findViewById(R.id.tak3);
        nie4 =  contentView.findViewById(R.id.nie4);
        tak4 =  contentView.findViewById(R.id.tak4);
        akceptacja = contentView.findViewById(R.id.acceptation);


//        color = findViewById(R.id.color);
//        starter = findViewById(R.id.start);
//        tak = findViewById(R.id.tak);
//        nie = findViewById(R.id.nie);
//        tak1 = findViewById(R.id.tak1);
//        nie1 = findViewById(R.id.nie1);
//        tak2 = findViewById(R.id.tak2);
//        nie2 = findViewById(R.id.nie2);
//        tak3 = findViewById(R.id.tak3);
//        nie3 = findViewById(R.id.nie3);
//        tak4 = findViewById(R.id.tak4);
//        nie4 = findViewById(R.id.nie4);

        starter.setOnClickListener(this);
        tak.setOnClickListener(this);
        nie.setOnClickListener(this);
        tak1.setOnClickListener(this);
        nie1.setOnClickListener(this);
        tak2.setOnClickListener(this);
        nie2.setOnClickListener(this);
        tak3.setOnClickListener(this);
        nie3.setOnClickListener(this);
        tak4.setOnClickListener(this);
        nie4.setOnClickListener(this);
        akceptacja.setOnClickListener(this);

        return contentView;
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        switch (clickedButton.getId()) {
            case R.id.acceptation:
                //color2.setBackgroundColor(colorInt);
                Intent intent = new Intent(getActivity(), AcceptedVictimsActivity.class);
                intent.putExtra("color", valueOf(colorInt));
                startActivity(intent);
            case R.id.start:
                color.setBackgroundColor(Color.WHITE);

                tak.setVisibility(View.VISIBLE);
                nie.setVisibility(View.VISIBLE);
                quest.setVisibility(View.VISIBLE);
                quest.setText(questions[0]);
                break;

            case R.id.tak:
                color.setBackgroundColor(Color.GREEN);
                colorInt = Color.GREEN;

                break;

            case R.id.nie:
                tak1.setVisibility(View.VISIBLE);
                nie1.setVisibility(View.VISIBLE);
                quest1.setVisibility(View.VISIBLE);
                quest1.setText(questions[1]);

                break;

            case R.id.tak1:
                color.setBackgroundColor(Color.YELLOW);
                colorInt = Color.YELLOW;

                break;

            case R.id.nie1:
                tak2.setVisibility(View.VISIBLE);
                nie2.setVisibility(View.VISIBLE);
                quest2.setVisibility(View.VISIBLE);
                quest2.setText(questions[2]);
                break;

            case R.id.tak2:
                color.setBackgroundColor(Color.RED);
                colorInt = Color.RED;

                break;

            case R.id.nie2:
                color.setBackgroundColor(Color.BLACK);
                colorInt = Color.BLACK;
                break;
                
        }
    }
}
