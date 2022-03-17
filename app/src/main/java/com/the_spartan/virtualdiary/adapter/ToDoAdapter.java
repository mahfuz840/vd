package com.the_spartan.virtualdiary.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.the_spartan.virtualdiary.R;
import com.the_spartan.virtualdiary.model.ToDo;
import com.the_spartan.virtualdiary.service.ToDoService;
import com.the_spartan.virtualdiary.util.FontUtil;
import com.the_spartan.virtualdiary.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ToDoAdapter extends ArrayAdapter<ToDo> implements Filterable {

    private ArrayList<ToDo> filteredTodos;
    private ArrayList<ToDo> originalTodos;
    private Context context;

    private ToDoService toDoService;

    public ToDoAdapter(Context context, ArrayList<ToDo> todos) {
        super(context, 0, todos);
        this.originalTodos = todos;
        this.filteredTodos = todos;
        this.context = context;

        toDoService = new ToDoService();
    }

    @Override
    public int getCount() {
        return filteredTodos.size();
    }

    @Override
    public ToDo getItem(int position) {
        return filteredTodos.get(position);
    }

    public ArrayList<ToDo> getItems() {
        return filteredTodos;
    }

    public ArrayList<ToDo> getOriginalTodos() {
        return originalTodos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.todo_list_item, null);
        }

        TextView tvName = convertView.findViewById(R.id.tv_task_name);
        TextView tvPriority = convertView.findViewById(R.id.tv_priority);
        TextView dueDate;
        TextView tvTime;

        Typeface myFont = FontUtil.initializeFonts(context);

        ToDo item = filteredTodos.get(position);

        if (item != null) {
            CheckBox cb = convertView.findViewById(R.id.cb_item_check);
            cb.setTag(position);
            cb.setChecked(item.isDone());

            cb.setOnCheckedChangeListener((button, isChecked) -> {
                int pos = (int) button.getTag();
                ToDo tappedTodo = getItem(pos);
                tappedTodo.setDone(isChecked);

                toDoService.saveOrUpdate(tappedTodo);
                originalTodos.set(pos, tappedTodo);

                notifyDataSetChanged();
            });

            tvName.setText(item.getSubject());

            if (item.isDone()) {
                tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvName.setPaintFlags(0);
            }

            tvPriority.setText(item.getPriority().getDisplayName());

            dueDate = convertView.findViewById(R.id.tv_date);
            tvTime = convertView.findViewById(R.id.tv_todo_time);
            if (!TextUtils.isEmpty(item.getDueDate())) {
                dueDate.setText(item.getDueDate());
            } else if (!TextUtils.isEmpty(item.getTime())) {
                dueDate.setText(TimeUtil.getTwelveHourFormattedTime(item.getTime()));
                tvTime.setVisibility(View.GONE);
                tvTime = null;
            } else {
                dueDate.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) tvName.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                tvName.setLayoutParams(layoutParams);
            }

            if (!TextUtils.isEmpty(item.getTime()) && tvTime != null) {
                tvTime.setText(TimeUtil.getTwelveHourFormattedTime(item.getTime()));
            }

            if (myFont != null) {
                tvName.setTypeface(myFont);
                tvPriority.setTypeface(myFont);
                dueDate.setTypeface(myFont);
                if (tvTime != null) {
                    tvTime.setTypeface(myFont);
                }
            }

            int color = FontUtil.initializeColor(context);

            if (color != 0) {
                tvName.setTextColor(color);
            }

            String fontSize = FontUtil.initializeFontSize(context);

            if (fontSize != null) {
                tvName.setTextSize(Float.parseFloat(fontSize));
                tvPriority.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize) - 3)));
                dueDate.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize) - 3)));
                tvTime.setTextSize(Float.parseFloat(String.valueOf(Integer.parseInt(fontSize) - 3)));
            }
        }

        return convertView;
    }

    public void deleteCompletedTodos() {
        List<ToDo> todosToDelete = new ArrayList<>();
        for (ToDo todo : originalTodos) {
            if (todo.isDone()) {
                todosToDelete.add(todo);
            }
        }

        toDoService.delete(todosToDelete);

        //remove after testing
        for (ToDo todo : todosToDelete) {
            if (todo.isDone()) {
                originalTodos.remove(todo);
                filteredTodos.remove(todo);
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new ToDoFilter();
    }

    private class ToDoFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = originalTodos;
                results.count = originalTodos.size();

                return results;
            }

            String queryString = constraint.toString().toLowerCase().trim();
            List<ToDo> nList = new ArrayList<>();
            String filterableString;

            for (ToDo item : originalTodos) {
                filterableString = item.getSubject().toLowerCase();
                if (filterableString.contains(queryString)) {
                    nList.add(item);
                }
            }

            results.values = nList;
            results.count = nList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            filteredTodos = (ArrayList<ToDo>) filterResults.values;
            notifyDataSetChanged();
//            setListViewHeightBasedOnChildren(listView);
        }
    }
}
