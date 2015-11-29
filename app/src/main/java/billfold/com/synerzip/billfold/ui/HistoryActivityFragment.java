package billfold.com.synerzip.billfold.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.adapter.HistoryListAdapter;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;
import billfold.com.synerzip.billfold.model.HistoryDataSet;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryActivityFragment extends Fragment {

    private ListView mHistoryList;
    private ProgressDialog mProgressDialog;
    private HistoryListAdapter historyListAdapter;

    private String from;
    private ArrayList<HistoryDataSet> historyList = new ArrayList<>();


    public HistoryActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        from = getActivity().getIntent().getStringExtra("from");


        View view = inflater.inflate(R.layout.fragment_history, container, false);

        mHistoryList = (ListView) view.findViewById(R.id.listView);
        boolean isPayFrom;
        if (from.equalsIgnoreCase("pay")) {
            isPayFrom = true;
        } else {
            isPayFrom = false;
        }
        historyListAdapter = new HistoryListAdapter(getActivity(), -1, historyList, isPayFrom);
        mHistoryList.setAdapter(historyListAdapter);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int id = preferences.getInt(AppConstant.USER_ID, -1);
        RequestTask requestTask = new RequestTask();
        String url;
        if (from.equalsIgnoreCase("pay")) {
            url = String.format(AppConstant.PAY_TRANSACTION_LIST, id);
        } else {
            url = String.format(AppConstant.RECEIVER_TRANSACTION_LIST, id);
        }

        requestTask.execute(new String[]{url});

        return view;
    }


    class RequestTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showProgressDialog("Loading History");
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            String result = connectionManager.getRequest(params[0]);

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (null != s && !s.isEmpty()) {
                historyList.clear();
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        HistoryDataSet historyDataSet = new HistoryDataSet();
                        historyDataSet.setDate(jsonObject.getString("createdDateStr"));
                        historyDataSet.setAmount(String.valueOf(jsonObject.getInt("amount")));
                        if (from.equalsIgnoreCase("pay")) {
                            historyDataSet.setPayTo(jsonObject.getString("receiverPhoneNumber"));
                        } else {
                            historyDataSet.setPayTo(jsonObject.getString("payerPhoneNumber"));
                        }
                        historyDataSet.setStatus(jsonObject.getString("status"));
                        historyDataSet.setId(jsonObject.getInt("id"));
                        historyList.add(historyDataSet);
                    }

                    historyListAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void showProgressDialog(String message) {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        mProgressDialog.show();
    }
}
