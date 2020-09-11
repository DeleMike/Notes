package com.mikeinvents.notes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikeinvents.notes.R;
import com.mikeinvents.notes.helpers.NoteHelper;
import com.mikeinvents.notes.models.Note;

public class DetailsActivity extends AppCompatActivity {
    public static final String EXTRA_TITLE_REPLY = "REPLY_CAPTION";
    public static final String EXTRA_DETAILS_REPLY = "REPLY_DETAILS";
    public static final String EXTRA_DATE_REPLY = "REPLY_DATE";

    public static final String WORD_EDIT = "word_edit";
    public static final String NOTES_ID = "notes_id";

    private int mId = MainActivity.NOTES_ADD;

    private EditText mTitleEditText,mDetailsEditText;
    private TextView mTextViewDate;
    private Button button;
    private NoteHelper mDb;
    private String date = " ";
    private int mIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = findViewById(R.id.details_toolbar);
        setSupportActionBar(toolbar);

        mDb = new NoteHelper(this);
        date += mDb.getDate();
        mTitleEditText = findViewById(R.id.details_edit_title);
        mDetailsEditText = findViewById(R.id.details_edit_the_detail);
        mTextViewDate = findViewById(R.id.textView);
        button = findViewById(R.id.save_button);
        mDetailsEditText.requestFocus();
        mTextViewDate.append(date);

        checkIntents();
        saveButton();
    }

    private void checkIntents() {
        //check if it was the FAB button pressed
        Intent intent = getIntent();
        String mainActivityIntent = intent.getStringExtra(MainActivity.MAIN_EXTRA_ID);
        if(mainActivityIntent == null){
            makeReadable();
        }
    }

    private void makeReadable() {
        Intent intent = getIntent();
        mIndex = intent.getIntExtra(MainActivity.CARD_ID, -99);
        Note aNote = mDb.query(mIndex);
        mTitleEditText.setText(aNote.getTitle());
        mTitleEditText.setTextColor(Color.BLACK);
        mDetailsEditText.setText(aNote.getDetails());
        mDetailsEditText.setTextColor(Color.BLACK);

        mTitleEditText.setEnabled(false);
        mDetailsEditText.setEnabled(false);
        button.setVisibility(View.GONE);

    }

    private void saveButton() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String newRecordIntent = intent.getStringExtra(MainActivity.MAIN_EXTRA_ID);

                //checking if it is an update intent
                if(newRecordIntent == null){
                    updateData();
                    
                }else {
                    //get the entered data
                    String title = mTitleEditText.getText().toString();
                    String details = mDetailsEditText.getText().toString();

                    //pass the data through an intent
                    Intent newNote = new Intent();
                    newNote.putExtra(EXTRA_TITLE_REPLY, title);
                    newNote.putExtra(EXTRA_DETAILS_REPLY, details);
                    newNote.putExtra(EXTRA_DATE_REPLY, date);

                    //pass an id to locate the intent in MainActivity
                    newNote.putExtra(NOTES_ID, mId);

                    setResult(RESULT_OK, newNote);
                    finish();
                }
            }
        });
    }

    private void updateData() {
        //Toast.makeText(this, "Updating database", Toast.LENGTH_SHORT).show();
        Note note = mDb.query(mIndex);
        if(note.getId() <= mDb.maxID()){
            String title = mTitleEditText.getText().toString();
            String details = mDetailsEditText.getText().toString();

            //pass the data through an intent
            Intent updateNote = new Intent();
            updateNote.putExtra(EXTRA_TITLE_REPLY, title);
            updateNote.putExtra(EXTRA_DETAILS_REPLY, details);
            updateNote.putExtra(EXTRA_DATE_REPLY, note.getDate());
            updateNote.putExtra(NOTES_ID, mId);
            updateNote.putExtra(WORD_EDIT, mIndex);

            setResult(RESULT_OK, updateNote);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_menu_edit:
                if(!mTitleEditText.isEnabled()){
                    mTitleEditText.setEnabled(true);
                    mDetailsEditText.setEnabled(true);
                    button.setVisibility(View.VISIBLE);
                }

                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
