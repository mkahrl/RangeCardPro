/**
 * @(#)PatientInfoManager.java
 *
 *
 * @author Mark Kahrl
 * @version 1.00 2013/4/25
 */

package marks.rangecard.pro;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import com.google.android.gms.maps.model.*;
import com.google.android.gms.maps.GoogleMap;
import java.util.*;

public class DataManager
{
    Hashtable<Marker, TargetInfo> targets = new Hashtable<Marker, TargetInfo>();
	private static DataManager instance;
    Context ctx;
    final static String targFile = "target-info";
    private GoogleMap map;

	/// Singleton class, access from this static method.
	public static DataManager getInstance(Context ctx)
	{
		if (instance==null) instance = new DataManager(ctx);
		return instance;
	}
	
    private DataManager(Context ctx)
    {
    	this.ctx=ctx.getApplicationContext();
    }


    public void setMap(GoogleMap map)
    {
        this.map=map;
        readTargetsFromFile();
    }
    
    public int getNumberOfTargets()
    {
        return targets.size();
    }
    
    public TargetInfo getTargetInfo(Marker m)
    {
        return targets.get(m);
    }
    
    public synchronized void addTarget(TargetInfo info, Marker marker)
    {
        targets.put(marker, info);
        save();
    }

    public void removeTarget(Marker m)
    {
        System.out.println("Remove target ....");
        targets.remove(m);
        save();
    }

    public Marker getMarker(TargetInfo tinfo)
    {
        Enumeration<Marker> markers = targets.keys();
        while (markers.hasMoreElements())
        {
            Marker m = markers.nextElement();
            TargetInfo ti = targets.get(m);
            if (ti.id.equals(tinfo.id)) return m;

        }
        return null;
    }
    
    public Enumeration<Marker> getKeys()
    {
        return targets.keys();
    }
    
    public synchronized TargetInfo getTargetById(String id)
    {
        Vector<TargetInfo> targetList = getTargets();
        Enumeration<TargetInfo> tlist = targetList.elements();
        while (tlist.hasMoreElements())
        {
            TargetInfo ti = tlist.nextElement();
            if (ti.id.equals(id)) return ti;
        }
        return null;
    }

    public synchronized Vector<TargetInfo> getTargets()
    {
        Vector<TargetInfo> targs = new Vector<TargetInfo>(targets.size());

        Enumeration<TargetInfo> te = targets.elements();

        while (te.hasMoreElements())
        {
            TargetInfo ti = te.nextElement();
            targs.add(ti);
        }
        targs = sortByRange(targs);
        return targs;
    }
    
   private synchronized void readTargetsFromFile()
    {
        System.out.println("getTargets...");
        Vector<String> targetList = new Vector<String>();
        targets = new Hashtable<Marker, TargetInfo>();
        File dir = ctx.getCacheDir();
   	    File file = new File(dir, targFile);
   	    try
        {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(in);
            targetList = (Vector<String>) oin.readObject();
            Enumeration<String> tlist = targetList.elements();
            while (tlist.hasMoreElements())
            {
                String data = tlist.nextElement();
                TargetInfo ti = new TargetInfo(data);

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(ti.name);
                markerOptions.draggable(true);
                if (ti.type > 0) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.frienddroidx));
                else markerOptions.icon(BitmapDescriptorFactory.defaultMarker(ti.markerColor));
                LatLng ll = new LatLng(ti.lat, ti.lon);
                markerOptions.position(ll);
                Marker marker = map.addMarker(markerOptions);
                targets.put(marker, ti);
            }
            
            in.close();
            oin.close();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }

    private Vector sortByRange(Vector<TargetInfo>  list)
    {
        boolean sorted = false;
        int length = list.size();
        int i = 0;

        if (length > 0)
        {
            while (!sorted)
            {
                sorted = true;
                for(i=0; i<length - 1; i++)
                {
                    TargetInfo info = list.elementAt(i);
                    TargetInfo info_inc = list.elementAt(i+1);

                    if ( info_inc.rng < info.rng )
                    {
                        list.setElementAt(info_inc, i);
                        list.setElementAt(info, i+1);
                        sorted = false;
                    }
                }
            }
        }
        return list;
    }

    public synchronized void save()
    {
        System.out.println("saveTargets...");
        Vector<String> targetList = new Vector<String>();
        Enumeration<TargetInfo> tlist = targets.elements();
        while (tlist.hasMoreElements())
        {
            TargetInfo ti = tlist.nextElement();
            targetList.add(ti.jsonString());
        }
        
        File dir = ctx.getCacheDir();
   	    File file = new File(dir, targFile);
        try
        {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(targetList);
            oout.flush();
            oout.close();
            out.close();
        }
        catch(Exception e)
        {
            System.out.println("Error saving data! "+e.toString());
        }

    }
}