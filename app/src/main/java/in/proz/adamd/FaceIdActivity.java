package in.proz.adamd;

import android.content.Intent;
import android.os.Build;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import in.proz.adamd.Retrofit.CommonClass;

public class FaceIdActivity extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout outer_layout_1;
    CommonClass commonClass;
    ImageView back_arrow;




    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pactivity_faceid);
        commonClass = new CommonClass();
        outer_layout_1 = findViewById(R.id.outer_layout_1);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        outer_layout_1.startAnimation(animation);
        animation.setRepeatCount(Animation.INFINITE);
        initView();



    }

    private void initView() {
        back_arrow = findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.back_arrow:
                callLogin();
                break;
        }
    }

    private void callLogin() {
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        callLogin();
    }
}
