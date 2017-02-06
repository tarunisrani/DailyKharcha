package com.tarunisrani.dailykharcha.model;

/**
 * Created by tarunisrani on 12/20/16.
 */
public class Category {


    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public float getCategory_value() {
        return category_value;
    }

    public void setCategory_value(float category_value) {
        this.category_value = category_value;
    }

    public float getCategory_percentace() {
        return category_percentace;
    }

    public void setCategory_percentace(float category_percentace) {
        this.category_percentace = category_percentace;
    }

    private String category_name;
    private float category_value;
    private float category_percentace;

    public Category(String name, float value){
        this.category_name = name;
        this.category_value = value;
    }

    public void addValue(float value){
        this.category_value += value;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof String){
            return ((String)o).equalsIgnoreCase(this.getCategory_name());
        } else if(o instanceof Category) {
            return ((Category)o).getCategory_name().equalsIgnoreCase(this.getCategory_name());
        } else{
            return false;
        }
    }
}
