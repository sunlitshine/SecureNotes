package com.gemalto.mfs.contentprovidertestapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder>{
    private List<String> mNoteList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleView;
        public TextView mDateView;

        public MyViewHolder(View view){
            super(view);
            mTitleView = (TextView) view.findViewById(R.id.lbl_title);
        }
    }

    public NotesAdapter(List<String> noteList) {
        this.mNoteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String title = mNoteList.get(position);
        holder.mTitleView.setText(title);
    }

    @Override
    public int getItemCount(){
        return  mNoteList.size();
    }
}

