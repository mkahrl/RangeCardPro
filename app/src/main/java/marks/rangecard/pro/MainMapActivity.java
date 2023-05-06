package marks.rangecard.pro;

import android.*;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.os.Handler;
import android.hardware.GeomagneticField;
import android.preference.PreferenceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import android.location.Location;
import com.google.android.gms.maps.model.*;
import android.view.*;
import android.net.Uri;

import java.lang.Override;
import java.util.*;
import android.widget.*;

import android.content.pm.*;
import android.provider.*;


public class MainMapActivity extends Activity implements GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener, PopupMenu.OnMenuItemClickListener
{
    GoogleMap map;
    com.google.android.gms.maps.MapView mapView;
    MGestureOverlay gov;
    DataManager dataManager;

    private final static String ZOOM = "ZOOM";
    private final static String LAT = "LAT";
    private final static String LON = "LON";
    static String BEARING = "bearing";
    static String MAP_MODE = "map_mode";
    static String CURRENT_LOC = "current_loc";
    static String BRNG_UNITS = "brng_units";

    final static int azure=0;
    final static int blue=1;
    final static int cyan=2;
    final static int green=3;
    final static int magenta=4;
    final static int orange=5;
    final static int red=6;
    final static int rose=7;
    final static int violet=8;
    final static int yellow=9;
    
    float zoom = -1.0f;
    float lat = -1.0f;
    float lon = -1.0f;
    LatLng ll;
    String refPoint = "gps";
    final static int PERM_FINE_LOC = 7077; // app defined prmission code
    Bundle state;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate ....");
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapToolbarEnabled(false);

