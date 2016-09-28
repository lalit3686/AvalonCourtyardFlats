package com.app.avalon.courtyard.flats.beans;

public class OwnerDetails {
    public String Sr;
    public String Name;
    public String Block;
    public String BlockNumber;
    public String CellOne;
    public String CellTwo;
    public String TwoWheelerOne;
    public String TwoWheelerTwo;
    public String FourWheelerOne;
    public String FourWheelerTwo;

    @Override
    public boolean equals(Object o) {
        if(o instanceof OwnerDetails){
            OwnerDetails object = (OwnerDetails) o;
            if(object.Name.equalsIgnoreCase(this.Name)){
                return true;
            }
        }
        return false;
    }
}