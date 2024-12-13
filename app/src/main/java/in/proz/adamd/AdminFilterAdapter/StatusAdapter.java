package in.proz.adamd.AdminFilterAdapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.NotesTable;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ProductViewHolder> {
    List<String> notesModalList;
    Context context;
    RecyclerView recyclerView;
    int pos =0;
    NotesTable notesTable;
    CommonClass commonClass = new CommonClass();
    TextView statusname;
    public StatusAdapter(Context context,List<String> notesModalList,RecyclerView recyclerView,int pos,TextView statusname){
        this.context=context;
         this.pos=pos;
        this.notesModalList=notesModalList;
        this.recyclerView=recyclerView;
        this.statusname = statusname;
        notesTable= new NotesTable(context);
        notesTable.getWritableDatabase();


        if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"status"))){
            statusname.setText(commonClass.getSharedPref(context,"status"));
        }else{
            statusname.setText("Status");
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.new_filter_row,null);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        String modal=notesModalList.get(position);
        holder.radiobutton.setVisibility(View.VISIBLE);
        holder.checkbox.setVisibility(View.GONE);
        holder.title.setText(modal);
        holder.radiobutton.setChecked(false);
        if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"status"))){
            if(commonClass.getSharedPref(context,"status").equals(modal)){
                holder.radiobutton.setChecked(true);
            }else{
                holder.radiobutton.setChecked(false);
            }
        }else{
            holder.radiobutton.setChecked(false);
        }

        holder.radiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    commonClass.putSharedPref(context,"status",modal);
                }else{
                    commonClass.putSharedPref(context,"status",null);
                }
                StatusAdapter adapter = new StatusAdapter(context,notesModalList,recyclerView,0,statusname);
                recyclerView.setAdapter(adapter);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesModalList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        RadioButton radiobutton;
        CheckBox checkbox;
        public ProductViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            radiobutton = view.findViewById(R.id.radiobutton);
            checkbox = view.findViewById(R.id.checkbox);
        }
    }
}
