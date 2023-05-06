/**
 * @(#)TargetInfo.java
 *
 *
 * @author Mark Kahrl
 * @version 1.00 2009/11/15
 */
package marks.rangecard.pro;

import java.io.Serializable;
import org.json.*;
import org.json.JSONException;

public class TargetInfo extends JSONObject implements Serializable
{
    public double lat;
    public double lon;
    public double alt;
    public String name = "Target";
    public long timestamp;
    public String id = "";
    
    public float markerColor;
    public boolean isShowing;
    public int type;// 0 target, 1 pos.
    public int color;
    public String tag = "";
    public String notes="";
    public String windage="";
    public String elevation="";
    public float rng;
    public float brng;
    public float elv;
    public boolean isRefPoint;
    public String card = "default";
    
    public TargetInfo()
    {
        super();
    }

    public TargetInfo(String data) throws JSONException
    {
        super(data);
        try
        {
            lat = getDouble("lat");
            lon = getDouble("lon");
            alt = getDouble("alt");
            name = getString("name");
            id = getString("id");
            tag = getString("tag");
            notes = getString("notes");
            card = getString("card");
            elevation = getString("elevation");
            windage = getString("windage");

            if (elevation==null)elevation="";
            if (windage==null)windage="";

            isRefPoint = getBoolean("isRefPoint");
            isShowing = getBoolean("isShowing");

            timestamp = getLong("timestamp");
            markerColor = (float)getDouble("markerColor");

            type = getInt("type");
            color = getInt("color");

            rng = (float) getDouble("rng");
            brng = (float) getDouble("brng");
            elv = (float) getDouble("elv");
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

    }
    
    
    public TargetInfo(double lat, double lon, double alt, String name)
    {
        super();
        if ( timestamp < 1 ) timestamp = System.currentTimeMillis();
        if (id.length() < 2 ) id = timestamp+"RangeCard";
    	this.lat=lat;
    	this.lon=lon;
    	this.alt=alt;
    	this.name = name;

        try
        {
            put("lat", lat);
            put("lon", lon);
            put("alt", alt);
            put("name", name);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
    
    public String jsonString()
    {
        try
        {
            put("lat", lat);
            put("lon", lon);
            put("alt", alt);
            put("name", name);
            put("id", id);
            put("tag", tag);
            put("notes", notes);
            put("card", card);
            put("isRefPoint", isRefPoint);
            put("isShowing", isShowing);
            put("type", type);
            put("color", color);
            put("markerColor", markerColor);
            put("timestamp", timestamp);

            put("rng", rng);
            put("brng", brng);
            put("elv", elv);
            put("windage", windage);
            put("elevation", elevation);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return super.toString();
    }
}
