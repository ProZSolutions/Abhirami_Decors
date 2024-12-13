package in.proz.adamd.AdminFilterAdapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import in.proz.adamd.ModalClass.KeystoneModal;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.NotesTable;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ProductViewHolder> {
    List<KeystoneModal> notesModalList;
    Context context;
    RecyclerView recyclerView;
    int pos =0;
    NotesTable notesTable;
     List<String> idListinner=new ArrayList<>();
     List<String> idlist=new ArrayList<>();
     List<String> onlyName = new ArrayList<>();
    CommonClass commonClass = new CommonClass();
    TextView department_name;
    public DepartmentAdapter(Context context, List<KeystoneModal> notesModalList, RecyclerView recyclerView, int pos,
                             TextView department_name){
        this.context=context;
        this.pos=pos;
        this.notesModalList=notesModalList;
        this.recyclerView=recyclerView;
        notesTable= new NotesTable(context);
        this.department_name = department_name;
        notesTable.getWritableDatabase();
        if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"department_id"))){
            idlist = Arrays.asList(commonClass.getSharedPref(context,"department_id").split(" , "));
            for(int i=0;i<idlist.size();i++){
                idListinner.add(idlist.get(i));
            }

            for(int k=0;k<notesModalList.size();k++){

                if(idListinner.contains(0)){
                    onlyName.add("All Department");
                    break;
                }else{
                    if(idListinner.contains(k)) {
                        if (!onlyName.contains(notesModalList.get(k).getTitle())) {
                            onlyName.add(notesModalList.get(k).getTitle());
                        }
                    }
                }
            }
            if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"branch_id"))){
                if(commonClass.getSharedPref(context,"branch_id").contains("0")){
                    department_name.setText("ALL");
                }else{
                    if(onlyName.size()!=0){
                        String str = String.join(" , ", onlyName);
                        department_name.setText(str);
                    }
                }
            }else{
                department_name.setText("Department");
            }

        }else{
            department_name.setText("Department");
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.new_filter_row,null);
     /*   TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
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
        holder.radiobutton.setVisibility(View.GONE);
        holder.checkbox.setVisibility(View.VISIBLE);
        holder.title.setText(modal.getTitle());

        holder.checkbox.setChecked(false);
        if(idListinner.size()!=0){
            if(idListinner.contains(String.valueOf(position))){
                holder.checkbox.setChecked(true);
             }else{

                holder.checkbox.setChecked(false);
            }
        }


        holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(!idListinner.contains(String.valueOf(position))) {
                        idListinner.add(String.valueOf(position));
                    }
                    String str = String.join(" , ", idListinner);
                    commonClass.putSharedPref(context,"department_id",str);
                    holder.checkbox.setChecked(true);
                    if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"department_id"))) {
                        idlist = Arrays.asList(commonClass.getSharedPref(context, "department_id").split(" , "));
                        idListinner.clear();
                        for (int i = 0; i < idlist.size(); i++) {
                            idListinner.add(idlist.get(i));
                        }
                    }
                     if(idListinner.contains("0")){
                         department_name.setText("All");
                     }else{
                         List<String> list = new ArrayList<>();
                         for (int i=0;i<idListinner.size();i++){
                             list.add(notesModalList.get(Integer.parseInt(idListinner.get(i))).getTitle());
                         }
                         String str1 = String.join(" , ", list);
                         department_name.setText(str1);
                     }

                     if(position==0){
                         idListinner.clear();
                         for(int i=0;i<notesModalList.size();i++){
                             idListinner.add(String.valueOf(i));
                         }
                         String str1 = String.join(" , ", idListinner);
                         commonClass.putSharedPref(context,"department_id",str1);
                     }


                    DepartmentAdapter adapter = new DepartmentAdapter(context,notesModalList,recyclerView,position,department_name);
                    recyclerView.setAdapter(adapter);
                }else{
                    if(!TextUtils.isEmpty(commonClass.getSharedPref(context,"department_id"))) {
                        idlist = Arrays.asList(commonClass.getSharedPref(context, "department_id").split(" , "));
                        idListinner.clear();
                        for (int i = 0; i < idlist.size(); i++) {
                            Log.d("DepartmentList"," get id "+idlist.get(i));
                            idListinner.add(idlist.get(i));
                        }
                    }
                    Log.d("DepartmentList"," size as "+idListinner.size()+" pos "+idListinner.indexOf(position));
                    if(idListinner.size()!=0) {
                        for(int i=0;i<idListinner.size();i++){
                            if(Integer.parseInt(idListinner.get(i))==position){
                                idListinner.remove(String.valueOf(position));
                            }
                        }
                        if(idListinner.size()==0){
                            idListinner.add("0");
                        }

                    }

                    if(position==0){
                        idListinner.clear();
                    }else{
                        idListinner.remove("0");
                    }


                    String str = String.join(" , ", idListinner);
                    commonClass.putSharedPref(context,"department_id",str);
                    holder.checkbox.setChecked(false);

                    if(idListinner.contains(0)){
                        department_name.setText("All");
                    }else{
                        List<String> list = new ArrayList<>();
                        for (int i=0;i<idListinner.size();i++){
                            list.add(notesModalList.get(Integer.parseInt(idListinner.get(i))).getTitle());
                        }
                        String str1 = String.join(" , ", list);
                        department_name.setText(str1);
                    }


                    DepartmentAdapter adapter = new DepartmentAdapter(context,notesModalList,recyclerView,position,department_name);
                    recyclerView.setAdapter(adapter);
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
