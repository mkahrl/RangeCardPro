package marks.rangecard.pro;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.*;
import android.view.*;

import com.berico.coords.Coordinates;
import com.google.android.gms.maps.model.*;

import java.lang.Override;
import java.util.Locale;


public class TargetActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener
{
    private final static String LL_FORMAT = "%.5f";
    private final static String ALT_FORMAT = "%.2f";
    private final static String RANGE_FORMAT = "%.2f";
    private final static String ELV_FORMAT = "%.3f";
    private final static String BRG_FORMAT = "%.1f";
    static String UNITS = "units";
    
    public TargetInfo info;
    String units = "m";
    String origMgrs="";
    int geosystem = 0;//LatLon
    static String GEO_SYSTEM = "geo_system";
    
    EditText name;
    EditText alt;
    EditText ln;
    EditText lt;
    EditText windage;
    EditText elevation;
    EditText notes;
    EditText mgrs;
    Spinner targColor;
    Intent intent;

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
    DataManager dataManager;
    boolean isRef=false;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataManager = DataManager.getInstance(this);
        intent = getIntent();
        String id = intent.getStringExtra("id");
        System.out.println("target id: "+id);
        if (id!=null) info = dataManager.getTargetById(id);
        else info = new TargetInfo();

        if (info.type < 1) setContentView(R.layout.edit_target);
        else setContentView(R.layout.edit_pos);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        units = sp.getString(UNITS, units);

        name = (EditText) findViewById(R.id.name);
        String nm = info.name;
        name.setText(nm, TextView.BufferType.EDITABLE);
        String eabr = UnitConvertor.getAbbr(units);

        TextView altlbl = (TextView) findViewById(R.id.alt_label);
        altlbl.setText(altlbl.getText().toString()+" ("+eabr+") ");

        units = sp.getString(UNITS, "m");
        geosystem = sp.getInt(GEO_SYSTEM, 0);

        alt = (EditText) findViewById(R.id.alt);
        double aalt = UnitConvertor.convertDistance((double) info.alt, units);
        String s = String.format(Locale.getDefault(),ALT_FORMAT, aalt);
        alt.setText(s, TextView.BufferType.EDITABLE);
        
        ln = (EditText) findViewById(R.id.lon);
        s = String.format(Locale.getDefault(), LL_FORMAT, info.lon);
        ln.setText(s, TextView.BufferType.EDITABLE);
        
        lt = (EditText) findViewById(R.id.lat);
        s = String.format(Locale.getDefault(), LL_FORMAT, info.lat);
        lt.setText(s, TextView.BufferType.EDITABLE);

        mgrs = (EditText) findViewById(R.id.mgrs);
        origMgrs = Coordinates.mgrsFromLatLon(info.lat, info.lon);
        mgrs.setText(origMgrs);

        units = sp.getString(UNITS, "m");
        String abr = UnitConvertor.getAbbr(units);
        double rang = UnitConvertor.convertDistance((double) info.rng, units);
        /////
        TextView rng = (TextView) findViewById(R.id.range);
        s = String.format(Locale.getDefault(), RANGE_FORMAT, rang);

        s = "Range:  "+s+" "+abr;
        rng.setText(s);
        
        TextView brng = (TextView) findViewById(R.id.bearing);
        float br = info.brng;
        if ( br < 0 ) br+=360;
        s = String.format(Locale.getDefault(), BRG_FORMAT, br);
        s = "Bearing: "+s;
        brng.setText(s);

        TextView elv = (TextView) findViewById(R.id.elv);
        s = String.format(Locale.getDefault(), ELV_FORMAT, info.elv);
        s = "Look Angle:  "+s;
        elv.setText(s);

        if ( geosystem > 0)
        {
            findViewById(R.id.mgrs_grp).setVisibility(View.VISIBLE);
            findViewById(R.id.lat_grp).setVisibility(View.GONE);
            findViewById(R.id.lon_grp).setVisibility(View.GONE);
        }
        else
        {
            findViewById(R.id.mgrs_grp).setVisibility(View.GONE);
            findViewById(R.id.lat_grp).setVisibility(View.VISIBLE);
            findViewById(R.id.lon_grp).setVisibility(View.VISIBLE);
        }

        if (info.type < 1)
        {
            windage = (EditText) findViewById(R.id.windage);
            windage.setText(info.windage);

            elevation = (EditText) findViewById(R.id.elevation);
            elevation.setText(info.elevation);
        }
        
