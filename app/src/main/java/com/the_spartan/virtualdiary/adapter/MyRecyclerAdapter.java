package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.graphics.Typeface;
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

import androidx.recyclerview.widget.RecyclerView;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.data.NoteProvider;
import com.the_spartan.virtualdiary.model.Note;
import com.the_spartan.virtualdiary.util.FontUtil;
import com.the_spartan.virtualdiary.util.StringUtil;
import com.the_spartan.virtualdiary.view.CustomDialog;

import java.util.ArrayList;

/**
 * Created by the_spartan on 3/11/18.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

    private static ClickListener clickListener;
    Context mContext;
    private ArrayList<Note> mNotes;

    public MyRecyclerAdapter(Context context, ArrayList<Note> notes) {
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
        String title = mNotes.get(position).getTitle();
        if (!title.equals("")) {
            title = title + "...";
            holder.titleView.setText(title);
        } else {
            String content = mNotes.get(position).getDescription();
            if (content.equals("")) {
                holder.titleView.setText("(no title)");
            } else {
                content = content + "...";
                holder.titleView.setText(content);
            }
        }


        holder.dateView.setText(dateOnly[0]);
        int month = mNotes.get(position).getMonth();
        String monthString = StringUtil.getMonthNameFromInt(month);
        holder.monthView.setText(monthString);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        params.height = (int) (height / 5.5);
        params.width = (int) (width / 3.35);
        params.topMargin = 10;
        params.bottomMargin = 10;

        holder.itemView.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        if (mNotes == null) {
            return 0;
        } else {
            return mNotes.size();
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        MyRecyclerAdapter.clickListener = clickListener;
    }


    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            Typeface myFont = FontUtil.initializeFonts(mContext);

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
                    showDialog(itemView);
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }

        public void showDialog(View itemView) {
            ViewGroup viewGroup = itemView.findViewById(android.R.id.content);
            final CustomDialog customDialog = new CustomDialog(mContext,
                    viewGroup,
                    R.layout.dialog,
                    R.string.confirm_delete,
                    R.string.dialog_msg_note_single_delete,
                    R.string.dialog_btn_yes,
                    R.string.dialog_btn_cancel);

            customDialog.posBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.getContentResolver().delete(Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(mNotes.get(getAdapterPosition()).getID())),
                            null,
                            null);

                    mNotes.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    customDialog.dismiss();
                }
            });

            customDialog.show();
        }
    }
}
