package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sample extends SQLiteOpenHelper {
    public static final String DB_NAME="Sample";
    public static final String DB_Table_name="Sample";
    public static  final String ID="id";
    public static  final String Ass_Name="Ass_Name";
    public static  final String Cat_id="Cat_id";


    public Sample(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id integer PRIMARY KEY AUTOINCREMENT NOT NULL,Ass_Name text,Cat_id text  )");
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









    public long insertData( String assetName){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

         contentValues.put(Ass_Name,assetName);
        contentValues.put(Cat_id,date);
        id=db.insert(DB_Table_name,null,contentValues);

        db.close();
        Log.d("mainDropList"," insert id "+id);
        return  id;

    }

    public int selectAllMainCat(){
        List<String> getMainList=new ArrayList<>();
        getMainList.add(0,"Select");
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+DB_Table_name
                ,null);
        int count =0;
        if(c!=null){
             count = c.getCount();
        }
        return count;
    }



}


