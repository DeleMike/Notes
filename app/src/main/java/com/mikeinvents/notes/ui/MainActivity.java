package com.mikeinvents.notes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.material.navigation.NavigationView;
import com.mikeinvents.notes.R;
import com.mikeinvents.notes.adapters.NotesAdapter;
import com.mikeinvents.notes.helpers.NoteHelper;
import com.mikeinvents.notes.interfaces.RecyclerTouchListener;
import com.mikeinvents.notes.interfaces.onItemClickListener;
import com.mikeinvents.notes.models.Note;


import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuItem;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
  //  private static final String TAG = "MainActivity";

    public static final int NOTES_EDIT = 1;
    public static final int NOTES_UPDATE = 2;
    public static final int NOTES_ADD = -1;

    public static final String MAIN_EXTRA_ID = "MAIN_ID";
    public static final String CARD_ID = "card_id";

    private RecyclerView mRecyclerView;
    private NotesAdapter notesAdapter;
    private NoteHelper mDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(MAIN_EXTRA_ID, "main_id");
                startActivityForResult(intent, NOTES_EDIT);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
        getNotes();

        navigationView.getMenu().findItem(R.id.nav_all_notes).setTitle("All Notes \t\t\t\t\t" + notesAdapter.getItemCount());
    }

    private void init() {
        mRecyclerView = findViewById(R.id.recycler_view_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            //we want to cache these and not allocate anything in the OnChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.TRANSPARENT);
                xMark = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_clear);
                if (xMark != null) {
                    xMark.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
                }
                xMarkMargin = 8;
                initiated = true;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                AlertDialog.Builder alertBox = new AlertDialog.Builder(MainActivity.this);
                alertBox.setTitle("Delete Item")
                        .setMessage("Do you really want to delete this item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeItem((long) viewHolder.itemView.getTag());
                                NavigationView navigationView = findViewById(R.id.nav_view);
                                navigationView.getMenu().findItem(R.id.nav_all_notes).setTitle("All Notes \t\t\t\t\t"
                                        + notesAdapter.getItemCount());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                notesAdapter.notifyDataSetChanged();
                            }
                        }).setCancelable(false).show();
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                if (viewHolder.getAdapterPosition() == -1) {
                    //view does not exist , dismiss
                    return;
                }

                if (!initiated) {
                    init();
                }

                //draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                        itemView.getRight(), itemView.getBottom());
                background.draw(c);
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicHeight();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                xMark.draw(c);
            }

        }).attachToRecyclerView(mRecyclerView);

        //to view an already existing note
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, new onItemClickListener() {
            @Override
            public void onClick(View view, int index) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra(CARD_ID, index);
                startActivityForResult(intent, NOTES_UPDATE);
            }
        }));
    }

    /**
     * Returns the Notes in the DB
     */
    private void getNotes() {
        mDB = new NoteHelper(this);
        //Log.i(TAG, "getNotes: NoteHelper Called");
        notesAdapter = new NotesAdapter(this, mDB);
        mRecyclerView.setAdapter(notesAdapter);
    }

    /**
     * Used to delete a Note from the DB; it locates the item by the TAG passed in the Adapter Class
     */
    private void removeItem(long tag) {
        try {
            if (mDB.mWritableDB == null) {
                mDB.mWritableDB = mDB.getWritableDatabase(); //get the writable DB
            }

            mDB.mWritableDB.delete(NoteHelper.NOTES_TABLE,
                    NoteHelper.KEY_ID + "=" + tag, null); //delete the item
            notesAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String note_title, note_details, note_date;

        if (requestCode == NOTES_UPDATE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    note_title = data.getStringExtra(DetailsActivity.EXTRA_TITLE_REPLY);
                    note_details = data.getStringExtra(DetailsActivity.EXTRA_DETAILS_REPLY);
                    note_date = data.getStringExtra(DetailsActivity.EXTRA_DATE_REPLY);

                    if (!(TextUtils.isEmpty(note_title) && TextUtils.isEmpty(note_details))) {
                        int id = data.getIntExtra(DetailsActivity.NOTES_ID, -99);
                        int index = data.getIntExtra(DetailsActivity.WORD_EDIT, -99);
                        Note note = mDB.query(index);
                        if (id == NOTES_ADD) {
                            if (TextUtils.isEmpty(note_title)) {
                                note_title = "";
                            }//if the title is empty
                            if (TextUtils.isEmpty(note_details)) {
                                note_details = ""; //if the details is empty
                            }
                            int rowUpdated = mDB.update(note.getId(), note_title, note_details, note_date);
                            System.out.println("UPDATE: No of Rows updated = "+ rowUpdated);
                        }

                        notesAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(mRecyclerView, "You are trying to update an empty note." +
                                " Data not SAVED", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }

        if (requestCode == NOTES_EDIT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    //get the data from details activity
                    note_title = data.getStringExtra(DetailsActivity.EXTRA_TITLE_REPLY);
                    note_details = data.getStringExtra(DetailsActivity.EXTRA_DETAILS_REPLY);
                    note_date = data.getStringExtra(DetailsActivity.EXTRA_DATE_REPLY);

                    //if the title or details is not empty, then get the ID from DetailsActivity
                    //then check if its is the same as the intent code that started the activity
                    if (!(TextUtils.isEmpty(note_title) && TextUtils.isEmpty(note_details))) {
                        int id = data.getIntExtra(DetailsActivity.NOTES_ID, -99);
                        if (id == NOTES_ADD) {

                            if (TextUtils.isEmpty(note_title)) {
                                note_title = "";
                            }//if the title is empty
                            if (TextUtils.isEmpty(note_details)) {
                                note_details = ""; //if the details is empty
                            }
                            int inserted = (int) mDB.insert(note_title, note_details, note_date); //insert into DB
                            System.out.println("UPDATE: Inserted row no = "+ inserted);
                            NavigationView navigationView = findViewById(R.id.nav_view);
                            navigationView.getMenu().findItem(R.id.nav_all_notes).setTitle("All Notes \t\t\t\t\t"
                                    + notesAdapter.getItemCount());
                        }
                        //update UI
                        notesAdapter.notifyDataSetChanged();
                    } else {
                        Snackbar.make(mRecyclerView, "You are trying to save an empty note." +
                                " Data not SAVED", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
////                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
////                intent.putExtra("Search Activity", newText);
//                return false;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
//        }else if(id == R.id.action_search){
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_about){
            AlertDialog.Builder alertBox = new AlertDialog.Builder(MainActivity.this);
            alertBox.setTitle("About")
                    .setMessage("Your app that saves up notes that you will like to keep!" +
                            "\n\nYou can delete a note by swiping left." +
                            "\n\nAny feedback? refer to Feedback section.\n\nEnjoy the app. ")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
