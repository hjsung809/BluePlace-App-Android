package com.hojun.blueplace.ui.component;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hojun.blueplace.R;
import com.hojun.blueplace.database.Clique;
import com.hojun.blueplace.database.CloseUser;

import java.util.ArrayList;
import java.util.List;

public class CliqueListAdapter extends RecyclerView.Adapter<CliqueListAdapter.ViewHolder> {
    List<Clique> cliques;
    List<Integer> selectedCliqueIds;


    public CliqueListAdapter() {
        super();
        cliques = null;
        selectedCliqueIds = new ArrayList<>();
    }

    public void setCliques(List<Clique> cliques) {
        this.cliques = cliques;
        selectedCliqueIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedCliqueIds(){
        return selectedCliqueIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewholder_clique,parent,false);
        CliqueListAdapter.ViewHolder vh = new CliqueListAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Clique clique = cliques.get(position);

        if (selectedCliqueIds.contains(clique.id)) {
            holder.view.setBackgroundColor(Color.GRAY);
        } else {
            holder.view.setBackgroundColor(Color.DKGRAY);
        }

        holder.cliqueIdTextView.setText("ID: " + clique.id);
        holder.cliqueNameTextView.setText("Name: " + clique.name);
        holder.cliqueOwnerIdTextView.setText("Owner ID: " + clique.ownerId);
    }

    @Override
    public int getItemCount() {
        if (cliques == null) {
            return 0;
        }
        return cliques.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView cliqueIdTextView;
        TextView cliqueNameTextView;
        TextView cliqueOwnerIdTextView;

        ViewHolder(View view) {
            super(view);
            this.view = view;

            // 뷰 클릭시.
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // 클릭된 유저 받아오기.
                        Clique clique = cliques.get(position);

                        if (selectedCliqueIds.contains(clique.id)) {
                            // 선택 해제
                            int index = selectedCliqueIds.indexOf(clique.id);
                            selectedCliqueIds.remove(index);
                            v.setBackgroundColor(Color.DKGRAY);
                        } else {
                            // 선택
                            selectedCliqueIds.add(clique.id);
                            v.setBackgroundColor(Color.GRAY);
                        }
                    }
                }
            });

            cliqueIdTextView = view.findViewById(R.id.cliqueIdTextView);
            cliqueNameTextView = view.findViewById(R.id.cliqueNameTextView);
            cliqueOwnerIdTextView = view.findViewById(R.id.cliqueOwnerIdTextView);
        }
    }
}
