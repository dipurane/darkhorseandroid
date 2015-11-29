package billfold.com.synerzip.billfold.ui;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;

/**
 * A placeholder fragment containing a simple view.
 */
public class RaiseInvoiceActivityFragment extends Fragment implements View.OnClickListener {

    private EditText mETPhoneNumber;
    private EditText mETBVC;
    private EditText mETAmount;
    private EditText mEtDescription;
    private Button mBtRaise;

    private ProgressDialog mProgressDialog;
    private int transactionId = -1;

    private final int RAISE_INVOICE = 134;
    private final int CHECK_RAISED_INVOICE_STATUS = 135;


    public RaiseInvoiceActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_raise_invoice, container, false);

        mETPhoneNumber = (EditText) view.findViewById(R.id.phone_editText);
        mETAmount = (EditText) view.findViewById(R.id.amount_editText);
        mETBVC = (EditText) view.findViewById(R.id.bvc_editText);
        mEtDescription = (EditText) view.findViewById(R.id.description_editText);
        mBtRaise = (Button) view.findViewById(R.id.raise_Button);
        mBtRaise.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == mBtRaise) {

            String phoneNumber = mETPhoneNumber.getText().toString().trim();

            if (null == phoneNumber || phoneNumber.isEmpty() || phoneNumber.length() != 10) {
                mETPhoneNumber.setError("Please enter valid phone number.");
                return;
            }
            String bvc = mETBVC.getText().toString().trim();
            if (null == bvc || bvc.isEmpty() || bvc.length() != 3) {
                mETBVC.setError("Please enter valid BVC number.");
                return;
            }

            String amount = mETAmount.getText().toString().trim();
            if (null == amount || amount.isEmpty()) {
                mETAmount.setError("Please enter valid amount.");
                return;
            }

            String description = mEtDescription.getText().toString().trim();
            if (null == description || description.isEmpty()) {
                mEtDescription.setError("Please enter description.");
                return;
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int id = preferences.getInt(AppConstant.USER_ID, -1);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("amount", amount);
                jsonObject.put("payerBVCCode", bvc);
                jsonObject.put("description", description);
                jsonObject.put("userId", id);
                jsonObject.put("payerPhoneNumber", phoneNumber);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            String url = String.format(AppConstant.RAISE_URI, id);
            RequestTask requestTask = new RequestTask(RAISE_INVOICE);
            requestTask.execute(new String[]{url, jsonObject.toString()});


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

    class RequestTask extends AsyncTask<String, Void, Integer> {

        int requestType;

        public RequestTask(int request) {
            requestType = request;
        }


        @Override
        protected void onPreExecute() {
            showProgressDialog("Loading...");
        }

        @Override
        protected Integer doInBackground(String... params) {

            String[] data = null;
            String raisedInvoiceResponse = null;
            int result = 0;
            if (requestType == RAISE_INVOICE) {
                ConnectionManager connectionManager = new ConnectionManager();
                data = connectionManager.postRequestWithResponseAndCode(params[0], params[1]);
                result = Integer.valueOf(data[0]);
                raisedInvoiceResponse = data[1];

                try {
                    JSONObject jsonObject = new JSONObject(raisedInvoiceResponse);
                    transactionId = jsonObject.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (transactionId != -1) {
                    sendStatusCheckRequest(transactionId);
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer s) {

            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (requestType == RAISE_INVOICE) {
                if (s == 200) {
                    Toast.makeText(getActivity(), "Transaction Successfully completed", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                } else if (s == 409) {
                    showDialog("Alert", "Payer already have open transactions.");
                } else if (s == 406) {
                    showDialog("Alert", "User Transaction can not be created due to server error.");
                } else if (s == 412) {
                    showDialog("Alert", "BVC code is in correct");
                } else {
                    showDialog("Alert", "Internal Server Error,Please try again later.");
                }
            }
        }
    }


    private void showDialog(String header, String message) {

        new AlertDialog.Builder(getActivity())
                .setTitle(header)
                .setMessage(message)
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .show();
    }


    private void sendStatusCheckRequest(int transactionId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        int id = preferences.getInt(AppConstant.USER_ID, -1);

        ConnectionManager connectionManager = new ConnectionManager();
        String format = connectionManager.getRequest(String.format(AppConstant.CHECK_INVOICE_STATUS_URL, id, transactionId));

        try {
            JSONObject jsonObject = new JSONObject(format);

            String status = jsonObject.getString("status");
            if (status.equalsIgnoreCase("OPEN")) {
                sendStatusCheckRequest(transactionId);
            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
