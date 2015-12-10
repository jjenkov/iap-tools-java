package com.jenkov.iap.ion.util;

/**
 * Created by jjenkov on 26-11-2015.
 */
public class OpenArrayList {

    public Object[] elements = null;

    public int capacity = 0;
    public int size     = 0;

    public OpenArrayList(){

    }

    public OpenArrayList(int capacity){
        this.capacity = capacity;
        this.elements = new Object[capacity];
    }

    public OpenArrayList(Object[] elements){
        this.elements = elements;
        this.capacity = elements.length;
    }


    public void add(Object element){
        this.elements[this.size++] = element;
    }

    public boolean addIfCapacity(Object element){
        if(this.size < this.capacity){
            this.elements[this.size++] = element;
            return true;
        }
        return false;
    }

}
