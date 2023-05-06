package marks.rangecard.pro;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.*;
import android.view.*;
import android.view.View.OnClickListener;

public class PreferencesActivity extends Activity implements AdapterView.OnItemSelectedListener
{
    Spinner mapMode;
    Spinner dUnits;
    Spinner bUnits;
    Spinner retSize;
    Spinner retColor;
    Spinner geoSystem;
    Spinner bearing;
    
    static String MAP_MODE = "map_mode";
    static String UNITS = "units";
    static String BRNG_UNITS = "brng_units";
    static String RET_SIZE = "retsize";;
    static String RET_COLOR = "retcolor";
    static String BEARING = "bearing";
    static String GEO_SYSTEM = "geo_system";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        ArrayAdapter<CharSequence> au = ArrayAdapter.createFromResource(this, R.array.map_modes, R.layout.listitem);
        au.setDropDownViewResource(R.layout.listitem);
        mapMode = (Spinner)findViewById(R.id.map_mode);
        mapMode.setAdapter(au);
        mapMode.setOnItemSelectedListener(this);
        String mode = sp.getString(MAP_MODE, "Satellite");
        int idx = getArrayIndex(R.array.map_modes, mode);
        mapMode.setSelection(idx);
        
        ArrayAdapter<CharSequence> uu = ArrayAdapter.createFromResource(this, R.array.distance_units, R.layout.listitem);
        uu.setDropDownViewResource(R.layout.listitem);
        dUnits = (Spinner)findViewById(R.id.units);
        dUnits.setAdapter(uu);
        dUnits.setOnItemSelectedListener(this);
        String un = sp.getString(UNITS, "m");
        idx = getArrayIndex(R.array.distance_units, un);
        dUnits.setSelection(idx);

        ArrayAdapter<CharSequence> bu = ArrayAdapter.createFromResource(this, R.array.brng_units, R.layout.listitem);
        bu.setDropDownViewResource(R.layout.listitem);
        bUnits = (Spinner)findViewById(R.id.brng_units);
        bUnits.setAdapter(bu);
        bUnits.setOnItemSelectedListener(this);
        idx = sp.getInt(BRNG_UNITS, 0);
        bUnits.setSelection(idx);
        
        ArrayAdapter<CharSequence> rs = ArrayAdapter.createFromResource(this, R.array.sizes, R.layout.listitem);
        rs.setDropDownViewResource(R.layout.listitem);
        retSize = (Spinner)findViewById(R.id.ret_size);
        retSize.setAdapter(rs);
        retSize.setOnItemSelectedListener(this);
        int sz = sp.getInt(RET_SIZE, 1);
        retSize.setSelection(sz);
        
        retColor = (Spinner)findViewById(R.id.ret_color);
        String[] rclrs = getResources().getStringArray(R.array.colors);
        ArrayAdapter clrs = new ColorAdapter(this,  R.layout.coloritem, rclrs);
        retColor.setAdapter(clrs);
        retColor.setSelection(sp.getInt(RET_COLOR, 1));
        retColor.setOnItemSelectedListener(this);

        bearing = (Spinner)findViewById(R.id.bearing);
        ArrayAdapter<CharSequence> brs = ArrayAdapter.createFromResource(this, R.array.bearing, R.layout.listitem);
        brs.setDropDownViewResource(R.layout.listitem);
        bearing.setAdapter(brs);
        bearing.setSelection(sp.getInt(BEARING, 0));
        bearing.setOnItemSelectedListener(this);

        geoSystem = (Spinner)findViewById(R.id.geo_units);
        ArrayAdapter<CharSequence> gss = ArrayAdapter.createFromResource(this, R.array.geo_systems, R.layout.listitem);
        gss.setDropDownViewResource(R.layout.listitem);
        geoSystem.setAdapter(gss);
        geoSystem.setSelection(sp.getInt(GEO_SYSTEM, 0));
        geoSystem.setOnItemSelectedListener(this);

    }
    
    int getArrayIndex(int res, String item)
    {
        String[] sa = getResources().getStringArray(res);
        
        for (int i=0; i<sa.length; i++)
        {
            if ( sa[i].equals(item)) return i;
        }
        return 0;
    }
    
    public void onNothingSelected (AdapterView<?> parent)
    {
        
    }
    
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor ed = sp.edit();
        
        if ( parent.getId() == R.id.map_mode )
        {
            String[] modes = getResources().getStringArray(R.array.map_modes);
            String mode = modes[position];
            ed.putString(MAP_MODE, mode);
            ed.commit();

        }
        
        if ( parent.getId() == R.id.units )
        {
            String[] units = getResources().getStringArray(R.array.distance_units);
            String unit = units[position];
            ed.putString(UNITS, unit);
            ed.commit();
            
        }

        if ( parent.getId() == R.id.brng_units )
        {
            ed.putInt(BRNG_UNITS, position);
            ed.commit();

        }
        
        if ( parent.getId() == R.id.ret_color )
        {
            ed.putInt(RET_COLOR, position);
            ed.commit();
            
        }
        
        if ( parent.getId() == R.id.ret_size )
        {
            ed.putInt(RET_SIZE, position);
            ed.commit();
            
        }

        if ( parent.getId() == R.id.bearing )
        {
            ed.putInt(BEARING, position);
            ed.commit();

        }

        if ( parent.getId() == R.id.geo_units )
        {
            ed.putInt(GEO_SYSTEM, position);
            ed.commit();

        }

    }
    
}
