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

/**
 * Completed by Kan Bu on 8/06/2019.
 *
 * Array Adapter for the List Views both in "EditCompActivity" and "AddCompResultsActivity"
 * which present a list of competitions in a scrolled list
 */

public class EditCompListAdapter extends ArrayAdapter<Competition> {
    // Local variables
    private Activity context;
    private List<Competition> compList;

    // Constructor
    public EditCompListAdapter(Activity context, List<Competition> compList){
        super(context, R.layout.activity_edit_comp, compList);
        this.context = context;
        this.compList = compList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.listview_comp,null, true);

        TextView compName = (TextView) listViewItem.findViewById(R.id.listView_compName);
        TextView compDate = (TextView) listViewItem.findViewById(R.id.listView_compDate);

        // Get the selected competition
        Competition comp = compList.get(position);
        // Set up the competition name and date on each competition item
        compName.setText(comp.getCname());
        compDate.setText(comp.getDate());

        return listViewItem;
    }
}
