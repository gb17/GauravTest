package inc.gb.gauravtest;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

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

        new DemoAsync().execute("http://188.166.204.35/mobile-api/v3/schemes");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

    class DemoAsync extends AsyncTask<String, Integer, JSONObject> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle("Loading....");
            dialog.setMessage("Please Wait....");
            dialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            String json;

            JSONObject jsonObject = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);

                connection.connect();
                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "n");
                }
                is.close();
                json = sb.toString();

                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                JSONObject newObject = jsonObject.getJSONObject("data");

                for (Iterator<String> iter = newObject.keys(); iter.hasNext(); ) {
                    String key = iter.next();
                    JSONObject object = newObject.getJSONObject(key);
                    ContentValues values = new ContentValues();
                    for (Iterator<String> iterval = object.keys(); iterval.hasNext(); ) {
                        String newkey = iterval.next();

                        Object value = object.get(newkey);
                        if (!object.isNull(newkey)) {
                            if (newkey.contains("-"))
                                newkey = newkey.replace("-", "_");

                            if (value instanceof JSONArray) {
                                JSONArray keyStr = (JSONArray) value;
                                values.put(newkey, keyStr.toString());
                            } else {
                                String keyStr = (String) value;
                                values.put(newkey, keyStr);
                            }
                        }
                    }
                    SQLiteDatabase db = getWritableDatabase();
                    db.insert(DBHandler.TABLE_NAME, null, values);

                    System.out.print("Object " + object);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MyInterface fragment = new MainActivity.PlaceholderFragment();
            fragment.updateList();
            dialog.dismiss();
        }

    }
}