package in.proz.adamd.AdminModule.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LoanSQL extends SQLiteOpenHelper {
    public static final String DB_NAME="LoanSQL";
    public static final String DB_Table_name="LoanSQL";
    public static  final String ID="nid";
    public static final String Position="position";
    public LoanSQL(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(nid text ,position text )");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }
    public long insertData(String  nid,String position ){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(ID,nid);
        contentValues.put(Position,position);
        if(selectRow(nid,position)==0) {
            id = db.insert(DB_Table_name, null, contentValues);
        }
        Log.d("getDetailsCheck"," insert "+id);
        db.close();
        return  id;
    }
    public int selectRow(String id ,String position
    ){
        int count=0;
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        String sql="SELECT * FROM "+DB_Table_name+" WHERE "+ID+" LIKE '"+id +"' AND  "+Position+" LIKE '"+position+"' ";
        Log.d("Area_Sample"," sq "+sql);
        Cursor cursor=sqLiteDatabase.rawQuery(sql,null);
        if(cursor!=null){
            count=cursor.getCount();
        }
        Log.d("getDetailsCheck", " present count as "+count);

        return count;
    }
    public List<String> getAllList(){
        SQLiteDatabase db=this.getWritableDatabase();
        List<String> arrayList = new ArrayList<>();

        Cursor c = db.rawQuery("select  * from " + DB_Table_name ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        arrayList.add(c.getString(c.getColumnIndex("nid")));
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+arrayList.size());


        return arrayList;
    }
    public void deleteAll( ){
         SQLiteDatabase db=this.getWritableDatabase();
        db.delete(DB_Table_name, null , null);
        db.close();
    }
    public void deleteRecord(String id,String position){
        Log.d("getDetailsCheck"," delete record "+id+" pos "+position);
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(DB_Table_name, ID + " = ? AND "+Position+" =?", new String[] { id ,position});
        db.close();
    }
}
