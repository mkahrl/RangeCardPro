
package marks.rangecard.pro;

import android.gesture.GestureOverlayView;
import android.view.MotionEvent;
import android.util.DisplayMetrics;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.hardware.GeomagneticField;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import java.io.*;
import java.util.Locale;

public class MGestureOverlay extends GestureOverlayView implements LocationListener
{
    int screenWidth=800;
    int screenHeight=800;
    
    int screenHCenter = screenWidth/2;
    int screenVCenter = screenHeight/2;
    
    private final static String RANGE_FORMAT = "%.2f";
    private final static String BEARING_FORMAT = "%.1f";
    
    MainMapActivity mapAct;
    Location currentLocation;
    Location refPoint;
    private LocationManager locManager;
    String locFile = "Last-Location";
    long lastSave;

    static String UNITS = "units";
    static String RET_SIZE = "retsize";;
    static String RET_COLOR = "retcolor";
    static String BEARING = "bearing";
    static String BRNG_UNITS = "brng_units";

    float range;
    float brng;
    String units = "m";
    SharedPreferences sp;
    
    final static int RET_SIZE_SMALL = 18;
    final static int RET_SIZE_MED = 36;
    final static int RET_SIZE_LARGE = 60;
    final static int RET_SIZE_HUGE = 100;
    
    final static int OFFSET_SIZE_SMALL = 138;
    final static int OFFSET_SIZE_MED = 184;
    final static int OFFSET_SIZE_LARGE = 252;
    final static int OFFSET_SIZE_HUGE = 320;
    
    final static float DOT_SIZE_SMALL = 1.0f;
    final static float DOT_SIZE_MED = 2.0f;
    final static float DOT_SIZE_LARGE = 4.0f;
    final static float DOT_SIZE_HUGE = 8.0f;
    
    final static float TEXT_SIZE_SMALL = 28.0f;
    final static float TEXT_SIZE_MED = 40.0f;
    final static float TEXT_SIZE_LARGE = 56.0f;
    final static float TEXT_SIZE_HUGE = 76.0f;
    
    int ret_size = RET_SIZE_MED;
    float dot_size = DOT_SIZE_MED;
    float text_size = TEXT_SIZE_MED;
    int offset = OFFSET_SIZE_MED;
    Paint paint;
    Point p;
    Location trgt;
    Handler handler;
    Update update;
    
    public MGestureOverlay(MainMapActivity mapAct)
    {
        super(mapAct);
        this.mapAct=mapAct;
        setGestureVisible(false);
        computeDisplayMetrics();
        
        locManager = (LocationManager) mapAct.getSystemService(Context.LOCATION_SERVICE);
        try
        {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0.01f, this);
        }
        catch(SecurityException e)
        {
            System.out.println(e.toString());
        }