        System.out.println("creating MapView.");
        mapView = new com.google.android.gms.maps.MapView(this, options);
        mapView.onCreate(savedInstanceState);
        System.out.println("getMapAsync .... ");
        mapView.getMapAsync(new MapReadyCallback(savedInstanceState));

    }

    @Override
    public void onPause()
    {
        System.out.println("onPause ....");
        if ( map != null) {
            zoom = map.getCameraPosition().zoom;
            ll = map.getCameraPosition().target;
        }
        super.onPause();
        mapView.onPause();
    }

    class MapReadyCallback implements OnMapReadyCallback
    {
        public MapReadyCallback(Bundle state)
        {
            MainMapActivity.this.state = state;
        }
        public void onMapReady (GoogleMap googleMap)
        {
            map = googleMap;
            System.out.println("onMapReady....");
            if ( map == null )
            {
                System.out.println("Map is NULL!!....");
                return;
            }

            System.out.println("checking permissions....");
            ///// implement new permission checks for Android M
            int permCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);

           ////  check if the permission has already been granted
            if ( permCheck != PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("requesting permissions....");
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERM_FINE_LOC);
            }
            else init();

        }
    }

    void init()
    {
        System.out.println("setting location enabled...");
        map.setMyLocationEnabled(true);
        gov = new MGestureOverlay(MainMapActivity.this);
        gov.addView(mapView);
        setContentView(gov);
        gov.computeDisplayMetrics();
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnInfoWindowClickListener(MainMapActivity.this);
        map.setOnMarkerDragListener(MainMapActivity.this);
        map.setInfoWindowAdapter(MainMapActivity.this);
        System.out.println("initialize map...");
        MapsInitializer.initialize(MainMapActivity.this);
        System.out.println("initialize data manager...");
        dataManager = DataManager.getInstance(MainMapActivity.this);
        dataManager.setMap(map);
        System.out.println("creating Gesture overlay.");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainMapActivity.this);
        String rpt = sp.getString("refPoint", "gps");
        setReference(rpt, false);
        updateMapMode();
        if (state!=null)
        {
            System.out.println("Restoring from instanceState ........  ");
            float zoom = state.getFloat("zoom", 0.0f);
            float lat = state.getFloat("lat", -1.0f);
            float lon = state.getFloat("lon", -1.0f);
            snapToLocation(lat, lon, zoom);
        }
        else
        {
            System.out.println("Restoring from preferences ........  ");
            zoom = sp.getFloat(ZOOM, -1.0f);
            lat = sp.getFloat(LAT, -1.0f);
            lon = sp.getFloat(LON, -1.0f);

            if (( zoom > 1.9) && (lat > 0)) snapToLocation(lat, lon, zoom);
            else panToCurrentLocation();
        }
        checkGPS();
        ///
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        int permCheck = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        ////  check if the permission has already been granted
        if ( permCheck == PackageManager.PERMISSION_GRANTED) init();
        else Toast.makeText(this , "No GPS Permissions !!", Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        if ( map != null) {
            zoom = map.getCameraPosition().zoom;
            ll = map.getCameraPosition().target;
        }
        outState.putFloat("zoom", zoom);
        outState.putFloat("lat", (float)ll.latitude);
        outState.putFloat( "lon", (float)ll.longitude);
        mapView.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void finishFromChild(Activity act)
    {
        super.finishFromChild(act);
        System.out.println("finishFromChild .....");
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        update();
        int id = item.getItemId();
        Intent intent = new Intent();

        switch( id )
        {
            case R.id.preferences:
                intent.setClassName("marks.rangecard.pro", "marks.rangecard.pro.PreferencesActivity");
                startActivity(intent);
                return true;
        
            case R.id.add_target:
                addTarget();
                return true;

            case R.id.add_position:
                addPosition();
                return true;


            case R.id.add_current_loc_pos:
                panToCurrentLocation();
                addCurrentLocAsPos();
                return true;

            case R.id.add_current_loc_target:
                panToCurrentLocation();
                addCurrentLocAsTarg();
                return true;

            case R.id.targetlist:
                updateTargetList();
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainMapActivity.this);
                SharedPreferences.Editor ed = sp.edit();
                sp = PreferenceManager.getDefaultSharedPreferences(this);
                ed = sp.edit();
                ed.putString(CURRENT_LOC, new CurrentLocation(gov.currentLocation).jsonString());
                ed.commit();
                intent = new Intent();
                intent.setClassName("marks.rangecard.pro", "marks.rangecard.pro.TargetListActivity");
                startActivityForResult(intent, 0);
                return true;

            case R.id.help:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://kahrlconsulting.com/RangeCardPro.html")));
                return true;

            case R.id.about:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kahrlconsulting.com/license.html")));
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    void checkGPS()
    {
        if (!GPSEnabled()) new EnableGPSDialog(this).show();
    }

    boolean GPSEnabled()
    {
        String locProviders=Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        System.out.println("LocationProviders: "+locProviders);
        if (locProviders.contains("gps")) return true;
        else return false;
    }

    private void updateTargetList()
    {
        System.out.println("udateTargetList ....");
        Vector<TargetInfo> targetList = dataManager.getTargets();
        Enumeration<TargetInfo> tlist = targetList.elements();
        while (tlist.hasMoreElements())
        {
            TargetInfo ti = tlist.nextElement();
            Marker m = dataManager.getMarker(ti);
            float[] rb = getRangeAndBearing(m);
            ti.rng = rb[0];
            ti.brng = rb[1];
            ti.elv = getElevation(m);
        }
    }
    
    void panToCurrentLocation()
    {
        System.out.println("panToCurrentLocation() ....");
        if ( map == null ) return;
        LatLng loc = new LatLng(gov.currentLocation.getLatitude(), gov.currentLocation.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 18.0f));
        
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        System.out.println("onActivityResult ...."+resultCode);
        if ( resultCode == 7 ) // goto
        {
            TargetInfo tf = (TargetInfo)data.getSerializableExtra("gotoTarget");
            if ( tf != null )
            {
                animateToLocation((float)tf.lat, (float)tf.lon, 18.0f);
            }
        }

        if ( resultCode == 9 ) //delete
        {
            TargetInfo tf = (TargetInfo)data.getSerializableExtra("deleteTarget");
            if ( tf != null )
            {
                TargetInfo ti = dataManager.getTargetById(tf.id);
                Marker m = dataManager.getMarker(ti);
                dataManager.removeTarget(m);
                m.remove();
                Toast.makeText(this , "Target Deleted.", Toast.LENGTH_LONG).show();

            }
        }

        if ( resultCode == 11 )// save
        {
            System.out.println("Clearing map....");
            String refId = data.getStringExtra("refPoint");

            if ( refId != null )
            {
                setReference(refId, true);
                TargetInfo ti = dataManager.getTargetById(refId);
                animateToLocation((float)ti.lat, (float)ti.lon, 18.0f);
            }
            map.clear();
            dataManager.setMap(map);

            String clr = data.getStringExtra("clear");
            if ( clr != null )
            {
                setReference("gps", true);
            }
        }

        if ( resultCode == 18 )
        {
            setReference("gps", true);// setref pt tp gps
            panToCurrentLocation();
        }

    }

    void setReference(String id, boolean show)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        if (id.equals("gps"))
        {
            ed.putString("refPoint", "gps");
            ed.commit();
            if (show) Toast.makeText(this , "Ref. set to GPS", Toast.LENGTH_LONG).show();
            gov.refPoint=null;

            return;
        }
        TargetInfo ti = dataManager.getTargetById(id);
        if ( ti != null )
        {
            gov.refPoint = new CurrentLocation(ti).getLocation();
            ed.putString("refPoint", ti.id);
            ed.commit();

            if (show) Toast.makeText(this , "Ref. set to: "+ti.name, Toast.LENGTH_LONG).show();
        }
        updateTargetList();
    }

    void animateToLocation(float lat, float lon, float zoom)
    {
        System.out.println("animateToLocation ....");
        if ( map == null) return;
        LatLng loc = new LatLng(lat, lon);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
    }
    
    void snapToLocation(float lat, float lon, float zoom)
    {
        System.out.println("snapToLocation ....");
        if ( map == null) return;
        LatLng loc = new LatLng(lat, lon);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoom));
       
    }
    
    void updateMapMode()
    {
        System.out.println("updateMapMode() ....");
        if ( map == null) return;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String mode = sp.getString(MAP_MODE, "Satellite");
        
        if ( mode.equals("Normal"))
        {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return;
        }
            
        if ( mode.equals("Satellite"))
        {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return;
        }
        
        if ( mode.equals("Terrain"))
        {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return;
        }
            
        if ( mode.equals("Hybrid"))
        {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return;
        }
    }

    public void update()
    {
        System.out.println("updateMode() ....");
        if ( map == null) return;
        zoom = map.getCameraPosition().zoom;
        ll = map.getCameraPosition().target;
        lat = (float) ll.latitude;
        lon = (float) ll.longitude;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        ed.putFloat(ZOOM, zoom);
        ed.putFloat(LAT, lat);
        ed.putFloat(LON, lon);
        ed.commit();
    }

    
    @Override
    public void onResume()
    {
        super.onResume();
        System.out.println("onResume..");
        updateMapMode();
        mapView.onResume();
    }

    public void addCurrentLocAsTarg()
    {
        System.out.println("addCurrentLocAsTarget");
        String name = "Target"+dataManager.getNumberOfTargets();

        int color = red;
        float markerColor = BitmapDescriptorFactory.HUE_RED;

        if ( dataManager.getNumberOfTargets() < 10 ) color = dataManager.getNumberOfTargets();
        else color = dataManager.getNumberOfTargets() % 10;

        markerColor = getMarkerColor(color);

        MarkerOptions markerOptions = new MarkerOptions();
        Projection proj = map.getProjection();
        Point p = new Point(gov.screenHCenter, gov.screenVCenter);
        LatLng pt = new LatLng(gov.currentLocation.getLatitude(), gov.currentLocation.getLongitude());
        markerOptions.position(pt);
        markerOptions.title(name);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor));
        markerOptions.draggable(true);
        Marker marker = map.addMarker(markerOptions);
        TargetInfo info = new TargetInfo(pt.latitude, pt.longitude, 0.0, name);
        info.color = color;
        info.alt = gov.currentLocation.getAltitude();
        info.markerColor = markerColor;
        dataManager.addTarget(info, marker);
    }


    public void addCurrentLocAsPos()
    {
        System.out.println("addCurrentLocAsPosition");
        String name = "Pos-"+dataManager.getNumberOfTargets();
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng pt = new LatLng(gov.currentLocation.getLatitude(), gov.currentLocation.getLongitude());
        markerOptions.position(pt);
        markerOptions.title(name);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.frienddroidx));
        markerOptions.draggable(true);
        Marker marker = map.addMarker(markerOptions);
        TargetInfo info = new TargetInfo(pt.latitude, pt.longitude, 0.0, name);
        info.alt = gov.currentLocation.getAltitude();
        info.type=1;
        dataManager.addTarget(info, marker);
    }
    
    public void addTarget()
    {
        String name = "Target"+dataManager.getNumberOfTargets();
        
        int color = red;
        float markerColor = BitmapDescriptorFactory.HUE_RED;

        if ( dataManager.getNumberOfTargets() < 10 ) color = dataManager.getNumberOfTargets();
        else color = dataManager.getNumberOfTargets() % 10;
        
        markerColor = getMarkerColor(color);

        MarkerOptions markerOptions = new MarkerOptions();
        Projection proj = map.getProjection();
        Point p = new Point(gov.screenHCenter, gov.screenVCenter);
        LatLng pt = proj.fromScreenLocation(p);
        markerOptions.position(pt);
        markerOptions.title(name);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor));
        markerOptions.draggable(true);
        Marker marker = map.addMarker(markerOptions);
        TargetInfo info = new TargetInfo(pt.latitude, pt.longitude, 0.0, name);
        info.color = color;
        info.markerColor = markerColor;
        dataManager.addTarget(info, marker);
    }

    public void addPosition()
    {
        System.out.println("addPosition");
        String name = "Pos-"+dataManager.getNumberOfTargets();

        MarkerOptions markerOptions = new MarkerOptions();
        Projection proj = map.getProjection();
        Point p = new Point(gov.screenHCenter, gov.screenVCenter);
        LatLng pt = proj.fromScreenLocation(p);
        markerOptions.position(pt);
        markerOptions.title(name);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.frienddroidx));
        markerOptions.draggable(true);
        Marker marker = map.addMarker(markerOptions);
        TargetInfo info = new TargetInfo(pt.latitude, pt.longitude, 0.0, name);
        info.type=1;
        dataManager.addTarget(info, marker);
    }

    float getMarkerColor(int color)
    {
        float markerColor = BitmapDescriptorFactory.HUE_RED;
        switch(color)
        {
            case azure:
                markerColor=BitmapDescriptorFactory.HUE_AZURE;
                break;
            case blue:
                markerColor=BitmapDescriptorFactory.HUE_BLUE;
                break;
            case cyan:
                markerColor=BitmapDescriptorFactory.HUE_CYAN;
                break;
            case green:
                markerColor=BitmapDescriptorFactory.HUE_GREEN;
                break;
            case magenta:
                markerColor=BitmapDescriptorFactory.HUE_MAGENTA;
                break;
            case orange:
                markerColor=BitmapDescriptorFactory.HUE_ORANGE;
                break;
            case red:
                markerColor=BitmapDescriptorFactory.HUE_RED;
                break;
            case rose:
                markerColor=BitmapDescriptorFactory.HUE_ROSE;
                break;
            case violet:
                markerColor=BitmapDescriptorFactory.HUE_VIOLET;
                break;
            case yellow:
                markerColor=BitmapDescriptorFactory.HUE_YELLOW;
                break;
        }
        return markerColor;
    }

    public float getElevation(Marker marker)
    {
        TargetInfo ti = dataManager.getTargetInfo(marker);
        double dalt=0;
        double elv = 0;
        float rng = 0.0f;
        LatLng mpos = marker.getPosition();

        Location pos = new Location("");
        pos.setLongitude(mpos.longitude);
        pos.setLatitude(mpos.latitude);

        if ( gov.refPoint == null )
        {
            rng = gov.currentLocation.distanceTo(pos);
            if ( rng >  0)
            {
                dalt = ti.alt - gov.currentLocation.getAltitude();
                elv = java.lang.Math.atan(dalt/rng);
            }

        }
        else
        {
            rng = gov.refPoint.distanceTo(pos);
            if ( rng > 0)
            {
                dalt = ti.alt - gov.refPoint.getAltitude();
                elv = java.lang.Math.atan(dalt/rng);
            }
         }

        elv = elv*(180/3.1415927);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int br_units = sp.getInt(BRNG_UNITS, 0);
        if (br_units > 0) elv = (float) (elv*1000.0/360.0);
        return (float) elv;
    }
    
    public float[] getRangeAndBearing(Marker marker)
    {
        float[] rngBrng = new float[2];
        LatLng mpos = marker.getPosition();
        
        Location pos = new Location("");
        pos.setLongitude(mpos.longitude);
        pos.setLatitude(mpos.latitude);

        if ( gov.refPoint == null ) {
            rngBrng[0] = gov.currentLocation.distanceTo(pos);
            rngBrng[1] = gov.currentLocation.bearingTo(pos);
        }
        else
        {
            rngBrng[0] = gov.refPoint.distanceTo(pos);
            rngBrng[1] = gov.refPoint.bearingTo(pos);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        int br = sp.getInt(BEARING, 0);
        if (br > 0) rngBrng[1]-=getDeclination(pos);
        int br_units = sp.getInt(BRNG_UNITS, 0);

        if (rngBrng[1] < 0) rngBrng[1] = 360+rngBrng[1];
        if (br_units > 0) rngBrng[1] = (float) (rngBrng[1]*1000.0/360.0);

        return rngBrng;
    }

    public float getDeclination(Location l)
    {
        return new GeomagneticField((float)l.getLatitude(), (float)l.getLongitude(), (float)l.getAltitude(), System.currentTimeMillis()).getDeclination();
    }
    
    
    public View getInfoContents(Marker marker)
    {
        return new TargetInfoWindow(this, marker);
    }
    
    public View getInfoWindow(Marker marker)
    {
        return null;
    }
    
    public void onInfoWindowClick(Marker marker)
    {
        TargetInfo info = dataManager.getTargetInfo(marker);
        float[] rb = getRangeAndBearing(marker);
        info.rng = rb[0];
        info.brng = rb[1];
        info.elv = getElevation(marker);
        showTargetDetail(info);
    }

    public void showTargetDetail(TargetInfo info)
    {
        Intent intent = new Intent();
        intent.setClassName("marks.rangecard.pro", "marks.rangecard.pro.TargetActivity");
        intent.putExtra("id", info.id);
        startActivityForResult(intent, 11);
    }
    
    public void onMarkerDrag(Marker marker)
    {
        
    }
    
    public void onMarkerDragStart(Marker marker)
    {
        
    }

    public boolean onMenuItemClick (MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.add_target:
                addTarget();
                return true;
            case R.id.add_position:
                addPosition();
                return true;
            case R.id.add_current_loc_pos:
                panToCurrentLocation();
                addCurrentLocAsPos();
                return true;
            case R.id.add_current_loc_target:
                panToCurrentLocation();
                addCurrentLocAsTarg();
                return true;
        }
        return false;
    }

    public void onMarkerDragEnd(Marker marker)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String ref = sp.getString("refPoint", "gps");
        LatLng pos = marker.getPosition();
        TargetInfo info = dataManager.getTargetInfo(marker);
        info.lon = pos.longitude;
        info.lat = pos.latitude;
        if ( ref.equals(info.id))
        {
            setReference(info.id, false);
            updateTargetList();
        }
        dataManager.save();
        marker.hideInfoWindow();
        marker.showInfoWindow();
    }
}
