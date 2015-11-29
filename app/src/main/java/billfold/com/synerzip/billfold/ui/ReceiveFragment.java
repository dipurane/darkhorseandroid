package billfold.com.synerzip.billfold.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import billfold.com.synerzip.billfold.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReceiveFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiveFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button mBtRaiseInvoice;
    private Button mBtHistory;
    private Button mBtAddBankDetail;


    public ReceiveFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiveFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReceiveFragment newInstance(String param1, String param2) {
        ReceiveFragment fragment = new ReceiveFragment();
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
        View view = inflater.inflate(R.layout.fragment_receive, container, false);


        mBtRaiseInvoice = (Button) view.findViewById(R.id.raise_Button);
        mBtAddBankDetail = (Button) view.findViewById(R.id.Add_bankDetail_Button);
        mBtHistory = (Button) view.findViewById(R.id.history_Button);

        mBtHistory.setOnClickListener(this);
        mBtAddBankDetail.setOnClickListener(this);
        mBtRaiseInvoice.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view == mBtRaiseInvoice) {
            HomeActivity.isManuallyPaused = true;
            Intent intent = new Intent(getActivity(), RaiseInvoiceActivity.class);
            startActivity(intent);

        } else if (view == mBtAddBankDetail) {
            HomeActivity.isManuallyPaused = true;
            Intent intent = new Intent(getActivity(), BankDetailactivity.class);
            startActivity(intent);

        } else if (view == mBtHistory) {
            Intent historyIntent = new Intent(getActivity(), HistoryActivity.class);
            historyIntent.putExtra("from", "rec");
            startActivity(historyIntent);
        }

    }
}
