package in.proz.adamd.AttendanceSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.proz.adamd.AttendanceUploadModal.INTimeOutTimeModal;

public class PunchINOUTDB extends SQLiteOpenHelper {
    public static final String DB_NAME="PunchINOUTDB";
    public static final String DB_Table_name="PunchINOUTDB";
    public static  final String ID="id";
    public static  final String Date="date";
    public static  final String InsertID="insert_id";
    public static  final String UUID="uuid";
    public static final String  Sync_id="sync_id";

    public static  final String Intime="intime";
    public static  final String Outtime="out_time";
    public static  final String WorkingHrs="work_hours";
    public static final String PinLocation ="pin_work_location";
    public static final String PoutLocation ="pout_work_location";
    public static final String CheckInLatLng ="checkinlatlng";
    public static final String CheckOutLatLng ="checkoutlatlng";
    public PunchINOUTDB(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,date text,insert_id text,intime text,out_time text,work_hours text,pin_work_location text,pout_work_location text,checkinlatlng text,checkoutlatlng text,sync_id text,uuid text )");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("select_project"," upgrade called "+oldVersion+" new version "+newVersion);
        switch(oldVersion) {
            case 1:
                onCreate(db);
                // we want both updates, so no break statement here...
            case 2:
                // db.execSQL(DATABASE_CREATE_someothertable);
        }
    }
    public void deleteAll(Context context){
        Log.d("mainDropList"," all deleted ");
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }

    public List<INTimeOutTimeModal> selectAllData(int i){
        List<INTimeOutTimeModal> geoModals = new ArrayList<>();
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select  * from " + DB_Table_name+" WHERE "+InsertID+"=1 ",null);
        Log.d("GEOTAG"," count as "+c.getCount());
        if(c!=null){
            if(c.getCount()!=0){
                if(c.moveToFirst()){
                    do{
                      if(i==1){
                           Double lat =0.0,lng = 0.0;
                           if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("checkinlatlng")))){
                              String[] arr = c.getString(c.getColumnIndex("checkinlatlng")).split(",");
                              lat = Double.parseDouble(arr[0]);
                              lng = Double.parseDouble(arr[1]);
                          }
                          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           if(TextUtils.isEmpty(c.getString(c.getColumnIndex("intime")))) {
                               if(lat!=0 && lng!=0){
                                   geoModals.add(new INTimeOutTimeModal(lat, lng, c.getString(c.getColumnIndex("pout_work_location")), c.getString(c.getColumnIndex("intime")),
                                           c.getString(c.getColumnIndex("out_time")), c.getString(c.getColumnIndex("uuid")), c.getString(c.getColumnIndex("sync_id"))
                                           , sdf.format(new Date()), c.getString(c.getColumnIndex("pin_work_location")), sdf.format(new Date())));
                               }

                          }
                      }else{
                          Double lat =0.0,lng = 0.0;
                           if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("checkoutlatlng")))){
                              String[] arr = c.getString(c.getColumnIndex("checkoutlatlng")).split(",");
                               lat = Double.parseDouble(arr[0]);
                               lng = Double.parseDouble(arr[1]);
                          }
                          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                           if(!TextUtils.isEmpty(c.getString(c.getColumnIndex("intime"))) ){
                               if(lat!=0  && lng!=0){
                                   geoModals.add(new INTimeOutTimeModal(lat, lng, c.getString(c.getColumnIndex("pout_work_location")), c.getString(c.getColumnIndex("intime")),
                                           c.getString(c.getColumnIndex("out_time")), c.getString(c.getColumnIndex("uuid")), c.getString(c.getColumnIndex("sync_id"))
                                           , sdf.format(new Date()), c.getString(c.getColumnIndex("pin_work_location")), sdf.format(new Date())));
                               }

                          }
                           }
                    }while (c.moveToNext());
                }
            }
        }
        return geoModals;
    }
    public void updateAllRecords(){
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        String sql= "update "+DB_Table_name+" SET "+InsertID+"=0 WHERE "+InsertID+"=1";
        db.execSQL(sql);
        db.close();
    }

    public long insertBulk(String date,String insertID,String inTime, String outTime,
                           String workHrs,String pinLocation,String poutLocation,String checkInLatLng,String checkOutLatLng,
                           String sync_id,String uuid){
        Log.d("checkinHeader"," in punch in "+date+" intime "+inTime+" out "+
                outTime+" pin "+pinLocation+" check in lo "+checkInLatLng+" sync id "+sync_id+" uuid "+uuid );
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(Date,date);
        contentValues.put(UUID,uuid);
        contentValues.put(Sync_id,sync_id);
        contentValues.put(InsertID,insertID);
        contentValues.put(Intime,inTime);
        contentValues.put(Outtime,outTime);
        contentValues.put(WorkingHrs,workHrs);
        contentValues.put(PinLocation,pinLocation);
        contentValues.put(PoutLocation,poutLocation);
        contentValues.put(CheckInLatLng,checkInLatLng);
        contentValues.put(CheckOutLatLng,checkOutLatLng);

        id=db.insert(DB_Table_name,null,contentValues);
        db.close();
        Log.d("checkinHeader"," insert punch id "+id);
        return  id;
    }

    public long updateBulk(String checkOutLatLng,String date,String sync_id,String poutLocation,String punchoutTime){
        Log.d("checkinHeader"," update "+date+" syn "+sync_id+" pout "+poutLocation+" time "+punchoutTime);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(Date,date);
        contentValues.put(Outtime,punchoutTime);
        contentValues.put(Sync_id,sync_id);
        contentValues.put(CheckOutLatLng,checkOutLatLng);
        contentValues.put(PoutLocation,poutLocation);
        id=db.update(DB_Table_name,contentValues,Sync_id+" = ?",
                new String[]{sync_id});
        Log.d("checkinHeader"," update id "+id);
        return id;
    }

    public void setWorkingHoures(String sync_id){
        ContentValues contentValues=new ContentValues();
         SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select  * from " + DB_Table_name+" where "+Sync_id+" LIKE '%"+sync_id+"%'" ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("checkinHeader", " count not present  ");
                if (c.moveToFirst()) {
                    do {
                        SimpleDateFormat df3 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                         String in_time =c.getString(c.getColumnIndex("intime"));
                        String out_time =c.getString(c.getColumnIndex("out_time"));

                        try {
                             Date start = df3.parse(in_time);
                             Date to = df3.parse(out_time);
                             long diff = to.getTime()-start.getTime();
                             String date_format= DateFormat.format("HH:mm:ss",diff).toString();
                             contentValues.put(WorkingHrs,date_format);
                             Log.d("checkinHeader"," diff "+diff);
                            long id1=db.update(DB_Table_name,contentValues,"sync_id=? ",
                                    new String[]{sync_id});
                            Log.d("checkinHeader"," check out time "+id1);

                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }


                    } while (c.moveToNext());
                }
            }
        }
    }

    public List<String> inTimeList(String date,int selectId){        //date in HH:mm:ss
        Log.d("insertAttendanceList"," select date "+date);
        List<String> list = new ArrayList<>();

        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select  * from " + DB_Table_name+" where "+Date+" LIKE '%"+date+"%'" ,null);
        Log.d("insertAttendanceList"," total "+c.getCount());
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("insertAttendanceList", " count not present  ");
                if (c.moveToFirst()) {
                    do {
                        Log.d("insertAttendanceList"," get details "+" intime "+
                                c.getString(c.getColumnIndex("intime")) +" outtime "+c.getString(c.getColumnIndex("out_time")));
                        if(selectId==0){
                            list.add(c.getString(c.getColumnIndex("intime")));
                        }else if(selectId==1){
                            list.add(c.getString(c.getColumnIndex("out_time")));
                        }else if(selectId==2){
                            list.add(c.getString(c.getColumnIndex("work_hours")));
                        }else if(selectId==3){
                            list.add(c.getString(c.getColumnIndex("pin_work_location")));
                        }else if(selectId==4){
                            list.add(c.getString(c.getColumnIndex("pout_work_location")));
                        }
                    } while (c.moveToNext());
                }
            }
        }

        Log.d("insertAttendanceList"," total size as "+list.size());

        return list;
    }






}
