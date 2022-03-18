package com.example.bottledispenser;

import android.annotation.SuppressLint;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class BottleDispenser {
    private int bottles;
    private double money;
    private String displayMessage;
    private String[] displayables; // used for sending displayable stuff to MainActivity
    private final ArrayList<Bottle> bottleList;

    // Singleton
    private static BottleDispenser instance = null;

    public static BottleDispenser getInstance(){
        if (instance == null) {
            instance = new BottleDispenser();
        }
        return instance;
    }

    private BottleDispenser() {
        bottles = 0;
        money = 0;
        String name, manuf, totE, size, price;

        bottleList = new ArrayList<Bottle>();

        name = "Pepsi Max";
        manuf = "PepsiCo";
        totE = "0.3";
        size = "0.5";
        price = "2.2";
        bottleList.add(new Bottle(name, manuf, totE, size, price));
        size = "1.5";
        price = "2.5";
        bottleList.add(new Bottle(name, manuf, totE, size, price));

        name = "Coca-Cola";
        manuf = "The Coca-Cola Company";
        totE = "0.3";
        size = "0.5";
        price = "2.2";
        bottleList.add(new Bottle(name, manuf, totE, size, price));
        size = "1.5";
        price = "2.9";
        bottleList.add(new Bottle(name, manuf, totE, size, price));

        name = "Fanta";
        manuf = "The Coca-Cola Company";
        totE = "0.25";
        size = "0.5";
        price = "1.8";
        bottleList.add(new Bottle(name, manuf, totE, size, price));
        size = "1.5";
        price = "2.9";
        bottleList.add(new Bottle(name, manuf, totE, size, price));

        name = "ED Redberry";
        manuf = "Hartwall";
        totE = "0.9";
        size = "0.5";
        price = "2.0";
        bottleList.add(new Bottle(name, manuf, totE, size, price));

        bottles = bottleList.size();
    }

    public ArrayList<Bottle> returnBottleList() {
        return bottleList;
    }

    public int bottlesLeft() {
        return bottles;
    }

    @SuppressLint("DefaultLocale")
    public String[] addMoney(double addAmount) {
        money += addAmount;
        displayMessage = "Added " + addAmount + " €!";

        displayables = new String[] {displayMessage, String.format("%.2f", money)};
        return displayables;
    }

    @SuppressLint("DefaultLocale")
    public String[] buyBottle(int spinnerChoice) {
        boolean success = false;

        if (bottleList.size() >= spinnerChoice - 1) {
            Bottle chosenBottle = bottleList.get(spinnerChoice-1);

            if (money == 0) {
                displayMessage = "Add money first!";
            } else if (money < Double.parseDouble(chosenBottle.getPrice())) {
                displayMessage = "No enough money!";
            }else if (bottles == 0) {
                displayMessage = "No more bottles!";
            } else {
                bottles -= 1;
                money -= Double.parseDouble(chosenBottle.getPrice());
                displayMessage = chosenBottle.getName() + ", " + chosenBottle.getSize() + " l "
                        + " came out of the dispenser!";

                // bottle removed from the dispenser
                bottleList.remove(spinnerChoice-1);
                success = true; // if the purchase was successful
            }
        } else {
            displayMessage = "Choose other slot!";
        }

        displayables = new String[] {displayMessage, String.format("%.2f", money), String.valueOf(success)};
        return displayables;
    }

    @SuppressLint("DefaultLocale")
    public String[] returnMoney() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        if (Double.parseDouble(df.format(money)) > 0) {
            displayMessage = String.format("%.2f", money) + " € returned!";
        } else {
            displayMessage = "No more money in the dispenser!";
        }
        money = 0;
        displayables = new String[] {displayMessage, String.format("%.2f", money)};
        return displayables;
    }
}
