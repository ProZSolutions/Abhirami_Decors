package in.proz.adamd.AttendanceSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.AttendanceUploadModal.GeoModal;

public class AttendanceListLocation extends SQLiteOpenHelper {
    public static final String DB_NAME="AttendanceListLocation";
    public static final String DB_Table_name="AttendanceListLocation";
    public static  final String ID="id";
    public static  final String Date="date";
    public static  final String Sync_id="sync_id";
    public static final String LAt ="lat";
    public static final String UUID ="uuid";
    public static final String Lng ="lng";
    public static final String Time ="time";
    public void deleteAll(Context context){
        Log.d("mainDropList"," all deleted ");
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }
    public List<GeoModal> selectAllData(){
        List<GeoModal> geoModals = new ArrayList<>();
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select  * from " + DB_Table_name,null);
        Log.d("GEOTAG"," count as "+c.getCount());
        if(c!=null){
            if(c.getCount()!=0){
                if(c.moveToFirst()){
                    do{

                        Double sync_id =0.0;
                        Double lat =0.0,lng = 0.0;
                        Log.d("GEOTAG"," syn "+c.getString(c.getColumnIndex("sync_id"))+" lat b"+
                                c.getString(c.getColumnIndex("lat"))+" lng "+c.getString(c.getColumnIndex("lng")));
                        if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("sync_id")))){
                            if(c.getString(c.getColumnIndex("sync_id"))!=null){
                                sync_id =  Double.parseDouble(c.getString(c.getColumnIndex("sync_id")));
                            }
                        }
                        if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("lat")))){
                            if(c.getString(c.getColumnIndex("lat"))!=null) {
                                lat = Double.parseDouble(c.getString(c.getColumnIndex("lat")));
                            }
                        }
                        if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("lng")))){
                            if(c.getString(c.getColumnIndex("lng"))!=null) {
                                lng = Double.parseDouble(c.getString(c.getColumnIndex("lng")));
                            }
                        }
                        String date = c.getString(c.getColumnIndex("date"))+" "+c.getString(c.getColumnIndex("time"));
                        Log.d("GEOTAG"," lat "+lat+" lng  "+lng+" sync id "+sync_id);

                        geoModals.add(new GeoModal(c.getString(c.getColumnIndex("uuid")),lat,lng,date,sync_id));
                    }while (c.moveToNext());
                }
            }
        }
        return geoModals;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,date text,sync_id text,lat text,lng text,time text,uuid text)");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public AttendanceListLocation(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    public long insertBulk(String date,String time,String lat, String lng,
                           String sync_id,String uuid){
        Log.d("GEOTAG"," date "+date+" lat "+lat+" lng "+lng+
                " sync "+sync_id+" m uuid "+uuid);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(Date,date);
        contentValues.put(Time,time);
        contentValues.put(UUID,uuid);
        contentValues.put(LAt,lat);
        contentValues.put(Lng,lng);
        contentValues.put(Sync_id,sync_id);

        id=db.insert(DB_Table_name,null,contentValues);
        db.close();
        Log.d("GEOTAG"," insert id "+id);
        return  id;
    }
}
