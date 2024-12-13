package in.proz.adamd.AttendanceSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.ModalClass.AttendanceListSubModal;

public class AttendanceListDB extends SQLiteOpenHelper {
    public static final String DB_NAME="AttendanceListDB";
    public static final String DB_Table_name="AttendanceListDB";
    public static  final String ID="id";
    public static  final String Date="date";
    public static  final String UUID="uuid";
    public static  final String WeekDate="week_date";
     public static final String WeekEnd ="week_end";
    public static final String Holiday ="holiday";
    public static final String Leave ="leave";
     public static final String InsertStatus ="insert_status";
    public static final String  InternetStatus="internet_status";
    public static final String  Sync_id="sync_id";
    PunchINOUTDB punchINOUTDB;

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,date text,week_date text,week_end text,holiday text,leave text,insert_status text,internet_status text,sync_id text,uuid text )");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }

    public AttendanceListDB(Context context) {
        super(context, DB_NAME,null,2);
        punchINOUTDB= new PunchINOUTDB(context);
        punchINOUTDB.getWritableDatabase();
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
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
    public long insertBulk(String date,String weekdate,String weekend, String holiday,
                           String leave,String insertstatus,String internetStatus,String sync_id,String uuid){
        Log.d("checkinHeader"," date "+date+" weekdate "+weekdate+" weekend "+
                weekend+" holiday "+holiday+" lev "+leave+" insert "+insertstatus+" interner "+internetStatus+
                " sync "+sync_id+" UUID "+uuid);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(Date,date);
        contentValues.put(WeekDate,weekdate);
        contentValues.put(WeekEnd,weekend);
        contentValues.put(Holiday,holiday);
        contentValues.put(Leave,leave);
        contentValues.put(InsertStatus,insertstatus);
        contentValues.put(InternetStatus,internetStatus);
        contentValues.put(Sync_id,sync_id);
        contentValues.put(UUID,uuid);

        id=db.update(DB_Table_name,contentValues,"sync_id=?  ",
                new String[]{sync_id});

        id=db.insert(DB_Table_name,null,contentValues);
        db.close();
        Log.d("checkinHeader"," insert id "+id);
        return  id;
    }

    public   List<AttendanceListSubModal> bulkDateList(String date){
        //date in HH:mm:ss
        String[] split = date.split(" ");
        String[] splt1 = split[0].split("-");
        String date_format = splt1[0]+"-"+splt1[1];
        Log.d("insertAttendanceList"," select date "+split[0]+" date format "+date_format);

        List<AttendanceListSubModal> attendanceListSubModalList = new ArrayList<>();
        List<String> inTime = new ArrayList<>();
        List<String> outTime = new ArrayList<>();
        List<String> WorkHrs = new ArrayList<>();
        List<String> Pin = new ArrayList<>();
        List<String> Pout = new ArrayList<>();


        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select  * from " + DB_Table_name+" where "+Date+" LIKE '%"+date_format+"%'" ,null);
        Log.d("insertAttendanceList"," total "+c.getCount());
        int main_count=0;
        if (c != null) {
            if (c.getCount() != 0) {
                 if (c.moveToFirst()) {
                    do {
                        Log.d("insertAttendanceList"," id as "+c.getString(c.getColumnIndex("id"))
                        +" date "+c.getString(c.getColumnIndex("date")) );
                        inTime = punchINOUTDB.inTimeList(c.getString(c.getColumnIndex("date")),0);
                        outTime = punchINOUTDB.inTimeList(c.getString(c.getColumnIndex("date")),1);
                        WorkHrs = punchINOUTDB.inTimeList(c.getString(c.getColumnIndex("date")),2);
                        Pin = punchINOUTDB.inTimeList(c.getString(c.getColumnIndex("date")),3);
                        Pout = punchINOUTDB.inTimeList(c.getString(c.getColumnIndex("date")),4);
                        attendanceListSubModalList.add(main_count,new AttendanceListSubModal(c.getString(c.getColumnIndex("date")),
                                c.getString(c.getColumnIndex("week_date")),inTime,outTime,WorkHrs,c.getString(c.getColumnIndex("week_end")),
                                c.getString(c.getColumnIndex("holiday")),c.getString(c.getColumnIndex("leave")),Pin,Pout,null));
                        main_count=+1;
                    } while (c.moveToNext());
                }
            }
        }

        Log.d("insertAttendanceList"," total size as "+attendanceListSubModalList.size());

        return attendanceListSubModalList;

    }

    public void updateBulk(String outtime,String workhrs,String pinLocation,String poutLocation,String sync_id){
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id =0;

        id=db.update(DB_Table_name,contentValues,"sync_id=?  ",
                new String[]{sync_id});
        db.close();
        Log.d("insertAttendanceList"," update is "+id);

    }
    public void deleteAll(Context context){
        Log.d("mainDropList"," all deleted ");
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }


}
