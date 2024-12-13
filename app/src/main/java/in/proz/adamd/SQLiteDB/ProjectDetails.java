package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProjectDetails extends SQLiteOpenHelper {
    public static final String DB_NAME="ProjectDetails";
    public static final String DB_Table_name="ProjectDetails";
    public static  final String ID="nid";
    public static  final String Pro_ID="pro_id";
    public static  final String Pro_name="pro_name";
    public static  final String Git_url="git_url";

    public ProjectDetails(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(nid integer PRIMARY KEY AUTOINCREMENT NOT NULL,pro_id text,pro_name text,git_url text )");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
    public long insertData(String pro_ID,String pro_name,String git_url){

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
        contentValues.put(Pro_ID,pro_ID);
        contentValues.put(Pro_name,pro_name);
        contentValues.put(Git_url,git_url);
        id=db.insert(DB_Table_name,null,contentValues);
        Log.d("dropdownlist"," insert "+id);
        db.close();
        return  id;
    }
    public List<String> getAllProjectList(String type_value){
        SQLiteDatabase db=this.getWritableDatabase();
        List<String> arrayList = new ArrayList<>();

        Cursor c = db.rawQuery("select  distinct(pro_name) from " + DB_Table_name ,null);
         if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        if(!arrayList.contains(c.getString(c.getColumnIndex("pro_name")))) {
                            arrayList.add(c.getString(c.getColumnIndex("pro_name")));
                        }
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+arrayList.size());


        return arrayList;
    }
    public  String getAllProjectIDList(String projectname){
        SQLiteDatabase db=this.getWritableDatabase();
        String project_id="";

        Cursor c = db.rawQuery("select  * from " + DB_Table_name+" where "+Pro_name+" LIKE '%"+projectname+"%'" ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("get_nw"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        project_id=c.getString(c.getColumnIndex("pro_id"));
                    } while (c.moveToNext());
                }
            }
        }
        Log.d("get_nw"," after update the content "+project_id);


        return project_id;
    }
    public  List<String> getAllGitList(String projectname){
        SQLiteDatabase db=this.getWritableDatabase();
        String project_id="";

        Cursor c = db.rawQuery("select  distinct(git_url) from " + DB_Table_name+" where "+Pro_name+" LIKE '%"+projectname+"%'" ,null);
        if (c != null) {
            if (c.getCount() != 0) {
                Log.d("projectDetails"," count not present  ");
                if (c.moveToFirst()) {
                    do {
                        project_id=c.getString(c.getColumnIndex("git_url"));
                        Log.d("projectDetails"," selectdd "+project_id);
                    } while (c.moveToNext());
                }
            }
        }
        ArrayList<String> strList = new ArrayList<String>();
        if(!TextUtils.isEmpty(project_id)) {
            String[] strSplit = project_id.split(",");

            // Now convert string into ArrayList
              strList = new ArrayList<String>(
                    Arrays.asList(strSplit));
        }
        Log.d("get_nw"," after update the content "+project_id);


        return strList;
    }
}
