package com.example.bottledispenser;

public class Bottle {
    private final String name_label;
    private final String manufacturer;
    private final String total_energy;
    private final String size_litres;
    private final String price_eur;

    public Bottle(String name,
                  String manuf,
                  String totE,
                  String size,
                  String price)
    {
        name_label = name;
        manufacturer = manuf;
        total_energy = totE;
        size_litres = size;
        price_eur = price;
    }

    public String getName(){
        return name_label;
    }

    public String getManufacturer(){
        return manufacturer;
    }

    public String getEnergy(){
        return total_energy;
    }

    public String getSize() {
        return size_litres;
    }

    public String getPrice() {
        return price_eur;
    }
}
