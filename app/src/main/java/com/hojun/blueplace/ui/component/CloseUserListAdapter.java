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
import com.hojun.blueplace.database.CloseUser;

import java.util.ArrayList;
import java.util.List;

public class CloseUserListAdapter extends RecyclerView.Adapter<CloseUserListAdapter.ViewHolder> {
    List<CloseUser> closeUsers;
    List<Integer> selectedUserIds;


    public CloseUserListAdapter() {
        super();
        closeUsers = null;
        selectedUserIds = new ArrayList<>();
    }

    public void setCloseUsers(List<CloseUser> closeUsers) {
        this.closeUsers = closeUsers;
        selectedUserIds.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedUserIds(){
        return selectedUserIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.viewholder_close_user,parent,false);
        CloseUserListAdapter.ViewHolder vh = new CloseUserListAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CloseUser closeUser = closeUsers.get(position);

        if (selectedUserIds.contains(closeUser.id)) {
            holder.view.setBackgroundColor(Color.GRAY);
        } else {
            holder.view.setBackgroundColor(Color.DKGRAY);
        }

        holder.userIdTextView.setText("Id: " + closeUser.id);
        holder.userEmailTextView.setText("Email: " + closeUser.email);
        holder.userPhoneNumberTextView.setText("Phone Number: " + closeUser.phoneNumber);
    }

    @Override
    public int getItemCount() {
        if (closeUsers == null) {
            return 0;
        }
        return closeUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView userIdTextView;
        TextView userEmailTextView;
        TextView userPhoneNumberTextView;

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
                        CloseUser closeUser = closeUsers.get(position);

                        if (selectedUserIds.contains(closeUser.id)) {
                            // 선택 해제
                            int index = selectedUserIds.indexOf(closeUser.id);
                            selectedUserIds.remove(index);
                            v.setBackgroundColor(Color.DKGRAY);
                        } else {
                            // 선택
                            selectedUserIds.add(closeUser.id);
                            v.setBackgroundColor(Color.GRAY);
                        }
                    }
                }
            });

            userIdTextView = view.findViewById(R.id.userIdTextView);
            userEmailTextView = view.findViewById(R.id.userEmailTextView);
            userPhoneNumberTextView = view.findViewById(R.id.userPhoneNumberTextView);
        }
    }
}
