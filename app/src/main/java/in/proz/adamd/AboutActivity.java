package in.proz.adamd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.Profile.ProfileActivity;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    TextView title;
    ImageView backpress;
    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;
    ImageView nhome_icon;
    TextView nhome_text;
    String intent_version,admin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_about_page);

        String versionName = BuildConfig.VERSION_NAME;
        TextView versioncode = findViewById(R.id.versioncode);
        versioncode.setText("Version Code : "+getApplicationContext().getString(R.string.version_Code));
        
        
        nhome_icon= findViewById(R.id.nhome_icon);
        nhome_text= findViewById(R.id.nhome_text);
      /*  nhome_icon.setImageTintList(getApplicationContext().getColorStateList(R.color.color_primary));
        nhome_text.setTextColor(getApplicationContext().getColor(R.color.black));*/

        nhome_layout= findViewById(R.id.nhome_layout);
        //nabout_layout= findViewById(R.id.nabout_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
       // nabout_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);




        backpress =findViewById(R.id.backpress);
        backpress.setVisibility(View.VISIBLE);
        title = findViewById(R.id.header_title);
        title.setText("About");
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            intent_version = bundle.getString("intent_version");
            admin = bundle.getString("admin");
        }
         backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent();
            }
        });
       /* CommonClass comm = new CommonClass();
        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(AboutActivity.this,online_layout,online_text,online_icon);*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       callIntent();
    }

    public void callIntent(){
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
      /*  if(!TextUtils.isEmpty(intent_version)){
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(getApplicationContext(), DashboardNewActivity.class);
            startActivity(intent);
        }*/
    }

    @Override
    public void onClick(View v) {
        int id= v.getId();
        switch (id){
            case R.id.nhome_layout:
                Intent intentabout = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intentabout);
                break;
            case R.id.nprofile_layout:
                Intent intentabout1 = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intentabout1);
                break;
            case R.id.nreports_layout:
                Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(notes);
                break;
            case R.id.nlocation_layout:
                Intent intent152 = new Intent(getApplicationContext(), MapCurrentLocation.class);
                startActivity(intent152);
                break;
        }
    }
}
