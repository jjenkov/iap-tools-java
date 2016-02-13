package com.jenkov.iap.ion.pojos;

/**
 * Created by jjenkov on 16-11-2015.
 */
public class PojoArray10Float {

    public Pojo10Float[] pojos = null;

    public PojoArray10Float() {

    }

    public PojoArray10Float(int count) {
        this.pojos = new Pojo10Float[count];
        for(int i=0; i < count; i++){
            this.pojos[i] = new Pojo10Float();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " (" + this.pojos.length + ")";
    }

}
