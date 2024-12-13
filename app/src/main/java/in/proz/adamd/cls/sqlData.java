package in.proz.adamd.cls;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.shashank.sony.fancytoastlib.FancyToast;

public class sqlData extends SQLiteOpenHelper {
    public Context ctx;
    public sqlData(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try{
            sqLiteDatabase.execSQL(cls_shared_data.s_Qry_Create_Attendance);
            sqLiteDatabase.execSQL(cls_shared_data.s_Qry_Create_Logs);
        }catch (Exception ex)
        {

            FancyToast.makeText(ctx,ex.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try{
            if(i!=i1) {
                sqLiteDatabase.execSQL(cls_shared_data.s_Qry_Drop_Attendance);
                sqLiteDatabase.execSQL(cls_shared_data.s_Qry_Drop_Logs);
            }
        }catch (Exception ex)
        {

            FancyToast.makeText(ctx,ex.getMessage(),FancyToast.LENGTH_LONG,FancyToast.ERROR,true).show();
        }
    }
}
