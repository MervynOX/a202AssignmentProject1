package com.example.user.a202assignmentproject1;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Adapter_List extends RecyclerView.Adapter<Adapter_List.ListViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public Adapter_List(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView nameText;
        public TextView commentText;
        public TextView countText;

        public ListViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.textview_task_name);
            commentText = itemView.findViewById(R.id.textview_comment_if);
            countText = itemView.findViewById(R.id.textview_amount_time);
        }
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.task_list, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String task = mCursor.getString(mCursor.getColumnIndex(List_Attribute .ListEntry.COLUMN_TASK));
        String comment = mCursor.getString(mCursor.getColumnIndex(List_Attribute .ListEntry.COLUMN_COMMENT));
        String amount = mCursor.getString(mCursor.getColumnIndex(List_Attribute .ListEntry.COLUMN_TIME));
        long id = mCursor.getLong(mCursor.getColumnIndex(List_Attribute .ListEntry._ID));

        holder.nameText.setText(task);
        holder.commentText.setText(comment);
        holder.countText.setText(amount);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
}
