package billfold.com.synerzip.billfold.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.model.HistoryDataSet;

/**
 * Created by synerzip on 29/11/15.
 */
public class HistoryListAdapter extends ArrayAdapter<HistoryDataSet> {

    private List<HistoryDataSet> historyList = null;
    private Context context;
    private LayoutInflater layoutInflater;
    private boolean isPayFor;

    public HistoryListAdapter(Context context, int resource, List<HistoryDataSet> objects, boolean isPayFor) {
        super(context, resource, objects);
        this.historyList = objects;
        this.context = context;
        this.isPayFor = isPayFor;
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ((null != historyList) ? historyList.size() : 0);
    }

    @Override
    public HistoryDataSet getItem(int position) {
        return ((null != historyList) ? historyList.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.history_list_item, null);
            holder = new ViewHolder();
            holder.date = (TextView) convertView.findViewById(R.id.date_label_TextView);
            holder.amount = (TextView) convertView.findViewById(R.id.amount_value_TextView);
            holder.status = (TextView) convertView.findViewById(R.id.status_value_TextView);
            holder.tofrom = (TextView) convertView.findViewById(R.id.to_value_TextView);
            holder.toFromlabel = (TextView) convertView.findViewById(R.id.to_label_TextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isPayFor) {
            holder.toFromlabel.setText("To :");
        } else {
            holder.toFromlabel.setText("From :");
        }

        HistoryDataSet historyDataSet = historyList.get(position);
        holder.date.setText(historyDataSet.getDate());
        holder.status.setText(historyDataSet.getStatus());
        holder.amount.setText(historyDataSet.getAmount());
        holder.tofrom.setText(historyDataSet.getPayTo());
        return convertView;
    }

    static class ViewHolder {
        private TextView date;
        private TextView tofrom;
        private TextView amount;
        private TextView status;
        private TextView toFromlabel;

    }

}
