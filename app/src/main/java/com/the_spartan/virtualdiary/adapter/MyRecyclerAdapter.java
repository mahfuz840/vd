package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.util.Utils;

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

            Typeface myFont = Utils.initializeFonts(mContext);

            if (myFont != null) {
                dateView.setTypeface(myFont);
                monthView.setTypeface(myFont);
                titleView.setTypeface(myFont);
            }

            Animation alphaAnim = AnimationUtils.loadAnimation(mContext, R.anim.scale_in);
            itemView.startAnimation(alphaAnim);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(itemView, "Do your want to delete this note?", "Yes", "No", getAdapterPosition());
                return true;
            }
        });

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
        public void showDialog(View view, String msg, String posText, String negText, int pos) {
            ViewGroup viewGroup = view.findViewById(android.R.id.content);
            final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog, viewGroup, false);
            dialogView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));


            //Now we need an AlertDialog.Builder object
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

            //setting the view of the builder to our custom view that we already inflated
            builder.setView(dialogView);

            //finally creating the alert dialog and displaying it
            final AlertDialog alertDialog = builder.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextView msgView = dialogView.findViewById(R.id.text_dialog);
            msgView.setText(msg);

            TextView posBtn = dialogView.findViewById(R.id.pos_btn);
            posBtn.setText(posText);

            TextView negBtn = dialogView.findViewById(R.id.neg_btn);
            negBtn.setText(negText);

            posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.getContentResolver().delete(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(mNotes.get(getAdapterPosition()).getID())),
                            null,
                            null);

                    mNotes.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    notifyItemRemoved(getAdapterPosition());
                    dialogView.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_out));
                    alertDialog.dismiss();
                }
            });
            negBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            alertDialog.show();

        }


    }


    public void setOnItemClickListener(ClickListener clickListener){
        MyRecyclerAdapter.clickListener = clickListener;
    }


    public interface ClickListener{
        void onItemClick(int position, View v);
    }


}
