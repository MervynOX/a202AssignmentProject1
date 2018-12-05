package com.example.user.a202assignmentproject1;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static com.example.user.a202assignmentproject1.Channel.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase mDatabase;
    private Adapter_List mAdapter;
    private EditText mTask, mComment;
    private TextView mTimer,nameText,commentText;
    private int mAmount = 0;
    private TextView mCountDown;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;

    private long mStartTime;
    private long mTimeLeft;
    private long mEndTime;

    private NotificationManagerCompat notificationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDBHelper dbHelper = new SQLiteDBHelper(this);
        mDatabase = dbHelper.getWritableDatabase();

        notificationManager = NotificationManagerCompat.from(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Adapter_List(this, getAllItems());
        recyclerView.setAdapter(mAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

        mTask = findViewById(R.id.task_name_edit_text);
        mComment = findViewById(R.id.Comment_edit_text);
        mTimer = findViewById(R.id.timer_amount);


        Button buttonIncrease = findViewById(R.id.button_increase);
        Button buttonDecrease = findViewById(R.id.button_decrease);
        Button buttonAdd = findViewById(R.id.button_add);

        buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increase();
            }
        });

        buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrease();
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
    }

    private void increase() {
        mAmount++;
        mTimer.setText(String.valueOf(mAmount));//increase the timer text displayed
    }

    private void decrease() {
        if (mAmount > 0) {
            mAmount--;
            mTimer.setText(String.valueOf(mAmount));// does the same but decrease
        }
    }

    private void addItem() {


        if (mTask.getText().toString().trim().length() == 0) {// checks if task is empty
            Toast.makeText(MainActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }


        String timer= mTimer.getText().toString();
        mStartTime = Long.parseLong(timer) * 60000;//changes the time entered to minutes
        mTimeLeft= mStartTime;
        String name = mTask.getText().toString();
        String comment = mComment.getText().toString();
        ContentValues cv = new ContentValues();
        cv.put(List_Attribute.ListEntry.COLUMN_TASK, name);
        cv.put(List_Attribute.ListEntry.COLUMN_COMMENT, comment);
        cv.put(List_Attribute.ListEntry.COLUMN_TIME, mAmount);

        mDatabase.insert(List_Attribute.ListEntry.TABLE_NAME, null, cv);//adds the items to the sqlite database
        mAdapter.swapCursor(getAllItems());//refreshes the recyclerview

        mTask.getText().clear();
        mComment.getText().clear();//resets the edit text boxes to empty
        if (mAmount!=0) { //only initiates a timer if the time is not 0
            startTimer();
        }
        mAmount=0;
        mTimer.setText(String.valueOf(mAmount));
    }

    private void removeItem(long id) {
        mDatabase.delete(List_Attribute.ListEntry.TABLE_NAME,
                List_Attribute.ListEntry._ID + "=" + id, null);//deletes the item that was being dragged
        mAdapter.swapCursor(getAllItems());
        mTimeLeft=0;
    }

    private Cursor getAllItems() {
        return mDatabase.query(
                List_Attribute.ListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                List_Attribute.ListEntry.COLUMN_TIMESTAMP + " DESC"//arranges newest item at the top
        );
    }

    private void startTimer() {//timer initiated
        mCountDown = findViewById(R.id.textview_amount_time);
        mEndTime = System.currentTimeMillis() + mTimeLeft;

        mCountDownTimer = new CountDownTimer(mTimeLeft, 1000) {//uses a builtin countdown to tick the timer with the refresh rate being every second
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeft = millisUntilFinished;
                CountDownUpdate();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;//when the timer ends the notification will pop up

                nameText = findViewById(R.id.textview_task_name);

                commentText= findViewById(R.id.textview_comment_if);

                String taskname = nameText.getText().toString();
                String commentop = commentText.getText().toString();

                Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(taskname)
                        .setContentText(commentop)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .build();

                notificationManager.notify(1, notification);

            }
        }.start();

        mTimerRunning = true;
    }

    private void CountDownUpdate() {//updates every second
        mCountDown = findViewById(R.id.textview_amount_time);

        if (mCountDown != null) {

            int hours = (int) (mTimeLeft / 1000) / 3600;
            int minutes = (int) ((mTimeLeft / 1000) % 3600) / 60;//formats the time inputted into hours,minutes and seconds
            int seconds = (int) (mTimeLeft / 1000) % 60;

            String TimeFormat;//changes the time into correct format
            if (hours > 0) {
                TimeFormat = String.format(Locale.getDefault(),
                        " %d:%02d:%02d", hours, minutes, seconds);
            } else {
                TimeFormat = String.format(Locale.getDefault(),
                        " %02d:%02d", minutes, seconds);
            }

            mCountDown.setText(TimeFormat);//updates the timer text

        }

    }

    @Override
    protected void onStop() {// continues the timer even when the user has left the app
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("startTimeInMillis", mStartTime);
        editor.putLong("millisLeft", mTimeLeft);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);

        editor.apply();

        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        mStartTime = prefs.getLong("startTimeInMillis", 1);
        mTimeLeft = prefs.getLong("millisLeft", mStartTime);
        mTimerRunning = prefs.getBoolean("timerRunning", false);

        CountDownUpdate();

        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeft = mEndTime - System.currentTimeMillis();

            if (mTimeLeft < 0) {

                mTimeLeft = 0;
                mTimerRunning = false;
                CountDownUpdate();


            } else {
                startTimer();
            }
        }
    }
}
