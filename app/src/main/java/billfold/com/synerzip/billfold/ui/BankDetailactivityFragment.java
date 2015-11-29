package billfold.com.synerzip.billfold.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;

/**
 * A placeholder fragment containing a simple view.
 */
public class BankDetailactivityFragment extends Fragment implements View.OnClickListener {

    private EditText mEtBankName;
    private EditText mEtAccountNo;
    private EditText mEtRoutingNo;
    private EditText mEtBirthDate;

    private EditText mEtLine;
    private EditText mEtPostalCode;
    private EditText mEtCity;
    private EditText mEtState;
    private EditText mEtCountry;

    private Button mBtSave;

    private int mYear;
    private int mMonth;
    private int mDay;

    private ProgressDialog mProgressDialog;


    public BankDetailactivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bank_detailactivity, container, false);

        mEtBankName = (EditText) view.findViewById(R.id.bank_name_editText);
        mEtAccountNo = (EditText) view.findViewById(R.id.account_editText);
        mEtRoutingNo = (EditText) view.findViewById(R.id.rounting_editText);
        mEtBirthDate = (EditText) view.findViewById(R.id.birth_editText);
        mEtLine = (EditText) view.findViewById(R.id.line_editText);
        mEtPostalCode = (EditText) view.findViewById(R.id.postalcode_editText);
        mEtCity = (EditText) view.findViewById(R.id.city_editText);
        mEtState = (EditText) view.findViewById(R.id.state_editText);
        mEtCountry = (EditText) view.findViewById(R.id.country_editText);
        mBtSave = (Button) view.findViewById(R.id.save_Button);

        mBtSave.setOnClickListener(this);
        addFocusChangeListener(mEtBirthDate);
        return view;
    }

    private void addFocusChangeListener(final EditText mEtBirthDate) {

        mEtBirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    toggle(getActivity());
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);

                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), myDateListener, year, month, day);
                    datePickerDialog.show();
                }

            }
        });
    }

    public static void toggle(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0); // hide
        } else {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY); // show
        }
    }//end method

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // arg1 = year
            // arg2 = month
            // arg3 = day

            mYear = arg1;
            mMonth = arg2;
            mDay = arg3;
            mEtBirthDate.setText(mMonth + "/" + mDay + "/" + mYear);

        }
    };

    @Override
    public void onClick(View v) {
        if (v == mBtSave) {
            String bankName = mEtBankName.getText().toString().trim();
            if (null == bankName || bankName.isEmpty()) {
                mEtBankName.setError("Bank Name is empty.");
                return;
            }

            String accountNo = mEtAccountNo.getText().toString().trim();
            if (null == accountNo || accountNo.isEmpty()) {
                mEtAccountNo.setError("Account Number is empty.");
                return;
            }

            String routingNo = mEtRoutingNo.getText().toString().trim();
            if (null == routingNo || routingNo.isEmpty()) {
                mEtRoutingNo.setError("Routing number is empty.");
                return;
            }

            String birthDate = mEtBirthDate.getText().toString().trim();
            if (null == birthDate || birthDate.isEmpty()) {
                mEtBirthDate.setError("Birth Date is empty.");
                return;
            }

            String line = mEtLine.getText().toString().trim();
            if (null == line || line.isEmpty()) {
                mEtLine.setError("Address line is empty.");
                return;
            }

            String postalCode = mEtPostalCode.getText().toString().trim();
            if (null == postalCode || postalCode.isEmpty()) {
                mEtPostalCode.setError("Postal code is empty.");
                return;
            }


            String city = mEtCity.getText().toString().trim();
            if (null == city || city.isEmpty()) {
                mEtCity.setError("City is empty.");
                return;
            }

            String state = mEtState.getText().toString().trim();
            if (null == state || state.isEmpty()) {
                mEtState.setError("State is empty.");
                return;
            }

            String country = mEtCountry.getText().toString().trim();
            if (null == country || country.isEmpty()) {
                mEtCountry.setError("Country is empty.");
                return;
            }
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int id = preferences.getInt(AppConstant.USER_ID, -1);

            JSONObject jsonObject = new JSONObject();
            JSONObject addressObj = new JSONObject();
            JSONObject birthDateObj = new JSONObject();
            try {
                addressObj.put("city", city);
                addressObj.put("country", country);
                addressObj.put("line1", line);
                addressObj.put("postalCode", postalCode);
                addressObj.put("state", state);
                jsonObject.put("address", addressObj);
                jsonObject.put("accountType", "sole_prop");
                jsonObject.put("tosAcceptanceIp", "121.247.75.143");
                jsonObject.put("bankName", bankName);
                jsonObject.put("accountNumber", "000123456789");
                birthDateObj.put("day", mDay);
                birthDateObj.put("month", mMonth);
                birthDateObj.put("year", mYear);
                jsonObject.put("birthDate", birthDateObj);
                jsonObject.put("routingNumber", "110000000");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("Json Obj ::", jsonObject.toString());

            RequestTask requestTask = new RequestTask();
            requestTask.execute(new String[]{String.format(AppConstant.BANK_ACCOUNT, id), jsonObject.toString()});


        }
    }


    class RequestTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            showProgressDialog("Saving Bank Detail");
        }

        @Override
        protected String doInBackground(String... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            int date = connectionManager.postRequestWithResponseCode(params[0], params[1]);
            return String.valueOf(date);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.e("Response ::", "" + s);
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (s.equalsIgnoreCase("200")) {
                getActivity().finish();
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
