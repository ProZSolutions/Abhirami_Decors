package in.proz.adamd.AdminModule.AdminRequest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import in.proz.adamd.AdminModule.AdminNewDashboard;
import in.proz.adamd.R;

public class AdminRequestActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView back_arrow;
    RelativeLayout search_layout;
    int api_hit_status;
    String type,type_title,date_str,tag;
    TextView title;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_request);
        Bundle b= getIntent().getExtras();
        if(b!=null){
            type = b.getString("type");
            type_title = b.getString("type_title");
            date_str = b.getString("date_str");
        }
        initView();
    }
    public void initView(){
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager layoutManager1=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager1);
        back_arrow = findViewById(R.id.back_arrow);
        title = findViewById(R.id.title);
        title.setText(type_title);
        search_layout = findViewById(R.id.search_layout);
        search_layout.setOnClickListener(this);
        back_arrow.setOnClickListener(this);

        if(type.equals("1")){
            api_hit_status=1;
            tag="No.of Employees";
            title.setText("Leave List");
        }else if(type.equals("2")){
            api_hit_status=2;
            tag="No.of Present";
            title.setText("On Duty List");
        }else if(type.equals("3")){
            api_hit_status=3;
            tag="No.of Absent";
            title.setText("Claim List");
        }else if(type.equals("4")){
            api_hit_status=4;
            tag="No.of Leave";
            title.setText("Assets List");
        }else if(type.equals("5")){
            api_hit_status=5;
            tag="No.of Late";
            title.setText("Tickets List");
        }else if(type.equals("6")){
            api_hit_status=6;
            tag="No.of Permission";
            title.setText("Loan List");
        }
    }
    public void OnBackPRess(){
        Intent intent = new Intent(AdminRequestActivity.this, AdminNewDashboard.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        OnBackPRess();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back_arrow:
                OnBackPRess();
                break;
            case R.id.search_layout:
                break;
        }
    }
}
