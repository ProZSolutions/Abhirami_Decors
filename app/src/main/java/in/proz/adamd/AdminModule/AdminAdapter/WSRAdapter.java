package in.proz.adamd.AdminModule.AdminAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tuyenmonkey.mkloader.MKLoader;

import java.util.List;

import in.proz.adamd.AdminModule.PDFViewerActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.Retrofit.CommonPojo;

public class WSRAdapter extends RecyclerView.Adapter<WSRAdapter.ProductViewHolder>{
    List<CommonPojo> commonPojoList;
    Context context;
    int commonPos;
    RecyclerView recyclerView;
    // ProgressDialog progressDialog;
    CommonClass commonClass;
    MKLoader loader;

    public  WSRAdapter(Activity context, List<CommonPojo> commonPojoList, int commonPos,
                                 RecyclerView recyclerView, MKLoader loader){

        this.context=context;
        this.commonPojoList=commonPojoList;
        this.commonPos = commonPos;
        this.recyclerView=recyclerView;
        this.loader = loader;
      /*  progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);*/
        commonClass = new CommonClass();

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.admin_wsr_row,null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        CommonPojo commonPojo = commonPojoList.get(position);
        holder.emp_id.setText(commonPojo.getEmpno());
        holder.name.setText(commonPojo.getEmpname());
        holder.date.setText("To:"+commonPojo.getTo());
        holder.mobile.setText("CC:"+commonPojo.getCc_name());
        if(!TextUtils.isEmpty(commonPojo.getDocument())){
            holder.back_arrow.setVisibility(View.VISIBLE);
        }else{
            holder.back_arrow.setVisibility(View.GONE);
        }

        holder.back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("getFilePath"," file "+commonPojo.getAttachmentPath());
                Intent intentpdf = new Intent(context, PDFViewerActivity.class);
                intentpdf.putExtra("type","invoice");
                intentpdf.putExtra("pdf_url",commonPojo.getAttachmentPath());
                context.startActivity(intentpdf);
            }
        });

    }
    @Override
    public int getItemCount() {
        return commonPojoList.size();
    }


    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView emp_id,date,name,mobile;
        RelativeLayout relativeLayout;
        ImageView back_arrow;
        public ProductViewHolder(View view) {
            super(view);
            emp_id = view.findViewById(R.id.emp_id);
            back_arrow = view.findViewById(R.id.back_arrow);
             relativeLayout = view.findViewById(R.id.relativeLayout);
            name = view.findViewById(R.id.name);
            mobile = view.findViewById(R.id.mobile);
            date = view.findViewById(R.id.date);
        }
    }

}
