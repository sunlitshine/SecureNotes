package com.shansong.securenotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shansong.securenotes.R;
import com.shansong.securenotes.models.SecureNote;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private List<SecureNote> mNoteList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitleView;
        public TextView mDateView;

        public MyViewHolder(View view){
            super(view);
            mTitleView = (TextView) view.findViewById(R.id.lbl_title);
            mDateView = (TextView) view.findViewById(R.id.lbl_date);
        }
    }

    public NotesAdapter(List<SecureNote> noteList) {
        this.mNoteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SecureNote note = mNoteList.get(position);
        holder.mTitleView.setText(note.getTitle().toUpperCase());
        holder.mDateView.setText(note.getDateTime());
    }

    @Override
    public int getItemCount(){
        return  mNoteList.size();
    }
}
