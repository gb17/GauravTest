package inc.gb.gauravtest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GB on 1/21/16.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static String DB_NAME = "MYDB.sqlite";
    public static String TABLE_NAME = "TBSCHEMES";
    public static String SCHEME_DATE = "scheme_last_date";

    public static String BRAND = "brand";
    public static String MANUFACTURER = "manufacturer";
    public static String MRP = "mrp";
    public static String SCHEME = "scheme";
    public static String ORDER_QUANTITY = "order_quantity";



    public static int DB_VERSION = 1;

    Context context;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY  AUTOINCREMENT, brand text, manufacturer text, margin text, molecule text, mrp text, order_quantity text, product_id text, scheme text, scheme_last_date  text, substitutes text)";
        sqLiteDatabase.execSQL(query1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}