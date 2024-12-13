package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AssetDropDown extends SQLiteOpenHelper {
    public static final String DB_NAME="DropDownTable";
    public static final String DB_Table_name="DropDownTable";
    public static  final String ID="id";
    public static  final String Ass_Name="Ass_Name";
    public static  final String Cat_id="Cat_id";
    public static  final String Cat_name="Cat_name";
    public static  final String SCat_id="SCat_id";
    public static  final String SCat_Name="SCat_Name";

    public static  final String Type_value="type_value";

    public AssetDropDown(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,Ass_Name text,Cat_id text,Cat_name text,SCat_id text,SCat_Name text )");
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






    public String selectMainID(String cat_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT Cat_id FROM " + DB_Table_name + " WHERE " + Cat_name + " LIKE '"+cat_name+"' ";
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    leaveID = c.getString(c.getColumnIndex("Cat_id"));

                }while (c.moveToNext());
            }
        }

        return leaveID;
    }
    public String selectSubCatID(String cat_name,String sub_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT SCat_id FROM " + DB_Table_name + " WHERE " + Cat_name + " LIKE '"+cat_name+"' AND  "+
                SCat_Name+" LIKE '"+sub_name+"' ";
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    leaveID = c.getString(c.getColumnIndex("SCat_id"));

                }while (c.moveToNext());
            }
        }

        return leaveID;
    }
    public String selectAssetID(String cat_name,String sub_name,String Assect){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT id FROM " + DB_Table_name + " WHERE " + Cat_name + " LIKE '"+cat_name+"' AND  "+
                SCat_Name+" LIKE '"+sub_name+"' AND "+Ass_Name+" LIKE '"+Assect+"' ";
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

    public long insertData(String  id_value ,String assetName,String cat_id,String cat_name,
                           String sub_id,String sub_name){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(ID,id_value);
        contentValues.put(Ass_Name,assetName);
        contentValues.put(Cat_id,cat_id);
        contentValues.put(Cat_name,cat_name);
        contentValues.put(SCat_id,sub_id);
        contentValues.put(SCat_Name,sub_name);
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
    public List<String> getAllNameList(String type_value){
        SQLiteDatabase db=this.getWritableDatabase();
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Select");

        Cursor c = db.rawQuery("select  * from " + DB_Table_name + " Where type_value=? ",
                new String[]{ type_value});
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
        Log.d("mainDropList"," all deleted ");
        // context.deleteDatabase(DB_NAME);
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("delete  from " + DB_Table_name);
        db.delete(DB_Table_name, null, null);
        db.close();
    }



}


