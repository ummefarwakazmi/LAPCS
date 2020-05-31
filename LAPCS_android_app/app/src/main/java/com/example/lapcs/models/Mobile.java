package com.example.lapcs.models;

public class Mobile {
    public String name,model;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Mobile() {
    }

    public Mobile(String name, String model) {

        this.name = name;
        this.model = model;
    }
}
