package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.ModalClass.NotesModal;

public class NotesTable extends SQLiteOpenHelper {
    public static final String DB_NAME="NotesTable";
    public static final String DB_Table_name="NotesTable";
    public static  final String ID="nid";
    public static  final String Title="ntitle";
    public static  final String Desc="ndesc";
    public static  final String Date="ndate";
    public NotesTable(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(nid integer PRIMARY KEY AUTOINCREMENT NOT NULL,ntitle text,ndesc text,ndate text )");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
    public long insertData(String  title ,String desc,String date){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(Title,title);
        contentValues.put(Desc,desc);
        contentValues.put(Date,date);

        id=db.insert(DB_Table_name,null,contentValues);
        Log.d("dropdownlist"," insert "+id);
        db.close();
        return  id;
    }
    public long updateData(String  title ,String desc,String date,String nid){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(Title,title);
        contentValues.put(Desc,desc);
        contentValues.put(Date,date);
        id=db.update(DB_Table_name,contentValues,"nid=?",new String[]{nid});

    //    id=db.insert(DB_Table_name,null,contentValues);
        Log.d("dropdownlist"," insert "+id);
        db.close();
        return  id;
    }
    public List<NotesModal> getAllList(String type_value){
        SQLiteDatabase db=this.getWritableDatabase();
        List<NotesModal> arrayList = new ArrayList<>();

        Cursor c = db.rawQuery("select  * from " + DB_Table_name ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        arrayList.add(new NotesModal(c.getString(c.getColumnIndex("ndate")),
                                c.getString(c.getColumnIndex("ntitle")),
                                c.getString(c.getColumnIndex("ndesc")), c.getString(c.getColumnIndex("nid"))));
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+arrayList.size());


        return arrayList;
    }
    public void deleteRecord(String id){
        SQLiteDatabase db=this.getWritableDatabase();
        db.delete(DB_Table_name, ID + " = ?", new String[] { id });
        db.close();
    }
}
