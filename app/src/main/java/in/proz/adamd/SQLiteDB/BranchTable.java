package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.ModalClass.LatLng;
import in.proz.adamd.Retrofit.CommonPojo;

public class BranchTable  extends SQLiteOpenHelper {
    public static final String DB_NAME="BranchTable";
    public static final String DB_Table_name="BranchTable";
    public static  final String ID="id";
    public static  final String Branch_Lat="branch_lat";
    public static  final String Branch_Lng="branch_lng";
    public static  final String Distance="distance";

    public BranchTable(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,branch_lat text,branch_lng text,distance text)");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());
        }
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








    public long insertData(String  id_value ,String branch_lat,String branch_lng){
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(ID,id_value);
        contentValues.put(Branch_Lat,branch_lat);
        contentValues.put(Branch_Lng,branch_lng);
        id=db.insert(DB_Table_name,null,contentValues);

        db.close();
        Log.d("mainDropList"," insert id "+id);
        return  id;

    }

    public List<String> selectAllMainCat(){
        List<String> getMainList=new ArrayList<>();
        getMainList.add(0,"Select");
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select distinct(Cat_name) from "+DB_Table_name
                ,null);
        int count =0;
        if(c!=null){
            if(c.getCount()!=0){
                if (c.moveToFirst()) {
                    do {
                        getMainList.add(c.getString(c.getColumnIndex("Cat_name")));
                    } while (c.moveToNext());
                }
            }
        }
        return getMainList;
    }
    public  List<String> selectSubCat(String cat_id){
        List<String> getMainList=new ArrayList<>();
        getMainList.add(0,"Select");
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select distinct(SCat_Name) from "+DB_Table_name+" where Cat_name=?"
                ,new String[]{cat_id});
        int count =0;
        if(c!=null){
            if(c.getCount()!=0){
                if (c.moveToFirst()) {
                    do {
                        getMainList.add(c.getString(c.getColumnIndex("SCat_Name")));
                    } while (c.moveToNext());
                }
            }
        }
        return getMainList;
    }
    public List<String> selectAllAssetNames(String cat_name,String sub_cat_name){
        List<String> getMainList=new ArrayList<>();
        getMainList.add(0,"Select");
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select distinct(Ass_Name) from "+DB_Table_name+" where Cat_name=? and SCat_Name=?"
                ,new String[]{cat_name,sub_cat_name});
        int count =0;
        if(c!=null){
            if(c.getCount()!=0){
                if (c.moveToFirst()) {
                    do {
                        getMainList.add(c.getString(c.getColumnIndex("Ass_Name")));
                    } while (c.moveToNext());
                }
            }
        }
        return getMainList;
    }
    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }
    public List<LatLng> getAllNameList(){
        SQLiteDatabase db=this.getWritableDatabase();
        List<LatLng> arrayList = new ArrayList<>();

        Cursor c = db.rawQuery("select  * from " + DB_Table_name ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        arrayList.add(new LatLng(c.getString(c.getColumnIndex("id")),
                                c.getString(c.getColumnIndex("branch_lat"))
                                ,c.getString(c.getColumnIndex("branch_lng"))));
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
        Log.d("mainDropList"," all deleted ");
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }



}


