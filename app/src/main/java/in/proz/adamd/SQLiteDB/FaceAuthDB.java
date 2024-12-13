package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.Face.SQLFaceModal;

public class FaceAuthDB extends SQLiteOpenHelper {
    public static final String DB_NAME="FaceAuthDB";
    public static final String DB_Table_name="FaceAuthDB";
    public static  final String ID="id";
    public static  final String Extra="extra";
    public static  final String Title="title";
    public static  final String Updated_at="updated_at";

    public FaceAuthDB(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,extra text,title text,updated_at text)");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch(oldVersion) {
            case 1:
                onCreate(db);
                // we want both updates, so no break statement here...
            case 2:
                // db.execSQL(DATABASE_CREATE_someothertable);
        }
    }
    public String  convertArrayToString(float[][] array){
        Gson gson = new Gson();
        String jsonString = gson.toJson(array);
        return jsonString;

      /*  StringBuilder builder = new StringBuilder();
        for (float[] row : floatArray) {
            for (float value : row) {
                builder.append(value).append(","); // Use a comma as a delimiter
            }
            builder.append(";"); // Use a semicolon for rows
        }
        String flattenedString = builder.toString();*/
    }
    public float[][]  convertStringToArray(String extra){
        Gson gson = new Gson();
        float[][] floatArray = gson.fromJson(extra, float[][].class);
        return floatArray;

       /* String[] rows = flattenedString.split(";");
        float[][] floatArray = new float[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            String[] values = rows[i].split(",");
            floatArray[i] = new float[values.length];
            for (int j = 0; j < values.length; j++) {
                floatArray[i][j] = Float.parseFloat(values[j]);
            }
        }*/
    }
    public long insertData(String extra,String title,String updated_at){
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;
         contentValues.put(Extra,extra);
        contentValues.put(Title,title);
        contentValues.put(Updated_at,updated_at);

        if(getCount()==0){
            Log.d("FaceAuthModal"," insert ");
            id=db.insert(DB_Table_name,null,contentValues);
        }else{
            Log.d("FaceAuthModal"," updated  ");
            id=db.update(DB_Table_name,contentValues,null,null);
        }
        db.close();
        Log.d("FaceAuthModal"," insert id "+id);
        return  id;

    }
    public void DropTable(){
        SQLiteDatabase db=this.getWritableDatabase();
        // db.execSQL("DROP TABLE "+DB_Table_name);
        //db.execSQL("DELETE FROM "+DB_Table_name);
        db.delete(DB_Table_name,null,null);
    }
    public int  getCount(){
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+DB_Table_name
                ,null);
        int mainCount =0;
        if(c!=null){
            mainCount= c.getCount();
        }
        return mainCount;

    }
    public SQLFaceModal selectFaceList(){
        SQLFaceModal sqlFaceModal=null;

        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from "+DB_Table_name
                ,null);
        Log.d("FaceAuthModal"," count as "+c.getCount());
        int count =0;
        if(c!=null){
            if(c.getCount()!=0){
                if (c.moveToFirst()) {
                    do {
                        sqlFaceModal = new SQLFaceModal(c.getString(c.getColumnIndex("extra")),
                                c.getString(c.getColumnIndex("title")),
                                c.getString(c.getColumnIndex("updated_at")));

                     } while (c.moveToNext());
                }
            }
        }
        return sqlFaceModal;
    }
}
