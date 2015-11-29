package billfold.com.synerzip.billfold.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;

/**
 * A placeholder fragment containing a simple view.
 */
public class GetInvoiceFragment extends Fragment implements View.OnClickListener {


    private EditText mETPhoneNumber;
    private EditText mETAmount;
    private EditText mEtDescription;
    private Button mBtAccept;
    private Button mBtReject;
    private ProgressDialog mProgressDialog;

    private int FETCH_INVOICE = 111;
    private int ACCEPT = 112;
    private int REJECT = 113;
    private int FETCH_CARD_LIST = 114;

    private int fetchInvoiceId = -1;


    public GetInvoiceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_invoice, container, false);

        mETPhoneNumber = (EditText) view.findViewById(R.id.phone_editText);
        mETPhoneNumber.setKeyListener(null);
        mETAmount = (EditText) view.findViewById(R.id.amount_editText);
        mETAmount.setKeyListener(null);
        mEtDescription = (EditText) view.findViewById(R.id.description_editText);
        mEtDescription.setKeyListener(null);

        mBtAccept = (Button) view.findViewById(R.id.accept_Button);
        mBtReject = (Button) view.findViewById(R.id.rejet_Button);
        mBtAccept.setOnClickListener(this);
        mBtReject.setOnClickListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int id = preferences.getInt(AppConstant.USER_ID, -1);
        RequestTask requestTask = new RequestTask(FETCH_INVOICE);
        requestTask.execute(new String[]{String.format(AppConstant.FETCH_INVOICE, id)});

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {

        if (v == mBtAccept) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int id = preferences.getInt(AppConstant.USER_ID, -1);
            RequestTask requestTask = new RequestTask(FETCH_CARD_LIST);
            requestTask.execute(new String[]{String.format(AppConstant.FETCH_CARD, id)});


        } else if (v == mBtReject) {

            if (fetchInvoiceId != -1) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("paymentAction", "REJECTED");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                int id = preferences.getInt(AppConstant.USER_ID, -1);
                RequestTask requestTask = new RequestTask(REJECT);
                requestTask.execute(new String[]{String.format(AppConstant.FETCH_INVOICE, id, fetchInvoiceId), jsonObject.toString()});
            } else {

            }


        }
    }


    class RequestTask extends AsyncTask<String, Void, String> {

        int requestType;

        public RequestTask(int request) {
            requestType = request;
        }

        @Override
        protected void onPreExecute() {
            if (requestType == FETCH_INVOICE) {
                showProgressDialog("Fetching Invoice...");
            } else if (requestType == REJECT) {
                showProgressDialog("Rejecting Invoice...");
            } else {
                showProgressDialog("Accepting Invoice...");
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            int responseCode;
            ConnectionManager connectionManager = new ConnectionManager();
            try {
                if (requestType == FETCH_INVOICE) {
                    result = connectionManager.getRequest(params[0]);
                } else if (requestType == REJECT) {
                    connectionManager.postRequestWithResponseCode(params[0], params[1]);
                } else if (requestType == FETCH_CARD_LIST) {
                    result = connectionManager.getRequest(params[0]);
                } else {
                    responseCode = connectionManager.putRequestWithResponseCode(params[0], params[1]);
                    result = String.valueOf(responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }


        @Override
        protected void onPostExecute(String s) {
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (requestType == FETCH_INVOICE) {

                try {
                    if (null != s && !s.isEmpty()) {
                        JSONObject jsonObject = new JSONObject(s);
                        mETPhoneNumber.setText(jsonObject.getString("receiverPhoneNumber"));
                        mEtDescription.setText(jsonObject.getString("description"));
                        mETAmount.setText(jsonObject.getString("amount"));
                        fetchInvoiceId = jsonObject.getInt("id");
                    } else {
                        Toast.makeText(getActivity(), "No Invoice", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (requestType == REJECT) {
                Toast.makeText(getActivity(), "Invoice declined successfully.", Toast.LENGTH_LONG).show();
                getActivity().finish();
            } else if (requestType == FETCH_CARD_LIST) {

                try {
                    JSONArray jsonArray = new JSONArray(s);
                    ArrayList<CardDetail> cardDetailsList = new ArrayList<CardDetail>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        CardDetail cardDetail = new CardDetail();
                        cardDetail.id = jsonObject.getInt("id");
                        cardDetail.lastFourDigits = jsonObject.getString("lastFourDigits");
                        cardDetail.cardId = jsonObject.getString("cardId");
                        cardDetail.tokenId = jsonObject.getString("tokenId");
                        cardDetail.defaultcard = jsonObject.getBoolean("defaultCard");
                        cardDetailsList.add(cardDetail);
                    }

                    if (cardDetailsList.size() > 0) {
                        showCreditCardDialog(cardDetailsList);
                    } else {
                        Toast.makeText(getActivity(), "No card added", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (requestType == ACCEPT) {

                if (s.equalsIgnoreCase("200")) {
                    Toast.makeText(getActivity(), "Invoice accepted successfully.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_LONG).show();
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


    private void showCreditCardDialog(final ArrayList<CardDetail> list) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setTitle("Select Card");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < list.size(); i++) {
            arrayAdapter.add("**** **** **** ****" + list.get(i).lastFourDigits);
        }
        builderSingle.setNegativeButton(
                "cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("cardId", list.get(which).cardId);
                            jsonObject.put("paymentAction", "ACCEPTED");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        int id = preferences.getInt(AppConstant.USER_ID, -1);
                        RequestTask requestTask = new RequestTask(ACCEPT);
                        requestTask.execute(new String[]{String.format(AppConstant.ACCEPT_REJECT_INVOICE, id, fetchInvoiceId), jsonObject.toString()});

                    }
                });
        builderSingle.show();
    }

    class CardDetail {
        int id;
        String lastFourDigits;
        String cardId;
        String tokenId;
        boolean defaultcard;
    }


}
