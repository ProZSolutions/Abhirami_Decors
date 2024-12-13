package in.proz.adamd.NotesActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import in.proz.adamd.Adapter.NotesAdapter;
import in.proz.adamd.DashboardNewActivity;
import in.proz.adamd.Map.MapCurrentLocation;
import in.proz.adamd.ModalClass.NotesModal;
import in.proz.adamd.Profile.ProfileActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.NotesTable;

public class NotesActivity extends AppCompatActivity implements View.OnClickListener {
      EditText edt_title, edt_details;
    LinearLayout request_layout, reset_layout, apply_leave_layout, listLayout, frame_layout;
     RecyclerView recyclerView;
    TextView title;
    ImageView back_arrow ,mike;
    ImageView frame_icon;
    TextView frame_tag;
    TextView no_data;
    CommonClass commonClass = new CommonClass();
    NotesTable notesTable;
    List<NotesModal> notesModalList;
    MKLoader loader;
    TextView submit_text;
    NotesModal notesModal;

    LinearLayout nhome_layout,nprofile_layout,nreports_layout,nlocation_layout;
    ImageView iimgnotes;
    TextView txtnote;

    /*  LinearLayout online_layout;
    ImageView online_icon;
    TextView online_text;*/
    String intent_version,admin ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_notes);
         initView();
        Bundle b= getIntent().getExtras();
        if(b!=null){
            admin = b.getString("admin");
            notesModal = (NotesModal) b.getSerializable("edit_content");
            intent_version = b.getString("intent_version");
            submit_text.setText("Update");
        }
        if(notesModal!=null){
            updateUI(notesModal);
        }
    }

    private void updateUI(NotesModal notesModal) {
        edt_title.setText(notesModal.getTitle());
        edt_details.setText(notesModal.getDesc());
        listLayout.setVisibility(View.GONE);
        apply_leave_layout.setVisibility(View.VISIBLE);
        frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
        frame_tag.setText("Note List");
    }

    private void initView() {
        TextView header_title = findViewById(R.id.header_title);
        header_title.setText(commonClass.getSharedPref(getApplicationContext(),"EmppName"));

        iimgnotes = findViewById(R.id.iimgnotes);
        txtnote = findViewById(R.id.txtnote);
        iimgnotes.setImageTintList(getApplicationContext().getColorStateList(R.color.n_org));
        txtnote.setTextColor(getApplicationContext().getColor(R.color.black));
        nhome_layout= findViewById(R.id.nhome_layout);
        nprofile_layout= findViewById(R.id.nprofile_layout);
        nreports_layout= findViewById(R.id.nreports_layout);
        nlocation_layout= findViewById(R.id.nlocation_layout);
        nhome_layout.setOnClickListener(this);
        nprofile_layout.setOnClickListener(this);
        nreports_layout.setOnClickListener(this);
        nlocation_layout.setOnClickListener(this);


        no_data = findViewById(R.id.no_data);
        mike = findViewById(R.id.mike);
        mike.setOnClickListener(this);
      /*  CommonClass comm = new CommonClass();
        online_icon = findViewById(R.id.online_icon);
        online_layout = findViewById(R.id.online_layout);
        online_text = findViewById(R.id.online_text);
        comm.onlineStatusCheck(NotesActivity.this,online_layout,online_text,online_icon);*/
        submit_text = findViewById(R.id.submit_text);
        frame_layout = findViewById(R.id.frame_layout);
        frame_layout.setOnClickListener(this);
        frame_icon = findViewById(R.id.frame_icon);
        frame_tag =findViewById(R.id.frame_tag);

        loader = findViewById(R.id.loader);
        notesModalList=new ArrayList<>();
        notesTable = new NotesTable(NotesActivity.this);
        notesTable.getWritableDatabase();
        notesModalList = notesTable.getAllList(null);

          back_arrow = findViewById(R.id.back_arrow);

        edt_details = findViewById(R.id.edt_details);
        edt_title =findViewById(R.id.edt_title);

        request_layout = findViewById(R.id.request_layout);
        reset_layout = findViewById(R.id.reset_layout);
        apply_leave_layout = findViewById(R.id.apply_leave_layout);
        listLayout = findViewById(R.id.listLayout);

        recyclerView = findViewById(R.id.recyclerView);
         back_arrow.setOnClickListener(this);

        reset_layout.setOnClickListener(this);
        request_layout.setOnClickListener(this);

         GridLayoutManager layoutManager=new GridLayoutManager(getApplicationContext(),1);
        recyclerView.setLayoutManager(layoutManager);

        if(notesModalList.size()!=0) {
            no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            NotesAdapter adapter = new NotesAdapter(NotesActivity.this, notesModalList, recyclerView);
            recyclerView.setAdapter(adapter);
        }else {
            no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callBackIntent();
    }

    private void callBackIntent() {
        if(!TextUtils.isEmpty(intent_version)){
            Intent intent = new Intent(NotesActivity.this, DashboardNewActivity.class);
            startActivity(intent);
        }else if(!TextUtils.isEmpty(admin)){
            Intent intent = new Intent(NotesActivity.this, DashboardNewActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(NotesActivity.this, DashboardNewActivity.class);
            startActivity(intent);
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS);
                String str_rem = edt_details.getText().toString();
                if (TextUtils.isEmpty(str_rem)) {
                    str_rem = Objects.requireNonNull(result).get(0);
                } else {
                    str_rem = str_rem + " " + Objects.requireNonNull(result).get(0);
                }
                edt_details.setText(str_rem);
            }
        }
    }
    private void recordVoiceToText( int i) {
        Intent intent4
                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
     /*   intent4.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault());*/
        intent4.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text");

        try {
            startActivityForResult(intent4, i);
        }
        catch (Exception e) {
            Toast.makeText(NotesActivity.this, " " + e.getMessage(),
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.nhome_layout:
                Intent intent1 = new Intent(getApplicationContext(), DashboardNewActivity.class);
                startActivity(intent1);
                break;
            case R.id.nprofile_layout:
                Intent intentabout = new Intent(getApplicationContext(), ProfileActivity.class);
                intentabout.putExtra("admin","admin");
                startActivity(intentabout);
                break;
            case R.id.nreports_layout:
                /*Intent notes=new Intent(getApplicationContext(), NotesActivity.class);
                notes.putExtra("admin","admin");
                startActivity(notes);*/
                break;
            case R.id.nlocation_layout:
                Intent intent = new Intent(getApplicationContext(), MapCurrentLocation.class);
                intent.putExtra("admin","admin");
                startActivity(intent);
                break;
            case R.id.mike:
                recordVoiceToText(1);
                break;
            case R.id.back_arrow:
                callBackIntent();
                break;

            case R.id.reset_layout:
                callResetUI();
                break;
            case R.id.request_layout:
                if(TextUtils.isEmpty(edt_title.getText().toString())){
                    commonClass.showWarning(NotesActivity.this,"Enter title");
                }else  if(TextUtils.isEmpty(edt_details.getText().toString())){
                    commonClass.showWarning(NotesActivity.this,"Enter Description");
                }else{
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String date = df3.format(new Date());
                    if(notesModal!=null){
                        long getId = notesTable.updateData(edt_title.getText().toString(),edt_details.getText().toString(),date,notesModal.getId());
                        if(getId!=0){
                            callResetUI();
                            commonClass.showSuccess(NotesActivity.this,"Notes Updated Successfully");
                            apply_leave_layout.setVisibility(View.GONE);
                            listLayout.setVisibility(View.VISIBLE);
                            frame_tag.setText("Add Notes");
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                            // change_layout.setText("Note Request");
                            notesModalList = notesTable.getAllList(null);
                            if(notesModalList.size()!=0) {
                                no_data.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                NotesAdapter adapter = new NotesAdapter(NotesActivity.this, notesModalList, recyclerView);
                                recyclerView.setAdapter(adapter);
                            }else {
                                no_data.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }else{
                            commonClass.showError(NotesActivity.this,"Failed to add notes");
                        }
                    }else{
                        long getId = notesTable.insertData(edt_title.getText().toString(),edt_details.getText().toString(),date);
                        if(getId!=0){
                            callResetUI();
                            commonClass.showSuccess(NotesActivity.this,"Notes Saved Successfully");
                            apply_leave_layout.setVisibility(View.GONE);
                            listLayout.setVisibility(View.VISIBLE);
                            frame_tag.setText("Add Notes");
                            frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                            // change_layout.setText("Note Request");
                            notesModalList = notesTable.getAllList(null);
                            if(notesModalList.size()!=0) {
                                no_data.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                NotesAdapter adapter = new NotesAdapter(NotesActivity.this, notesModalList, recyclerView);
                                recyclerView.setAdapter(adapter);
                            }else {
                                no_data.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }else{
                            commonClass.showError(NotesActivity.this,"Failed to add notes");
                        }
                    }


                }
                break;

            case R.id.frame_layout:
                if(apply_leave_layout.getVisibility()==View.VISIBLE){
                    listLayout.setVisibility(View.VISIBLE);
                    apply_leave_layout.setVisibility(View.GONE);
                    frame_tag.setText("Add Notes");
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.add_circle_white));

                    notesModalList = notesTable.getAllList(null);
                    NotesAdapter adapter =new NotesAdapter(NotesActivity.this,notesModalList,recyclerView);
                    recyclerView.setAdapter(adapter);
                }else{
                    listLayout.setVisibility(View.GONE);
                    apply_leave_layout.setVisibility(View.VISIBLE);
                    frame_icon.setImageDrawable(getResources().getDrawable(R.drawable.calendar_icon_new));
                    frame_tag.setText("Note List");
                }
                break;

        }
    }

    private void callResetUI() {

        edt_title.setText(null);
        edt_details.setText(null);

    }


}
