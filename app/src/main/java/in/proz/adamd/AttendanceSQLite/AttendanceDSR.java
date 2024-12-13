package in.proz.adamd.AttendanceSQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AttendanceDSR extends SQLiteOpenHelper {
    public static final String DB_NAME="AttendanceDSR";
    public static final String DB_Table_name="AttendanceDSR";
    public static  final String ID="id";
    public static  final String Date="date";
    public static  final String Project="project";
    public static  final String Other="other";
    public static final String Planned ="planned";
    public AttendanceDSR(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(id text,date text,project text,planned text,other text)");
            if(db==null){
            }else{
            }
        }catch (Exception e){
            Log.d("area_fragment1","error while creating db "+e.getMessage());       }
    }
    public long insertBulk(String date,String project,String planned,String other){
        Log.d("checkinHeader"," date "+date+" project "+project+" planner "+planned+" other "+other);
        ContentValues contentValues=new ContentValues();
        SQLiteDatabase db=this.getWritableDatabase();
        long id=0;

        contentValues.put(Date,date);
        contentValues.put(Project,project);
        contentValues.put(Planned,planned);
        contentValues.put(Other,other);
        id=db.insert(DB_Table_name,null,contentValues);
        db.close();
        Log.d("checkinHeader"," insert id "+id);
        return  id;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
