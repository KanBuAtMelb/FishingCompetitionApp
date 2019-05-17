package com.example.fishingtest.Adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.fishingtest.Model.Competition;
import com.example.fishingtest.R;

import java.util.List;

public class EditCompListAdapter extends ArrayAdapter<Competition> {

    private Activity context;
    private List<Competition> compList;

    public EditCompListAdapter(Activity context, List<Competition> compList){
        super(context, R.layout.activity_add_competition, compList);
        this.context = context;
        this.compList = compList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.listview_comp,null, true);

      // Get the Layout Parameters for ListView Current Item View
//        ViewGroup.LayoutParams params = listViewItem.getLayoutParams();
//
//        // Set the height of the Item View
//        params.height = 20;
//        listViewItem.setLayoutParams(params);


        TextView compName = (TextView) listViewItem.findViewById(R.id.listView_compName);
        TextView compDate = (TextView) listViewItem.findViewById(R.id.listView_compDate);

        Competition comp = compList.get(position);

        compName.setText(comp.getCname());
        compDate.setText(comp.getDate());

        // TODO: iF it is an old competition, do something on background image


        return listViewItem;
    }
}
