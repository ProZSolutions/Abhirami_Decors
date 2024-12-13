package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.ModalClass.CalendarSubClass;

public class HolidayCalendar extends SQLiteOpenHelper {
    public static final String DB_NAME="HolidayCalendar";
    public static final String DB_Table_name="HolidayCalendar";
    public static  final String ID="id";
    public static  final String Title="title";
    public static  final String Start="start";
    public static  final String End_value="end_value";
    public static  final String AllDay="allDay";
    public HolidayCalendar(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,title text,start text,end_value text,allDay text )");
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
    public int selectRow(String id,String leave_type
    ){
        int count=0;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String sql="SELECT * FROM "+DB_Table_name+" WHERE "+ID+" LIKE '"+id +"' AND  "+ Title +" LIKE '"+leave_type+"'";
        Log.d("Area_Sample"," sq "+sql);
        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);
        if(cursor!=null){
            count=cursor.getCount();
        }
        Log.d("area_fragment1"," select row "+sql+ " present count as "+count);

        return count;
    }
    public long insertData(CalendarSubClass calendarSubClass) {
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(ID,calendarSubClass.getId());
        contentValues.put(Title,calendarSubClass.getTitle());
        contentValues.put(Start,calendarSubClass.getStart());
        contentValues.put(End_value,calendarSubClass.getEnd());
        contentValues.put(AllDay,calendarSubClass.getAllday());
        int count = selectRow(calendarSubClass.getId(),calendarSubClass.getTitle());
        Log.d("dropdownlist"," row count "+count);
        if (count==0){
            id=db.insert(DB_Table_name,null,contentValues);
            Log.d("dropdownlist"," insert "+id);
        }else{
            id=db.update(DB_Table_name,contentValues,"id=? and type_value=? ",
                    new String[]{calendarSubClass.getId(),calendarSubClass.getTitle()});
            Log.d("dropdownlist"," update "+id);
        }
        Log.d("dropdownlist"," insertion id "+id);
        db.close();
        return  id;
    }
    public List<CalendarSubClass> getAllList( ){
        SQLiteDatabase db=this.getWritableDatabase();
        List<CalendarSubClass> arrayList = new ArrayList<>();

      /*      Cursor c = db.rawQuery("select  * from " + DB_Table_name + " Where type_value=? ",
                    new String[]{ type_value});*/
        Cursor c = db.rawQuery("select  * from " + DB_Table_name ,
                null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        arrayList.add(new CalendarSubClass(c.getString(c.getColumnIndex("id")),c.getString(c.getColumnIndex("title")),
                                c.getString(c.getColumnIndex("allDay")),c.getString(c.getColumnIndex("start")),c.getString(c.getColumnIndex("end_value"))));
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+arrayList.size());


        return arrayList;
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
