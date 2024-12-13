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

import java.util.ArrayList;
import java.util.List;

import in.proz.adamd.AdminModule.SQLiteDB.BranchDB;
import in.proz.adamd.ModalClass.KeystoneModal;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.NotesTable;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ProductViewHolder> {
    List<KeystoneModal> notesModalList;
    Context context;
    RecyclerView recyclerView;
    int pos =0;
    NotesTable notesTable;
    CommonClass commonClass = new CommonClass();
    TextView brachname,departmentname;
    public BranchAdapter(Context context,List<KeystoneModal> notesModalList,RecyclerView recyclerView,int pos,TextView brachname,
                         TextView departmentname){
        this.context=context;
        this.departmentname=departmentname;
        this.pos=pos;
        this.notesModalList=notesModalList;
        this.recyclerView=recyclerView;
        this.brachname = brachname;
        notesTable= new NotesTable(context);
        notesTable.getWritableDatabase();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.new_filter_row,null);
   /*     TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);*/

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        KeystoneModal modal=notesModalList.get(position);
        holder.radiobutton.setVisibility(View.VISIBLE);
        holder.checkbox.setVisibility(View.GONE);
        holder.title.setText(modal.getTitle());
        if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"branch_id"))){
            if(String.valueOf(position).equals(commonClass.getSharedPref(context,"branch_id"))){
                commonClass.putSharedPref(context,"branch_id",String.valueOf(position));
                holder.radiobutton.setChecked(true);
                BranchAdapter adapter = new BranchAdapter(context,notesModalList,recyclerView,position,brachname,departmentname);
                recyclerView.setAdapter(adapter);
            }
        }else{
            if(position==0){
                holder.radiobutton.setChecked(true);
                commonClass.putSharedPref(context,"branch_id",String.valueOf(position));
                BranchAdapter adapter = new BranchAdapter(context,notesModalList,recyclerView,position,brachname,departmentname);
                recyclerView.setAdapter(adapter);
            }

        }
        holder.radiobutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    commonClass.putSharedPref(context,"branch_id",String.valueOf(position));
                    commonClass.putSharedPref(context,"department_id",null);
                    if(commonClass.getSharedPref(context,"branch_id").equals("0")){
                        brachname.setText("All Branch");
                        departmentname.setText("Department");
                     }else{
                        departmentname.setText("Department");
                        int pos =Integer.parseInt(commonClass.getSharedPref(context,"branch_id"));
                        BranchDB branchDB = new BranchDB(context);
                        branchDB.getWritableDatabase();
                        List<String> branchNameList = new ArrayList<>();
                        if(pos!=0){
                              branchNameList =     branchDB.SelectDistrictList();
                            String id=branchDB.selectDistrictId(branchNameList.get(pos));
                         }
                        String name = branchNameList.get(Integer.parseInt(commonClass.getSharedPref(context,"branch_id")));
                        brachname.setText(name);
                    }


                    holder.radiobutton.setChecked(true);
                    BranchAdapter adapter = new BranchAdapter(context,notesModalList,recyclerView,position,brachname,departmentname);
                    recyclerView.setAdapter(adapter);
                }else{
                 }
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