        currentLocation = getCurrent();
        sp = PreferenceManager.getDefaultSharedPreferences(mapAct);
        paint = new Paint();
        p = new Point(screenHCenter, screenVCenter);
        trgt = new Location("marks");
        handler = new Handler(mapAct.getMainLooper());
        update = new Update();
    }
    
    @Override
    public void draw(Canvas canvas)
    {
      //  System.out.println("gov::draw() ........");
        super.draw(canvas);
        int clr = sp.getInt(RET_COLOR, 1);
        paint.setColor(ColorAdapter.getColor(clr));
        paint.setTextSize(text_size);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        switch(sp.getInt(RET_SIZE, 1))
        {
            case 0:
                ret_size = RET_SIZE_SMALL;
                dot_size = DOT_SIZE_SMALL;
                text_size = TEXT_SIZE_SMALL;
                offset = OFFSET_SIZE_SMALL;
                break;
            case 1:
                ret_size = RET_SIZE_MED;
                dot_size = DOT_SIZE_MED;
                text_size = TEXT_SIZE_MED;
                offset = OFFSET_SIZE_MED;
                break;
            case 2:
                ret_size = RET_SIZE_LARGE;
                dot_size = DOT_SIZE_LARGE;
                text_size = TEXT_SIZE_LARGE;
                offset = OFFSET_SIZE_LARGE;
                break;
            case 3:
                ret_size = RET_SIZE_HUGE;
                dot_size = DOT_SIZE_HUGE;
                text_size = TEXT_SIZE_HUGE;
                offset = OFFSET_SIZE_HUGE;
                break;
                
        }
        computeRangeAndBearing();
        canvas.drawLine(screenHCenter-ret_size*2, screenVCenter, screenHCenter+ret_size*2, screenVCenter, paint);
        canvas.drawLine(screenHCenter, screenVCenter-ret_size*2, screenHCenter, screenVCenter+ret_size, paint);
        canvas.drawCircle(screenHCenter, screenVCenter, dot_size , paint);

        units = sp.getString(UNITS, "m");
        String abr = UnitConvertor.getAbbr(units);
        double rng = UnitConvertor.convertDistance(range, units);
        String r = String.format(Locale.getDefault(), RANGE_FORMAT, rng);


        String b = String.format(Locale.getDefault(), BEARING_FORMAT, brng);
        canvas.drawText("RANGE:      "+r+ " ("+abr+")", screenHCenter-offset, screenVCenter+64, paint);
        canvas.drawText("BRNG:       "+b, screenHCenter-offset, screenVCenter+154, paint);
        
        handler.postDelayed(update, 1200);
        
    }
    
    public class Update extends Thread
    {
        @Override
        public void run()
        {
            computeRangeAndBearing();
            invalidate();
            mapAct.update();
        }
    }

    
    public void computeDisplayMetrics()
     {
      //   System.out.println("gov::computeDisplayMetrics() ........");
         if (mapAct==null) return;
    	 DisplayMetrics metrics = new DisplayMetrics();
         mapAct.getWindowManager().getDefaultDisplay().getMetrics(metrics);
         screenWidth = metrics.widthPixels;
         screenHeight = metrics.heightPixels;
         screenHCenter = screenWidth/2;
         screenVCenter = (int) ((screenHeight/2)*.88);
     }
    
    void computeRangeAndBearing()
    {
      //  System.out.println("gov::computeRangeAndBearing() ........");
        if ( mapAct.map == null ) return;
        Projection proj = mapAct.map.getProjection();
        if (currentLocation == null)
        {
            System.out.println("current location is NULL !");
            return;
        }

        LatLng rPt = proj.fromScreenLocation(p);

        trgt.setLongitude(rPt.longitude);
        trgt.setLatitude(rPt.latitude);
        if ( refPoint == null )
        {
            brng = currentLocation.bearingTo(trgt);
            range = currentLocation.distanceTo(trgt);
        }
        else
        {
            brng = refPoint.bearingTo(trgt);
            range = refPoint.distanceTo(trgt);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mapAct);
        int br = sp.getInt(BEARING, 0);
        if (br > 0) brng-=getDeclination(trgt);
        int br_units = sp.getInt(BRNG_UNITS, 0);

        if (brng < 0) brng = 360+brng;
        if (br_units > 0) brng = (float) (brng*1000.0/360.0);
        
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        invalidate();
        return super.dispatchTouchEvent(event);

    }

    public float getDeclination(Location l)
    {
        return new GeomagneticField((float)l.getLatitude(), (float)l.getLongitude(), (float)l.getAltitude(), System.currentTimeMillis()).getDeclination();
    }

    
    public void onStatusChanged(String p, int i, Bundle b)
    {
        
    }
    
    public void onLocationChanged(Location loc)
    {
        currentLocation = loc;
        if ((System.currentTimeMillis() - lastSave) > 3000) saveCurrentLocation(new CurrentLocation(currentLocation));
    }
    
    public void onProviderEnabled(String s)
    {
        
    }
    
    public void onProviderDisabled(String s)
    {
        
    }
    
    public Location getCurrent()
    {
        Location l = new Location("RangeCard");
        CurrentLocation cl = getCurrentLocation();
        l.setLatitude(cl.lat);
        l.setLongitude(cl.lon);
        l.setAltitude(cl.alt);
        l.setAccuracy((float)cl.acc);
        return l;
    }
    
    public synchronized CurrentLocation getCurrentLocation()
    {
        CurrentLocation cl = new CurrentLocation();
        File dir = mapAct.getCacheDir();
   	    File file = new File(dir, locFile);
   	    try
        {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(in);
            cl = (CurrentLocation) oin.readObject();
            in.close();
            oin.close();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        return cl;
    }
    
    public synchronized void saveCurrentLocation(CurrentLocation loc)
    {
        File dir = mapAct.getCacheDir();
   	    File file = new File(dir, locFile);
        try
        {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(loc);
            oout.flush();
            oout.close();
            out.close();
        }
        catch(Exception e)
        {
            System.out.println("Error saving data! "+e.toString());
        }
        lastSave = System.currentTimeMillis();
    }
}