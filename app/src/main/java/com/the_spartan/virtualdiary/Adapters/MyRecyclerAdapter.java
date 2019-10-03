package com.the_spartan.virtualdiary.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.the_spartan.virtualdiary.activities.MainActivity;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.objects_and_others.Note;
import com.the_spartan.virtualdiary.R;

import java.util.ArrayList;

/**
 * Created by the_spartan on 3/11/18.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    String mDate;
    Context mContext;

    private static ClickListener clickListener;
    private ArrayList<Note> mNotes;
    public MyRecyclerAdapter(Context context, ArrayList<Note> notes){
        mContext = context;
        mNotes = notes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.note_recycler_main_activity, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String dateWithTime = mNotes.get(position).getDateTimeFormatted(mContext);
        String dateOnly[] = dateWithTime.split("/");
        String title = mNotes.get(position).getMtitle();
        if(!title.equals("")){
            title = title + "...";
            holder.titleView.setText(title);
        } else {
            String content = mNotes.get(position).getmContent();
            if(content.equals("")){
                holder.titleView.setText("(no title)");
            } else {
                content = content + "...";
                holder.titleView.setText(content);
            }
        }


        holder.dateView.setText(dateOnly[0]);
        String monthString;
        int month = mNotes.get(position).getmMonth();
        switch (month){
            case 1:
                monthString = "Jan";
                break;
            case 2:
                monthString = "Feb";
                break;
            case 3:
                monthString = "Mar";
                break;
            case 4:
                monthString = "Apr";
                break;
            case 5:
                monthString = "May";
                break;
            case 6:
                monthString = "Jun";
                break;
            case 7:
                monthString = "Jul";
                break;
            case 8:
                monthString = "Aug";
                break;
            case 9:
                monthString = "Sep";
                break;
            case 10:
                monthString = "Oct";
                break;
            case 11:
                monthString = "Nov";
                break;
                case 12:
                monthString = "Dec";
                break;
                default:
                    monthString = "Unknown";
        }
        holder.monthView.setText(monthString);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        params.height = (int)(height/5.5);
        params.width = (int)(width/3.35);
        params.topMargin = 10;
        params.bottomMargin = 10;

        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        if (mNotes == null)
            return 0;
        else
            return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dateView;
        TextView monthView;
        TextView titleView;
        View root;

        public ViewHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setLongClickable(true);
            dateView = itemView.findViewById(R.id.note_date);
            monthView = itemView.findViewById(R.id.note_month);
            titleView = itemView.findViewById(R.id.note_title);
            root = itemView.findViewById(R.id.note_layout_root);

            Animation alphaAnim = AnimationUtils.loadAnimation(mContext, R.anim.alpha);
            itemView.startAnimation(alphaAnim);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                int pos = getAdapterPosition();
                builder.setTitle("Warning!");
                builder.setMessage("Do your really want to delete this note?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        int pos = getAdapterPosition();
                        mContext.getContentResolver().delete(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(mNotes.get(getAdapterPosition()).getID())),
                                null,
                                null);

                        mNotes.remove(getAdapterPosition());
                        notifyDataSetChanged();
                        notifyItemRemoved(getAdapterPosition());
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                android.app.AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.show();




                return true;
            }
        });

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }




    }


    public void setOnItemClickListener(ClickListener clickListener){
        MyRecyclerAdapter.clickListener = clickListener;
    }


    public interface ClickListener{
        void onItemClick(int position, View v);
    }
}
