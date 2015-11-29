package billfold.com.synerzip.billfold.ui;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import billfold.com.synerzip.billfold.R;
import billfold.com.synerzip.billfold.connection.ConnectionManager;
import billfold.com.synerzip.billfold.constant.AppConstant;
import billfold.com.synerzip.billfold.utils.Utils;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PayFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Button mBtFetchInvoice;
    private Button mBtHistory;
    private Button mBtAddCard;
    private TextView mTVBVC;
    private ProgressDialog mProgressDialog;
    public static final int MY_SCAN_REQUEST_CODE = 100;


    private String cardNumber;
    private String CVC;
    private int expireYear;
    private int expireMonth;


    public static final String PUBLISHABLE_KEY = "pk_test_I2iJjDijZFk42B48uXzM8Gr7";
    public static final String SECRETE_KEY = "sk_test_G1zt3FBefMtxpViN0z7AW7p1";
    public static final String CARD_NUMBER = "4242424242424242";

    private int GET_BVC = 11;
    private int ADD_CARD = 12;


    public PayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PayFragment newInstance(String param1, String param2) {
        PayFragment fragment = new PayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pay, container, false);
        mBtAddCard = (Button) view.findViewById(R.id.Add_Button);
        mBtFetchInvoice = (Button) view.findViewById(R.id.fetch_Button);
        mBtHistory = (Button) view.findViewById(R.id.history_Button);

        mBtAddCard.setOnClickListener(this);
        mBtFetchInvoice.setOnClickListener(this);
        mBtHistory.setOnClickListener(this);
        mTVBVC = (TextView) view.findViewById(R.id.bvc_TextView);

        getBVCCode();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        if (view == mBtAddCard) {
            Intent scanIntent = new Intent(getActivity(), CardIOActivity.class);
            // customize these values to suit your needs.
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
            scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, true); // default: false
            // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
            startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
        } else if (view == mBtFetchInvoice) {
            Intent getInvoiceActivity = new Intent(getActivity(), GetInvoiceActivity.class);
            startActivity(getInvoiceActivity);
        } else if (view == mBtHistory) {
            Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
            historyIntent.putExtra("from", "pay");
            startActivity(historyIntent);
        }

    }

    class RequestTask extends AsyncTask<String, Void, String> {

        int requestType;

        public RequestTask(int request) {
            requestType = request;
        }

        @Override
        protected void onPreExecute() {
            showProgressDialog("Loading...");
        }

        @Override
        protected String doInBackground(String... params) {

            ConnectionManager connectionManager = new ConnectionManager();
            String result = null;
            int responseCode;
            if (requestType == GET_BVC) {
                result = connectionManager.postRequest(params[0], "");
            } else {
                responseCode = connectionManager.postRequestWithResponseCode(params[0], params[1]);
                result = String.valueOf(responseCode);
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (requestType == GET_BVC) {
                mTVBVC.setText(result);
            } else {
                if (result.equalsIgnoreCase("200")) {
                    Toast.makeText(getActivity(), "Card Added Successfully", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getBVCCode() {

        if (Utils.isNetworkAvailable(getActivity())) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            int id = preferences.getInt(AppConstant.USER_ID, -1);
            if (id != -1) {
                String url = String.format(AppConstant.BVC_URL, id);
                RequestTask requestTask = new RequestTask(GET_BVC);
                requestTask.execute(new String[]{url});
            }
        } else {
            Utils.showToast(getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "card Number Detail :" + scanResult.cardNumber + "\n";

                cardNumber = scanResult.cardNumber;
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    expireYear = scanResult.expiryYear;
                    expireMonth = scanResult.expiryMonth;
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n" + "CVV Detail :::" + scanResult.cvv + "\n";
                    CVC = scanResult.cvv;
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }

              /*  tvStripeToken.setVisibility(View.VISIBLE);
                btStripeCall.setVisibility(View.VISIBLE);*/

            } else {
                resultDisplayStr = "Scan was canceled.";
            }

            if (null != cardNumber && null != CVC) {
                saveCreditCard();
            }

//           / tvText.setText(resultDisplayStr);

            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultStr);
        }
        // else handle other activity results
    }

    /**
     * open stripe call to validate card and get the token
     */

    public void saveCreditCard() {

        Card card = new Card(
                CARD_NUMBER,
                expireMonth,
                expireYear,
                CVC);

        boolean validation = card.validateCard();
        if (validation) {
            showProgressDialog("Loading...");
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            int id = preferences.getInt(AppConstant.USER_ID, -1);
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("tokenId", token.getId());
                                jsonObject.put("lastFourDigits", cardNumber.substring(cardNumber.length() - 4, cardNumber.length()));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            RequestTask requestTask = new RequestTask(ADD_CARD);
                            requestTask.execute(new String[]{String.format(AppConstant.ADD_CARD_TOKEN, id), jsonObject.toString()});
                        }

                        public void onError(Exception error) {
                            Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            if (null != mProgressDialog && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        }
                    });
        } else if (!card.validateNumber()) {
            Toast.makeText(getActivity(), "The card number that you entered is invalid", Toast.LENGTH_SHORT).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(getActivity(), "The expiration date that you entered is invalid", Toast.LENGTH_SHORT).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(getActivity(), "The CVC code that you entered is invalid", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "The card details that you entered are invalid", Toast.LENGTH_SHORT).show();
        }
    }


}
