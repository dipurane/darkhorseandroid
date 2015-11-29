package billfold.com.synerzip.billfold.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginUserActivityFragment extends Fragment implements View.OnClickListener {

    private final String[] mCountries = new String[]{
            "India",
            "Kenya",
            "Germany",
            "SriLanka",
            "Nepal"
    };

    private final String[] mCountriesCode = new String[]{
            "+91",
            "+254",
            "+49",
            "+94",
            "+977"
    };

    private final String[] mCountryCode = new String[]{

    };

    private AutoCompleteTextView mCountrySelectionEditText;
    //Mobile Registration
    private EditText mETMobileNumber;
    private EditText mETCountryCode;
    private Button mBTRegisterUser;
    private Spinner mSpinner;
    private ProgressDialog mProgressDialog;

    //Mobile Verification

    private EditText mETCodeVerification;
    private Button mBTCodeVerify;

    //User registration

    private EditText mETFirstName;
    private EditText mEtLastName;
    private EditText mETEmailId;
    private Button mBTFinalSubmit;


    //Parent Layout
    private RelativeLayout mRLMobileRegLayout;
    private RelativeLayout mRLCodeVericationLayout;
    private RelativeLayout mRLUserDetailLayout;

    private int iRequestCode;
    private String mobileNumber;

    private final int MOB_REG = 111;
    private final int MOB_VER = 112;
    private final int USER_REG = 113;


    public LoginUserActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_user, container, false);
        initMobileRegUI(view);
        initMobileVerificationUI(view);
        initUserDetailLayout(view);

        return view;
    }

    private void initMobileRegUI(View view) {
        mRLMobileRegLayout = (RelativeLayout) view.findViewById(R.id.mobile_reg_ParentLayout);
        mETMobileNumber = (EditText) view.findViewById(R.id.mobile_number_EditText);
        mETCountryCode = (EditText) view.findViewById(R.id.country_code_EditText);
        mETCountryCode.setEnabled(true);
        mETCountryCode.setText(mCountriesCode[0]);
        mBTRegisterUser = (Button) view.findViewById(R.id.register_user_button);
        mBTRegisterUser.setOnClickListener(this);
        mSpinner = (Spinner) view.findViewById(R.id.country_AutoComplete_textView);
        addItemClickListener(mSpinner);
        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mCountries);
        adapter_state
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mSpinner.setDropDownVerticalOffset(60);
        }
        mSpinner.setAdapter(adapter_state);
        mSpinner.requestFocus();
    }

    private void initMobileVerificationUI(View view) {
        mRLCodeVericationLayout = (RelativeLayout) view.findViewById(R.id.mobile_verification_ParentLayout);
        mETCodeVerification = (EditText) view.findViewById(R.id.verification_code_Edittext);
        mBTCodeVerify = (Button) view.findViewById(R.id.submit_button);
        mBTCodeVerify.setOnClickListener(this);
    }

    private void initUserDetailLayout(View view) {

        mRLUserDetailLayout = (RelativeLayout) view.findViewById(R.id.user_detail_ParentLayout);
        mRLUserDetailLayout.setVisibility(View.GONE);
        mRLCodeVericationLayout.setVisibility(View.GONE);
        mETFirstName = (EditText) view.findViewById(R.id.first_name_Edittext);
        mEtLastName = (EditText) view.findViewById(R.id.last_name_Edittext);
        mETEmailId = (EditText) view.findViewById(R.id.email_id_Edittext);
        mBTFinalSubmit = (Button) view.findViewById(R.id.final_regi_button);
        mBTFinalSubmit.setOnClickListener(this);
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("custom-event-name"));
        super.onResume();
    }

    private void addItemClickListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mETCountryCode.setText(mCountriesCode[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        if (view == mBTRegisterUser) {

            mobileNumber = mETMobileNumber.getText().toString();
            if (null == mobileNumber || mobileNumber.isEmpty() || mobileNumber.length() != 10) {
                mETMobileNumber.setError("Please Enter valid Mobile Number");
                return;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mobileNumber", mobileNumber);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestTask requestTask = new RequestTask(MOB_REG);
            requestTask.execute(new String[]{AppConstant.MOB_REG_URL, jsonObject.toString()});

        } else if (view == mBTCodeVerify) {

            String message = mETCodeVerification.getText().toString();

            if (null == message || message.isEmpty() || message.length() != 4) {
                mETCodeVerification.setError("Please Enter valid Code");
                return;
            }

            RequestTask requestTask = new RequestTask(MOB_VER);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mobileNumber", mobileNumber);
                jsonObject.put("verificationCode", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestTask.execute(new String[]{AppConstant.MOB_VER_URL, jsonObject.toString()});

        } else if (view == mBTFinalSubmit) {

            String firstName = mETFirstName.getText().toString();
            String lastName = mEtLastName.getText().toString();
            String emaId = mETEmailId.getText().toString();

            if (null == firstName || firstName.isEmpty()) {
                mETFirstName.setError("First name should not be empty.");
                return;
            }

            if (null == lastName || lastName.isEmpty()) {
                mEtLastName.setError("Last name should not be empty.");
                return;
            }

            if (null == emaId || emaId.isEmpty()) {
                mETEmailId.setError("Last name should not be empty.");
                return;
            }

            String email_regex = "[A-Z]+[a-zA-Z_]+@\b([a-zA-Z]+.){2}\b?.[a-zA-Z]+";

            if (!isValidEmailAddress(emaId.trim())) {
                mETEmailId.setError("Please enter valid email id");
                return;
            }

            RequestTask requestTask = new RequestTask(USER_REG);

            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put("lastName", lastName.trim());
                jsonObject.put("firstName", firstName.trim());
                jsonObject.put("email", emaId.trim());
                jsonObject.put("phoneNumber", mobileNumber);
                jsonObject.put("is_verified", true);
                jsonObject.put("is_verified", true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestTask.execute(new String[]{AppConstant.USER_VER_URL, jsonObject.toString()});


        }

    }


    public boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
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


    class RequestTask extends AsyncTask<String, Void, String> {

        int requestType;

        public RequestTask(int requestType) {
            this.requestType = requestType;
        }

        @Override
        protected void onPreExecute() {
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (requestType == MOB_REG) {
                showProgressDialog(getResources().getString(R.string.dl_msg_registering_mob_no));
            } else if (requestType == MOB_VER) {
                showProgressDialog(getResources().getString(R.string.dl_msg_registering_mob_no));
            } else {
                showProgressDialog(getResources().getString(R.string.registering_user));
            }
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            String result = connectionManager.postRequest(params[0], params[1]);

            return result;
        }


        @Override
        protected void onPostExecute(String result) {

            Log.e("Result ::", "" + result);
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (requestType == MOB_REG) {
                if (result.equalsIgnoreCase("true")) {
                    callForVerification();
                } else {
                    mBTRegisterUser.setText("Resend");
                }

            } else if (requestType == USER_REG) {

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int id = jsonObject.getInt("id");
                    Log.e("id :::", "" + id);
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt(AppConstant.USER_ID, id);
                    editor.apply();

                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else if (requestType == MOB_VER) {
                if (result.equalsIgnoreCase("true")) {
                    showUserDetailScreen();
                } else {

                }
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            message = message.substring(message.length() - 4, message.length());
            mETCodeVerification.setText(message);

            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            RequestTask requestTask = new RequestTask(MOB_VER);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("mobileNumber", mobileNumber);
                jsonObject.put("verificationCode", message);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            requestTask.execute(new String[]{AppConstant.MOB_VER_URL, jsonObject.toString()});
            Log.e("receiver", "Got message: " + message);
        }
    };


    private void callForVerification() {
        mRLCodeVericationLayout.setVisibility(View.VISIBLE);
        mRLUserDetailLayout.setVisibility(View.GONE);
        mRLMobileRegLayout.setVisibility(View.GONE);
        //handler.postDelayed(runnable, 3000);
        showProgressDialog(getResources().getString(R.string.checking_for_sms));

    }

    private void showUserDetailScreen() {
        mRLCodeVericationLayout.setVisibility(View.GONE);
        mRLUserDetailLayout.setVisibility(View.VISIBLE);
        mRLMobileRegLayout.setVisibility(View.GONE);
    }

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }
    };


}
