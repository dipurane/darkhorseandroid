package billfold.com.synerzip.billfold.constant;

/**
 * Created by synerzip on 28/11/15.
 */
public class AppConstant {
    public static final String MOB_REG_URL = "http://54.85.74.160:8000/apis/usermanagement/authentication";
    public static final String MOB_VER_URL = "http://54.85.74.160:8000/apis/usermanagement/verification";
    public static final String USER_VER_URL = "http://54.85.74.160:8000/apis/usermanagement/userprofile";
    public static final String BVC_URL = "http://54.85.74.160:8000/apis/payer/%s/pvc";
    public static final String USER_ID = "userId";
    public static final String RAISE_URI = "http://54.85.74.160:8000/apis/receiver/%s/transaction";
    public static final String BANK_ACCOUNT = "http://54.85.74.160:8000/apis/paymentgateway/%s/bankaccount";
    public static final String FETCH_INVOICE = "http://54.85.74.160:8000/apis/payer/%s/transaction";
    public static final String ACCEPT_REJECT_INVOICE = "http://54.85.74.160:8000/apis/payer/%s/transactions/%s";
    public static final String ADD_CARD_TOKEN = "http://54.85.74.160:8000/apis/paymentgateway/%s/creditcard";
    public static final String FETCH_CARD = "http://54.85.74.160:8000/apis/paymentgateway/%s/creditcard";
    public static final String PAY_TRANSACTION_LIST = "http://54.85.74.160:8000/apis/payer/%s/transactionList";
    public static final String RECEIVER_TRANSACTION_LIST = "http://54.85.74.160:8000/apis/receiver/%s/transactionList";
    public static final String CHECK_INVOICE_STATUS_URL = "http://54.85.74.160:8000/apis/receiver/%s/transaction/%s";


}
