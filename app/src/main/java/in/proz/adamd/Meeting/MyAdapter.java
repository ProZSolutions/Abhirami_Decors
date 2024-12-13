package in.proz.adamd.Meeting;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import in.proz.adamd.R;
import in.proz.adamd.Retrofit.CommonClass;


public class MyAdapter extends ArrayAdapter<StateVO> {
    private Context mContext;
    private ArrayList<StateVO> listState;
    CommonClass commonClass= new CommonClass();
    private MyAdapter myAdapter;
    private boolean isFromView = false;
    TextView textView;
    Spinner multipleItemSelectionSpinner;
    CheckBox checkBox; TextView check_all_text;

    public MyAdapter(Context context, int resource, List<StateVO> objects,
                     TextView textView, Spinner multipleItemSelectionSpinner,
                     CheckBox checkBox, TextView check_all_text) {
        super(context, resource, objects);
        this.mContext = context;
        this.listState = (ArrayList<StateVO>) objects;
        this.myAdapter = this;
        this.textView = textView;
        this.check_all_text=check_all_text;;
        this.checkBox = checkBox;
        this.multipleItemSelectionSpinner = multipleItemSelectionSpinner;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    public View getCustomView(final int position, View convertView,
                              ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater layoutInflator = LayoutInflater.from(mContext);
            convertView = layoutInflator.inflate(R.layout.spinner_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView
                    .findViewById(R.id.text);
            holder.mCheckBox = (CheckBox) convertView
                    .findViewById(R.id.checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mTextView.setText(listState.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.mCheckBox.setChecked(listState.get(position).isSelected());
        isFromView = false;


        holder.mCheckBox.setTag(position);
        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

           /*     if(position==0){
                         if(isChecked){
                            for(int i=0;i<listState.size();i++){
                                listState.get(i).setSelected(true);
                            }
                             removeAllChecks(parent,true);
                         }else{
                            for(int i=0;i<listState.size();i++){
                                listState.get(i).setSelected(false);
                            }
                             removeAllChecks(parent,false);
                        }

                    updateList(listState,textView,parent);
                }else{*/
                    if (!isFromView) {
                        listState.get(position).setSelected(isChecked);
                    }
                    updateList(listState,textView,parent);
                //}




            }
        });
        return convertView;
    }
    private void removeAllChecks(ViewGroup vg,boolean condition) {
        View v = null;
        for(int i = 0; i < vg.getChildCount(); i++){
            try {
                v = vg.getChildAt(i);
                ((CheckBox)v).setChecked(condition);
            }
            catch(Exception e1){ //if not checkBox, null View, etc
                try {
                    removeAllChecks((ViewGroup)v,condition);
                }
                catch(Exception e2){ //v is not a view group
                    continue;
                }
            }
        }

    }

    private void updateList(ArrayList<StateVO> listState, TextView textView, ViewGroup vg) {
        int count=0;
        String main_title="";
        List<String> subString= new ArrayList<>();
        for(int i=0;i<listState.size();i++){
            if(listState.get(i).isSelected()){
                count+=1;
                Log.d("insertMeeting"," link "+listState.get(i).getTitle());
                 main_title=listState.get(i).getTitle()+","+main_title;
                 /*String[] split_str = listState.get(i).getTitle().split("-");
                 if(split_str.length>1) {
                     //subString.add(split_str[1]);
                     subString.add(listState.get(i).getTitle());
                 }*/

                if(!listState.get(i).getTitle().contains("Select")){
                    // subString.add(listState.get(i).getTitle());
                     /*if(listState.get(i).getTitle().contains("-")){
                         String[] strSplit = listState.get(i).getTitle().split("-");
                         if(strSplit.length>1){
                             String str="";
                             for(int k=1;k<strSplit.length;k++){
                                 str=str+strSplit[k];
                             }
                             Log.d("insertMeeting"," str s "+str);
                             subString.add(str);
                         }
                     }*/
                    subString.add(listState.get(i).getTitle());
                    Log.d("insertMeeting"," getList "+listState.get(i).getTitle()+" str ");
                }
            }
        }


        if(count!=0) {
            String listString = String.join(",", subString);
            commonClass.putSharedPref(mContext,"members",listString);
            if (count == 1) {
               // textView.setText(main_title);
                textView.setText("1 Member Selected");
            } else {
                String[] len = main_title.split(",");
                if(listState.size() == len.length){
                    checkBox.setChecked(true);
                    check_all_text.setText("UnCheck All");
                    textView.setText("All Selected");
                }else {
                    textView.setText(String.valueOf(len.length) + " Members Selected");
                }
            }
        }else{
            textView.setText("0 Members");
            commonClass.putSharedPref(mContext,"members",null);
        }
    /*    MyAdapter myAdapter = new MyAdapter(mContext, 0,
                listState,textView,multipleItemSelectionSpinner);
      multipleItemSelectionSpinner.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
        multipleItemSelectionSpinner.performClick();*/




        Log.d("getMembers"," mem" +commonClass.getSharedPref(mContext,"members"));

    }

    private class ViewHolder {
        private TextView mTextView;
        private CheckBox mCheckBox;
    }
}