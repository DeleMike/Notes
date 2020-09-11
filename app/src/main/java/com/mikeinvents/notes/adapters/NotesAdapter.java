package com.mikeinvents.notes.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mikeinvents.notes.R;
import com.mikeinvents.notes.helpers.NoteHelper;
import com.mikeinvents.notes.models.Note;

import androidx.recyclerview.widget.RecyclerView;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>{
    private static final String TAG = "NotesAdapter";
    private Context mContext;
    private NoteHelper mDb;

    public NotesAdapter(Context context, NoteHelper db){
        mContext = context;
        mDb = db;
        
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.notes_card, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NotesAdapter.ViewHolder holder, int position) {
        final Note note = mDb.query(position);
        if(note.getTitle().equalsIgnoreCase("")){
            holder.mTitle.setText(note.getDetails());
            Log.i(TAG, "ViewHolder: mTitle = "+note.getDetails());
            holder.mTitle.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }else{
            holder.mTitle.setText(note.getTitle());
            Log.i(TAG, "ViewHolder: mTitle = "+note.getTitle());
        }

        holder.mDate.setText("Created: " + note.getDate());
        Log.i(TAG, "ViewHolder: mDate = "+note.getDate());

        long id = note.getId();
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return  mDb == null ? 0 : (int) mDb.count();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDate;

        ViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.notes_title);
            mDate = itemView.findViewById(R.id.notes_date);
        }
    }
}
