package in.proz.adamd.SQLiteDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import in.proz.adamd.CommonJson.DashboardContent;
import in.proz.adamd.R;

public class DashboardUIViewTable extends SQLiteOpenHelper {
    public static final String DB_NAME="DashboardUIViewTable";
    public static final String DB_Table_name="DashboardUIViewTable";
     public static  final String Table_tag="table_tag";
    public static  final String Header_tag="header_tag";
    public static  final String Id="id";
    public static  final String Tag="tag";
    public static  final String Title="title";
    public static  final String Visible="visible";
    public DashboardUIViewTable(Context context) {
        super(context, DB_NAME,null,2);
        context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            Log.d("area_fragment1"," table created");
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    +DB_Table_name+
                    "(table_tag text,header_tag text,id text,tag text,title text,visible text)");
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
    public long insertData(String  table_tag ,String header_tag,String id,String tag,String innerTilte,boolean isvisible){
        Log.d("dashboardUIViewTable"," insert header tage "+header_tag+" is visible "+isvisible+" table tag "+
                table_tag);
        String isVisiblev="0";
        if(!isvisible){
            isVisiblev="1";
        }
        long id1=0;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            ContentValues contentValues=new ContentValues();
            db = this.getWritableDatabase();


            contentValues.put(Table_tag,table_tag);
            contentValues.put(Header_tag,header_tag);
            contentValues.put(Id,id);
            contentValues.put(Tag,tag);
            contentValues.put(Title,innerTilte);
            contentValues.put(Visible,isVisiblev);
            if(selectAllAssetNames(table_tag,id)==0) {
                id1 = db.insert(DB_Table_name, null, contentValues);
            }else{
                id1=db.update(DB_Table_name,contentValues,"table_tag=? and id=?  ",
                        new String[]{table_tag,id});

            }

         } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();  // Close cursor
            }
           /* if (db != null && db.isOpen()) {
                db.close();  // Close database
            }*/

        }

        Log.d("dashboardUIViewTable"," insert id "+id);
        return id1;

    }
    public  long selectAllAssetNames(String header_tag, String id){
            long count=0;

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            c = db.rawQuery("select * from "+DB_Table_name+" where table_tag=? and id=?"
                    ,new String[]{header_tag,id});
            count =c.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();  // Close cursor
            }
            /*if (db != null && db.isOpen()) {
                db.close();  // Close database
            }*/
        }

        return c.getCount();
    }
    public   List<DashboardContent>  selectAllItems(String header_tag,int[] imageArray){
        Log.d("dashboardUIViewTable"," header tag  " +header_tag+" array size "+ imageArray.length);
         List<DashboardContent> adminHeaderDashboard = new ArrayList<>();

        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            c = db.rawQuery("select * from "+DB_Table_name+" where table_tag=? "
                    ,new String[]{header_tag});
            Log.d("dashboardUIViewTable"," total count "+c.getCount());
            if (c != null) {
                if (c.getCount() != 0) {
                    if (c.moveToFirst()) {
                        do {
                            Log.d("dashboardUIViewTable"," details "+c.getString(c.getColumnIndex("visible"))+
                                    " id "+c.getString(c.getColumnIndex("id"))+" tag "+
                                    c.getString(c.getColumnIndex("tag"))+" title "+c.getString(c.getColumnIndex("title")));
                            if(c.getString(c.getColumnIndex("visible")).equals("0")){
                                int position  = Integer.parseInt(c.getString(c.getColumnIndex("id")));
                                int image = R.drawable.calendar_icon;
                                if (position >= 0 && position < imageArray.length) {
                                    image=imageArray[position];
                                }
                                adminHeaderDashboard.add(new DashboardContent(c.getString(c.getColumnIndex("id"))
                                        ,c.getString(c.getColumnIndex("tag"))
                                        ,c.getString(c.getColumnIndex("title")),
                                        true,image));
                            }
                        } while (c.moveToNext());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();  // Close cursor
            }
            /*if (db != null && db.isOpen()) {
                db.close();  // Close database
            }*/
        }


        Log.d("dashboardUIViewTable"," return size as "+adminHeaderDashboard.size());

        return adminHeaderDashboard;
    }
    public   String  selectHeaderTagOnly(String header_tag){
        Log.d("dashboardUIViewTable"," header tag "+header_tag);
          String header_title=null;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = this.getWritableDatabase();
            c = db.rawQuery("select distinct(header_tag) from "+DB_Table_name+" where table_tag=? "
                    ,new String[]{header_tag});
            if (c != null) {
                if (c.getCount() != 0) {
                    if (c.moveToFirst()) {
                        do {
                            header_title=c.getString(c.getColumnIndex("header_tag"));
                        } while (c.moveToNext());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) {
                c.close();  // Close cursor
            }
            /*if (db != null && db.isOpen()) {
                db.close();  // Close database
            }*/
        }
        return header_title;
    }
    public  long selectAllCount(){

        SQLiteDatabase db = null;
        long totle=0;
        Cursor cursor = null;
        try {
            db = this.getWritableDatabase();
            Cursor c = db.rawQuery("select * from "+DB_Table_name,null);
            totle=c.getCount();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();  // Close cursor
            }
           /* if (db != null && db.isOpen()) {
                db.close();  // Close database
            }*/
        }

        return  totle;
    }
    public void DropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL("DELETE FROM "+DB_Table_name); // Replace 'TableName' with your table's name
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db.isOpen()) {
                db.close(); // Close the database
            }
        }
    }
}
