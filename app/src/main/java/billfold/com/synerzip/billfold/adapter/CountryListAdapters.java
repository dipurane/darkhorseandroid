package billfold.com.synerzip.billfold.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import billfold.com.synerzip.billfold.R;

/**
 * Created by synerzip on 28/11/15.
 */
public class CountryListAdapters extends ArrayAdapter<String> {

    public String[] mCountryName;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ArrayList<String> suggestions;

    public CountryListAdapters(Context context, int resource, String[] contryName) {
        super(context, resource);

        this.mCountryName = contryName;
        Log.e("Contry name Lenth ::", "" + contryName.length);
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ((null != mCountryName) ? mCountryName.length : 0);
    }

    @Override
    public String getItem(int position) {
        return ((null != mCountryName) ? mCountryName[position] : null);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      /*  ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.country_list_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.country_name_TextView);
            holder.imageView = (ImageView) convertView.findViewById(R.id.country_ImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(mCountryName[position]);*/
        return getCustomView(position, convertView, parent);
    }


    public View getCustomView(int position, View convertView, ViewGroup parent) {

        /********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
        View row = mLayoutInflater.inflate(R.layout.country_list_item, parent, false);

        TextView label = (TextView) row.findViewById(R.id.country_name_TextView);
        ImageView sub = (ImageView) row.findViewById(R.id.country_ImageView);
        ImageView companyLogo = (ImageView) row.findViewById(R.id.image);

        if (position == 0) {

            // Default selected Spinner item
            label.setText("Please select company");
            //sub.setText("");
        } else {
            // Set values for spinner each row
            label.setText(mCountryName[position]);

        }

        return row;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

   /*
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = resultValue.toString();
            return str;
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (String countryName : mCountryName) {
                    if (countryName.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(countryName);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filteredList = (ArrayList<String>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (String c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
*/

}
