package marks.rangecard.pro;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import android.view.*;

public class TargetListActivity extends Activity
{
    ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.target_list);
        list = (ListView) findViewById(R.id.targetslist);
        list.setAdapter(new TargetAdapter(this));
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        System.out.println("TragetListActivity:onActivityResult ...."+resultCode);
        TargetAdapter ta = (TargetAdapter) list.getAdapter();
        setResult(resultCode, data);
        finish();
    }

}
