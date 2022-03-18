package com.example.bottledispenser;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContentInfo;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    TextView displayText;
    TextView displayMoney;
    TextView displayInfo;
    String[] disp;
    Spinner spinner;
    ArrayList<String> spinnerItems;
    SeekBar amountBar;
    Context context = null;
    String fileName = "";
    boolean receiptPrintable = false;
    Button printReceiptButton;
    ArrayList<Bottle> b;
    ArrayList<Bottle> pb; // previously purchased bottle
    int pbi; // prev purchased bottle id

    // dispenser-object where methods are called from
    BottleDispenser dispenser = BottleDispenser.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayText = (TextView) findViewById(R.id.displayText);
        displayMoney = (TextView) findViewById(R.id.displayMoney);
        displayInfo = (TextView) findViewById(R.id.bottleInfo);
        amountBar = (SeekBar) findViewById(R.id.amountBar);
        spinner = (Spinner) findViewById(R.id.spinner);
        context = MainActivity.this;
        printReceiptButton = (Button) findViewById(R.id.printReceiptButton);

        printReceiptButton.setBackgroundColor(Color.LTGRAY);

        System.out.println(MainActivity.this.getFilesDir().getAbsolutePath());

        // initial spinner setup
        updateSpinner();

        // changes bottle info display according to the chosen bottle
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemId() == 0) {
                    displayInfo.setText("");
                } else {
                    Bottle b = dispenser.returnBottleList().get((int) spinner.getSelectedItemId()-1);
                    displayInfo.setText("Info:\nEnergy: " + b.getEnergy() + "\nManufacturer: " + b.getManufacturer());
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    // spinner refresher, which is used when a bottle is bought
    public void updateSpinner() {
        b = dispenser.returnBottleList();

        // arraylist of items displayed in the spinner
        spinnerItems = new ArrayList<>();
        spinnerItems.add("Choose bottle (" + dispenser.bottlesLeft() + " left)");

        for (int i = 0; i < b.size(); i++) {
            spinnerItems.add(b.get(i).getName() + ",    " + b.get(i).getSize()
                    + " l,  " + b.get(i).getPrice() + " €");
        }

        // adding items to the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        spinner.setSelection(0);
    }

    // add money; the amount depends on the discrete seekbar progress
    @SuppressLint("SetTextI18n")
    public void addMoneyButton(View v) {
        double addAmount = 1;
        int increment = amountBar.getProgress();

        switch (increment) {
            case 0:
                addAmount = 0.1;
                break;
            case 1:
                addAmount = 0.2;
                break;
            case 2:
                addAmount = 0.5;
                break;
            case 3:
                addAmount = 1;
                break;
            case 4:
                addAmount = 2;
                break;
        }
        disp = dispenser.addMoney(addAmount);

        displayText.setText(disp[0]);
        displayMoney.setText(disp[1] + " €");
    }

    // attempt buying the bottle chosen by using the spinner
    @SuppressLint("SetTextI18n")
    public void buyBottleButton(View v) {
        int spinnerChoice = (int) spinner.getSelectedItemId();
        pb = new ArrayList<>(b);
        if (spinnerChoice != 0) {
            pbi = spinnerChoice - 1;

            disp = dispenser.buyBottle(spinnerChoice);

            if (disp[2].equals("true")) {
                updateSpinner();

                printReceiptButton.setBackgroundColor(Color.GRAY);
                receiptPrintable = true;
            }
            displayText.setText(disp[0]);
            displayMoney.setText(disp[1] + " €");
        } else {
            displayText.setText("Choose a bottle!");
        }
    }

    @SuppressLint("SetTextI18n")
    public void returnMoneyButton(View v) {
        disp = dispenser.returnMoney();

        displayText.setText(disp[0]);
        displayMoney.setText(disp[1] + " €");
    }

    // prints a receipt of the latest purchase to a file
    @SuppressLint("SetTextI18n")
    public void printReceipt(View v) {

        if (receiptPrintable) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Helsinki"));
            Date date = new Date();

            fileName = "Receipt_" + dateFormat.format(date) + "_.txt";

            try {
                OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));

                int i = pbi;

                writer.write("RECEIPT     " + dateFormat.format(date) + "\n\n");
                writer.write("Thank you for the purchase!\n\n" + String.format("%-22s%-22s%s", "Item:", "Size:", "Price:") + "\n");
                writer.write(String.format("%-22s%-22s%s €", pb.get(i).getName(), pb.get(i).getSize() + " l", pb.get(i).getPrice()) + "\n\n");
                writer.write("Energy: " + pb.get(i).getEnergy() + "\n");
                writer.write("Manufacturer: " + pb.get(i).getManufacturer());

                writer.close();

                printReceiptButton.setBackgroundColor(Color.LTGRAY);
                receiptPrintable = false;
                displayText.setText("Receipt printed!");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}