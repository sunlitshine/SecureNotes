package com.shansong.securenotes.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shansong.securenotes.R;
import com.shansong.securenotes.data.SecureNote;

import java.util.List;

/**
 * Created by bolorundurowb on 16-Jul-16.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private List<SecureNote> notesList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, content, date;

        public MyViewHolder(View view){
            super(view);
            title = (TextView) view.findViewById(R.id.lbl_title);
            content = (TextView) view.findViewById(R.id.lbl_details);
            date = (TextView) view.findViewById(R.id.lbl_date);
        }
    }

    public NotesAdapter(List<SecureNote> noteList) {
        this.notesList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_row, parent, false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SecureNote note = notesList.get(position);
        holder.title.setText(note.getTitle().toUpperCase());
        holder.content.setText(note.getContent());

//        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
//        String reportDate = df.format(note.getDateTime());

        holder.date.setText(note.getDateTime());
    }

    @Override
    public int getItemCount(){
        return  notesList.size();
    }
}
