package in.proz.adamd.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import in.proz.adamd.ModalClass.NotesModal;
import in.proz.adamd.NotesActivity.NotesActivity;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;
import in.proz.adamd.SQLiteDB.NotesTable;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ProductViewHolder> {
    List<NotesModal> notesModalList;
    Context context;
    RecyclerView recyclerView;
    NotesTable notesTable;
    CommonClass commonClass = new CommonClass();
    public NotesAdapter(Context context,List<NotesModal> notesModalList,RecyclerView recyclerView){
        this.context=context;
        this.notesModalList=notesModalList;
        this.recyclerView=recyclerView;
        notesTable= new NotesTable(context);
        notesTable.getWritableDatabase();
    }
    private String getTime(String string) {
        String[] arr1=string.split(" ");
        String[] arr2=arr1[1].split(":");
        int rt=0;
        Integer hr = Integer.parseInt(arr2[0]);
        String str_date;
        if(hr>=12){
            if(hr!=12){
                hr=hr-12;
            }

            str_date = hr+":"+arr2[1]+" PM";

        }else{
            str_date = hr+":"+arr2[1]+" AM";
        }
        return arr1[0]+" "+str_date;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.activity_notes_row,null);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 300, 0);
        Animation alphaAnimation = new AlphaAnimation(0, 1);
        translateAnimation.setDuration(500);
        alphaAnimation.setDuration(1300);
        AnimationSet animation = new AnimationSet(true);
        animation.addAnimation(translateAnimation);
        animation.addAnimation(alphaAnimation);
        view.setAnimation(animation);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        NotesModal modal=notesModalList.get(position);
        holder.date.setText(getTime(modal.getDate()));
        holder.desc.setText(modal.getDesc());
        holder.title.setText(modal.getTitle());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
                TextView msg = view.findViewById(R.id.msg);
                TextView cancel_text = view.findViewById(R.id.cancel_text);
                cancel_text.setText("NO");

                TextView btn_title1 = view.findViewById(R.id.btn_title1);
                TextView btn_title = view.findViewById(R.id.btn_title);
                btn_title1.setText("YES");
                btn_title.setText("YES");

                msg.setText("Would you like to delete it?");
                LinearLayout approve = view.findViewById(R.id.approve);
                approve.setVisibility(View.GONE);
                LinearLayout decline = view.findViewById(R.id.decline);
                decline.setVisibility(View.VISIBLE);
                LinearLayout cancel = view.findViewById(R.id.cancel);
                builder.setView(view);
                final android.app.AlertDialog mDialog = builder.create();
                mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                mDialog.create();
                mDialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        notesTable.deleteRecord(modal.getId());
                        commonClass.showSuccess((Activity) context,"Notes Deleted Successfully");
                        notesModalList.remove(position);
                        recyclerView.setAdapter(null);
                        if(notesModalList.size()==0){
                            Intent intent = new Intent(context,NotesActivity.class);
                            context.startActivity(intent);
                        }else{
                            NotesAdapter adapter = new NotesAdapter(context,notesModalList,recyclerView);
                            recyclerView.setAdapter(adapter);
                        }

                    }
                });



            }
        });
        holder.edit_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                android.app.AlertDialog.Builder builder=new android.app.AlertDialog.Builder(context);
                View view= LayoutInflater.from(context).inflate(R.layout.new_warning_dlg,null);
                TextView msg = view.findViewById(R.id.msg);
                TextView cancel_text = view.findViewById(R.id.cancel_text);
                cancel_text.setText("NO");
                TextView btn_title1 = view.findViewById(R.id.btn_title1);
                TextView btn_title = view.findViewById(R.id.btn_title);
                btn_title1.setText("YES");
                btn_title.setText("YES");

                msg.setText("Would you like to edit?");
                LinearLayout approve = view.findViewById(R.id.approve);
                approve.setVisibility(View.GONE);
                LinearLayout decline = view.findViewById(R.id.decline);
                decline.setVisibility(View.VISIBLE);
                LinearLayout cancel = view.findViewById(R.id.cancel);
                builder.setView(view);
                final android.app.AlertDialog mDialog = builder.create();
                mDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
                mDialog.create();
                mDialog.show();
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
                decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                        Intent intent = new Intent(context, NotesActivity.class);
                        intent.putExtra("edit_content",modal);
                        context.startActivity(intent);
                    }
                });





            }
        });
    }

    @Override
    public int getItemCount() {
        return notesModalList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView title,desc,date;
         ImageView delete,edit_icon;
        public ProductViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            desc = view.findViewById(R.id.desc);
            delete = view.findViewById(R.id.delete);
            edit_icon = view.findViewById(R.id.edit_icon);
            date = view.findViewById(R.id.date);



        }
    }
}
