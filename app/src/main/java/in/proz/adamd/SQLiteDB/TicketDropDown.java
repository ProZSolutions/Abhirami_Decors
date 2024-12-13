package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class TicketDropDown extends SQLiteOpenHelper {
    public static final String DB_NAME="TicketDropDown";
    public static final String DB_Table_name="TicketDropDown";
    public static  final String ID="id";
    public static  final String Name="Name";
    public static  final String Type_value="type_value";

    public TicketDropDown(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,Name text,type_value text )");
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






    public String selectOnlyID(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT * FROM " + DB_Table_name + " WHERE " + Name + " LIKE '"+name+"'  ";
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    leaveID = c.getString(c.getColumnIndex("id"));

                }while (c.moveToNext());
            }
        }

        return leaveID;
    }

    public long insertData(String  id_value ,String name){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        Log.d("getTicket"," id "+id_value+" name "+name);

        contentValues.put(ID,id_value);
        contentValues.put(Name,name);

            id=db.insert(DB_Table_name,null,contentValues);
            Log.d("dropdownlist"," insert "+id);

        db.close();
        return  id;

    }


    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }
    public List<String> getAllNameList(String type_value){
        SQLiteDatabase db=this.getWritableDatabase();
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Select");

        Cursor c = db.rawQuery("select  * from " + DB_Table_name  ,
               null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        arrayList.add(c.getString(c.getColumnIndex("Name")));
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+arrayList.size());


        return arrayList;
    }
    public int selectRowCount(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+DB_Table_name,null);
        int count =0;
        if(cursor!=null){
            count=cursor.getCount();
        }
        return count;
    }


    public void deleteAll(Context context){
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }



}


