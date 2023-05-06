package marks.rangecard.pro;

import org.json.*;
import android.location.*;
import java.io.Serializable;
import java.util.Date;
import com.berico.coords.Coordinates;

public class CurrentLocation extends JSONObject implements Serializable
{
	final static String TAG = "RANGE_CARD_ULOC:";
    boolean mine=true;
	public String origin="";
	public String originName="";
	public double lat=38.0;
	public double lon = -98.0;
	public double alt = 10.0;
	public double acc = 1.0;
	public String mgrs="";
	public long timestamp=100000;
    public CurrentLocation() 
    {
    }

    public CurrentLocation(TargetInfo info)
    {
        try
        {
            put("longitude",info.lon);
            put("lat", info.lat);
            put("alt", info.alt);
         //   put("acc",  info.acc);
            put("timestamp", info.timestamp);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        lat = getLattitude();
        lon = getLongitude();
        alt = getAltitude();
        acc = getAccuracy();

        timestamp=info.timestamp;
    }
    
    public CurrentLocation(String data) throws JSONException
    {
    	super(data);
    	lat = getLattitude();
    	lon = getLongitude();
    	alt = getAltitude();
    	acc = getAccuracy();
    	try
    	{
    		timestamp = getLong("timestamp");
    		origin = getString("origin");
    		originName = getString("originName");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	mine = false;
    }
    
   
    
    public void setOrigin(String origin)
    {
    	this.origin=origin;
    	try
    	{
    		put("origin",origin);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }
    
    public void setOriginName(String name)
    {
    	this.originName=name;
    	try
    	{
    		put("originName",originName);
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    }

    public Location getLocation()
    {
        Location l = new Location("RangeCard");
        l.setLatitude(lat);
        l.setLongitude(lon);
        l.setAltitude(alt);
        l.setAccuracy((float)acc);
        l.setTime(timestamp);
        return l;
    }
    
    public CurrentLocation(Location loc)
    {
    	try
    	{
    		put("longitude",loc.getLongitude());
	    	put("lat", loc.getLatitude());
	    	put("alt", loc.getAltitude());
	    	put("acc",  loc.getAccuracy());
	    	put("timestamp", loc.getTime());
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	lat = getLattitude();
    	lon = getLongitude();
    	alt = getAltitude();
    	acc = getAccuracy();
    	timestamp=loc.getTime();
    	
    }
    
    public double getLongitude()
    {
    	try
    	{
    		return getDouble("longitude");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return lon;
    }
    
    public double getLattitude()
    {
    	try
    	{
    		return getDouble("lat");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return lat;
    }
    
    public double getAltitude()
    {
    	try
    	{
    		return getDouble("alt");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return alt;
    }
    
    public double getAccuracy()
    {
    	try
    	{
    		return getDouble("acc");
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return acc;
    }
    
    public String getTime()
    {
    	try
    	{
    		return new Date(timestamp).toString();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.toString());
    	}
    	return new Date().toString();
    }
    
    public String jsonString()
    {
    	return super.toString();
    }
    
    @Override
    public String toString()
    {
    	return TAG+super.toString();
    }
    
}