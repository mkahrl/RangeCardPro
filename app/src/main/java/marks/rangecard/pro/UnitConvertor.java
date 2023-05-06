package marks.rangecard.pro;

import android.content.Context;

public class UnitConvertor 
{
    public static String defaultUnit="m";
	
    public UnitConvertor()
    {
    }
    
    public static String getAbbr(String unit)
    {
        if ( unit.equals("yard")) return "yd";
        if ( unit.equals("Naut. Mile")) return "NM";
        return unit;
    }
    
    public static double convertToMeter(double d, String unit)
    {
        if (unit.equals(defaultUnit)) return d;
        if (unit.equals("ft"))   return d/3.281;
        if (unit.equals("yard"))   return d/1.0936;
        if (unit.equals("km"))   return d*1000.0;
        if (unit.equals("mile")) return 0.00062137/d;
        if (unit.equals("Naut. Mile")) return 0.0005399568/d;
        return d;
    }
    
    public static double convertDistance(double d, String unit)
    {
    	if (unit.equals(defaultUnit)) return d;
    	if (unit.equals("ft"))   return d*3.281;
    	if (unit.equals("yard"))   return d*1.0936;
    	if (unit.equals("km"))   return d/1000.0;
    	if (unit.equals("mile")) return 0.00062137*d;
        if (unit.equals("Naut. Mile")) return 0.0005399568*d;
    	return d;
    }
    
}
