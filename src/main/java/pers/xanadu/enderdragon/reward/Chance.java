package pers.xanadu.enderdragon.reward;


public class Chance {
    private double value;
    private String str;
    public Chance(Chance chance){
        str = chance.str;
        value = chance.value;
    }
    public Chance(double d0,String str){
        this.str = str;
        this.value = d0;
    }
    public double getValue(){
        return value;
    }
    public String getStr(){
        return str;
    }

}
