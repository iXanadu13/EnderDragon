package xanadu.enderdragon.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyMath {
    public static double div(double d1,double d2,int len){
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2,len, RoundingMode.HALF_UP).doubleValue();
    }
}
