package marks.rangecard.pro;;

import java.util.*;
import java.io.*;
import android.content.Context;

public class DataStore
{
    private static DataStore instance;
    /// Singleton class, access from this static method.
    public static DataStore getInstance(Context ctx)
    {
        if (instance==null) instance = new DataStore(ctx);
        return instance;
    }
    
    private DataStore(Context ctx)
    {
    }
}