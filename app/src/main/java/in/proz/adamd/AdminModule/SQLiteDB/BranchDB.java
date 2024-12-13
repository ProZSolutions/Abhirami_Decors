package in.proz.adamd.AdminModule.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.ModalClass.KeystoneModal;

public class BranchDB extends SQLiteOpenHelper {
    public static final String DB_NAME="BranchDB";
    public static final String DB_Table_name="BranchDB";
    public static  final String ID="id";
    public static  final String Name="name";
    public static  final String Location="location";
    public static  final String Address="address";


    public BranchDB(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,name text,location text,address text )");
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



    public String selectDistrictName(String cat_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT name FROM " + DB_Table_name + " WHERE " + ID + " LIKE '"+cat_name+"' ";
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    leaveID = c.getString(c.getColumnIndex("name"));

                }while (c.moveToNext());
            }
        }

        return leaveID;
    }


    public String selectDistrictId(String cat_name){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        String sql = "SELECT id FROM " + DB_Table_name + " WHERE " + Name + " LIKE '"+cat_name+"' ";
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

    public List<KeystoneModal> selectAllDistrictDetails(){
        List<KeystoneModal> commonPojos = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;

        String sql = "SELECT * FROM " + DB_Table_name ;
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        commonPojos.add(new KeystoneModal("0","ALL"));
         if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Log.d("AllOverMain"," name "+c.getString(c.getColumnIndex("name")));
                    commonPojos.add(new KeystoneModal(c.getString(c.getColumnIndex("id")),
                            c.getString(c.getColumnIndex("name"))));

                }while (c.moveToNext());
            }
        }
        Log.d("AllOverMain"," size as "+commonPojos.size());
        return commonPojos;
    }
    public List<String> SelectDistrictList(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c,c1;
        List<String> districtList = new ArrayList<>();
        districtList.add(0,"ALL");
        String sql = "SELECT * FROM " + DB_Table_name ;
        c = db.rawQuery(sql, null);
        Log.d("AllOverMain"," random count "+c.getCount());

        String leaveID ="";
        Log.d("AllOverMain"," work id count "+c.getCount());
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Log.d("AllOverMain"," name "+c.getString(c.getColumnIndex("name")));
                    districtList.add(c.getString(c.getColumnIndex("name")));

                }while (c.moveToNext());
            }
        }
        Log.d("AllOverMain"," size as "+districtList.size());
        return districtList;
    }

    public long insertData(String  id_str ,String name,String address,String location){
        Log.d("mainDropList"," branch id "+id_str+" name  "+name+" address "+address+"" +
                " locatin "+location);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(ID,id_str);
        contentValues.put(Name,name);
        contentValues.put(Address,address);
        contentValues.put(Location,location);

        id=db.insert(DB_Table_name,null,contentValues);

        db.close();
        Log.d("mainDropList"," insert id "+id);
        return  id;

    }



    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }





}


