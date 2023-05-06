package marks.rangecard.pro;

import android.app.Activity;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.*;
import android.view.View;
import android.content.Intent;
import com.berico.coords.Coordinates;

public class GPSActivity extends Activity implements View.OnClickListener
{
    DataManager dataManager;

    private final static String LL_FORMAT = "%.5f";
    private final static String ALT_FORMAT = "%.2f";
    static String CURRENT_LOC = "current_loc";
    static String UNITS = "units";
    String units = "m";
    SharedPreferences sp;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.current_loc);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        units = sp.getString(UNITS, "m");

        String data = sp.getString(CURRENT_LOC, "");
        CurrentLocation cloc = new CurrentLocation();
        try
        {
            cloc = new CurrentLocation(data);
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }

        TextView alt = (TextView) findViewById(R.id.alt);
        double aalt = UnitConvertor.convertDistance((double) cloc.alt, units);

        String abr = UnitConvertor.getAbbr(units);

        String s = String.format(ALT_FORMAT, aalt);
        s = s+" "+abr;
        alt.setText(s);

        TextView ln = (TextView) findViewById(R.id.lon);
        s = String.format(LL_FORMAT, cloc.lon);
        ln.setText(s);

        TextView lt = (TextView) findViewById(R.id.lat);
        s = String.format(LL_FORMAT, cloc.lat);
        lt.setText(s);

        TextView mgrs = (TextView) findViewById(R.id.mgrs);
        String ms= Coordinates.mgrsFromLatLon(cloc.lat, cloc.lon);
        mgrs.setText(ms);


        TextView acc = (TextView) findViewById(R.id.acc);
        double acu = UnitConvertor.convertDistance((double) cloc.acc, units);
        s = String.format(ALT_FORMAT, acu);
        s = s+" "+abr;
        acc.setText(s);

        TextView tm = (TextView) findViewById(R.id.time);
        tm.setText(cloc.getTime());
        CheckBox refpt = (CheckBox)findViewById(R.id.ref_pt);
        refpt.setOnClickListener(this);
        String rpt = sp.getString("refPoint", "gps");

        if ( rpt.equals("gps"))  refpt.setChecked(true);

    }

    public void onClick(View v)
    {
        CheckBox refpt = (CheckBox)findViewById(R.id.ref_pt);
        Intent intent = new Intent();
        if ( refpt.isChecked() )
        {
            intent.putExtra("refPoint", "gps");
            setResult(18, intent);
            finish();
        }

    }
}