        targColor = (Spinner)findViewById(R.id.target_color);
        String[] rclrs = getResources().getStringArray(R.array.target_colors);
        ArrayAdapter clrs = new TargetColorAdapter(this,  R.layout.coloritem, rclrs);
        targColor.setAdapter(clrs);
        targColor.setSelection(info.color);
        targColor.setOnItemSelectedListener(this);
        
        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.go_to).setOnClickListener(this);
        findViewById(R.id.delete).setOnClickListener(this);

        TextView ttype = (TextView) findViewById(R.id.target_type);
        TextView clabel = (TextView) findViewById(R.id.target_color_lbl);
        notes = (EditText) findViewById(R.id.notes);
        notes.setText(info.notes);

        if (info.type > 0 )
        {
            ttype.setText("Position");
            targColor.setVisibility(View.GONE);
            clabel.setVisibility(View.GONE);
        }
        else ttype.setText("Target");

        String rpt = sp.getString("refPoint", "gps");
        if ( rpt.equals(info.id))
        {
            CheckBox refpt = (CheckBox)findViewById(R.id.base_ref);
            refpt.setChecked(true);
            refpt.setOnClickListener(this);
            isRef=true;
        }
        if (isRef) findViewById(R.id.delete).setVisibility(View.GONE);

    }

    @Override
    public boolean onNavigateUp ()
    {
        save();
        return super.onNavigateUp();
    }

    @Override
    public void onBackPressed()
    {
       // save();
        super.onBackPressed();
    }
    
    public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
    {
    }
    
    public void onNothingSelected (AdapterView<?> parent)
    {
        
    }
    
    public void onClick(View v)
    {
        Intent intent = new Intent();
        switch(v.getId())
        {
            case R.id.save:
                save();
                break;
            case R.id.go_to:
                intent.putExtra("gotoTarget", info);
                setResult(7, intent);
                finish();
                break;
            case R.id.delete:
                new DeleteTargetDialog(this).show();
                break;
           /* case R.id.base_ref:
                CheckBox refpt = (CheckBox)findViewById(R.id.base_ref);
                if ( refpt.isChecked() )
                {
                    intent = new Intent();
                    intent.putExtra("refPoint", info.id);
                    setResult(11, intent);
                    finish();
                }*/
        }
     }

    void save()
    {
        double aalt=0.0;
        try
        {
             aalt = new Double(alt.getText().toString()).doubleValue();
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        aalt = UnitConvertor.convertToMeter(aalt, units);
        info.alt = aalt;
        info.name = name.getText().toString();
        System.out.println("saving name:  "+info.name);

        if (geosystem > 0)
        {
            String nmgrs = mgrs.getText().toString();
            if (nmgrs != null)
            {
                nmgrs = nmgrs.trim();
                if (!nmgrs.equals(origMgrs))
                {
                    try {
                        double[] latlon = Coordinates.latLonFromMgrs(nmgrs);
                        info.lat = latlon[0];
                        info.lon = latlon[1];
                    }
                    catch(Exception e)
                    {
                        System.out.println("MGRS conversion failed: " +e.toString());
                        mgrs.setText(origMgrs);
                        Toast.makeText(this , "Invalid MGRS entry!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        else
        {
            try
            {
                info.lat = new Double(lt.getText().toString()).doubleValue();
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                info.lat=0;
            }
            try
            {
                info.lon = new Double(ln.getText().toString()).doubleValue();
            }
            catch(Exception e)
            {
                System.out.println(e.toString());
                info.lon=0;

            }
        }
        
        if (info.type < 1)
        {
            info.windage = windage.getText().toString();
            info.elevation = elevation.getText().toString();
        }

        info.color = targColor.getSelectedItemPosition();
        float markerColor = BitmapDescriptorFactory.HUE_RED;

        switch(info.color)
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

        info.markerColor = markerColor;
        info.notes = notes.getText().toString();
        dataManager.save();
        CheckBox refpt = (CheckBox)findViewById(R.id.base_ref);
        Intent intent = new Intent();
        if ( refpt.isChecked() )
        {
            intent.putExtra("refPoint", info.id);
        }
        else
        {
            if (isRef) intent.putExtra("clear", info.id);
        }
        setResult(11, intent);
        finish();
    }


    public void removeTarget()
    {
        Intent intent = new Intent();
        intent.putExtra("deleteTarget", info);
        setResult(9, intent);
        finish();
    }
}
