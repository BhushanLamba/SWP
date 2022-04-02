package wts.com.newdesigntask.retrofit;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface WebServiceInterface {

    @FormUrlEncoded
    @POST("FetchAePSReport")
    Call<JsonObject> getAepsReport(@Field("UserId") String UserId,
                                   @Field("fromdate") String fromdate,
                                   @Field("todate") String todate);

    @FormUrlEncoded
    @POST("GetUserPayoutTransactionReport")
    Call<JsonObject> getSettlementReport(@Field("userid") String userid,
                                         @Field("fromdate") String fromdate,
                                         @Field("todate") String todate);

    @FormUrlEncoded
    @POST("UploadDocumentForPayout")
    Call<JsonObject> uploadbankDetails(@Field("beneid") String beneid,
                                       @Field("passbook") String passbook,
                                       @Field("panimagge") String panimagge,
                                       @Field("doctype") String doctype);

    @FormUrlEncoded
    @POST("AddBeneficiaryForPayout")
    Call<JsonObject> addBankDetails(@Field("userid") String userid,
                                    @Field("bankid") String bankid,
                                    @Field("account") String account,
                                    @Field("ifsc") String ifsc,
                                    @Field("name") String name,
                                    @Field("accounttype") String accounttype);

    @FormUrlEncoded
    @POST("TransferMoneyByPayout")
    Call<JsonObject> moveToBank(@Field("userid") String userid,
                                @Field("beneid") String beneid,
                                @Field("amount") String amount,
                                @Field("mode") String mode,
                                @Field("accountno") String accountno,
                                @Field("bankname") String bankname,
                                @Field("ifsc") String ifsc,
                                @Field("name") String name,
                                @Field("accounttype") String accounttype);

    @FormUrlEncoded
    @POST("GetAccountListForPayout")
    Call<JsonObject> getAccountDetails(@Field("userid") String userid);

    @FormUrlEncoded
    @POST("DoUserAePSTransactionMaster")
    Call<JsonObject> doAepsTransaction(@Field("userid") String userid,
                                       @Field("bankiin") String bankiin,
                                       @Field("aadharno") String aadharno,
                                       @Field("transactiontype") String transactiontype,
                                       @Field("data") String data,
                                       @Field("mobileno") String mobileno,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude,
                                       @Field("amount") String amount);

    @POST("GetAePSBankList")
    Call<JsonObject> getBankForAeps();

    @GET("MPlanDthPlanUserHx.aspx")
    Call<JsonObject> getDthMplan(@Query("OpName") String OpName);

    @GET("MPlanCustomerInfoUserHx.aspx")
    Call<JsonObject> getDthUserInfo(@Query("OpName") String operatorName,
                                    @Query("Number") String dthNo);

    @POST("MPlanSpecialUserHx.aspx")
    Call<JsonObject> getMyPlans(@Query("OpName") String OpName,
                                @Query("Number") String Number);

    @GET("MPlanSimpleUserHx.aspx")
    Call<JsonObject> getPlans(@Query("OpName") String OpName,
                              @Query("Circle") String Circle);

    @FormUrlEncoded
    @POST("GetMnpOperator")
    Call<JsonObject> getOperatorCircle(@Header("Authorization") String auth,
                                       @Field("mobileno") String mobileno
    );

    @FormUrlEncoded
    @POST("UtilityTransactionReport")
    Call<JsonObject> getBbpsReport(@Header("Authorization") String auth,
                                  @Field("userid") String userid,
                                  @Field("fromdate") String fromdate,
                                  @Field("todate") String todate);

    @FormUrlEncoded
    @POST("UTIReport")
    Call<JsonObject> getUtiReport(@Header("Authorization") String auth,
                                  @Field("userid") String userid,
                                  @Field("fromdate") String fromdate,
                                  @Field("todate") String todate);


    @FormUrlEncoded
    @POST("OnlineBbpsBillPay")
    Call<JsonObject> payBill(@Header("Authorization") String auth,
                             @Field("userid") String userid,
                             @Field("serviceid") String serviceid,
                             @Field("operatorid") String operatorid,
                             @Field("consumerno") String consumerno,
                             @Field("mobileno") String mobileno,
                             @Field("amount") String amount,
                             @Field("fetchBillID") String fetchBillID,
                             @Field("ConsumerName") String ConsumerName,
                             @Field("BillDate") String BillDate,
                             @Field("DueDate") String DueDate);

    @FormUrlEncoded
    @POST("UtiCouponCalculation")
    Call<JsonObject> calculateCouponAmount(@Header("Authorization") String auth,
                                    @Field("userid") String userid,
                                    @Field("operatorid") String operatorid,
                                    @Field("quantity") String quantity);

    @FormUrlEncoded
    @POST("BuyPsaCoupon")
    Call<JsonObject> purchaseCoupon(@Header("Authorization") String auth,
                               @Field("userid") String userid,
                               @Field("operatorid") String operatorid,
                               @Field("quantity") String quantity,
                               @Field("amount") String amount,
                               @Field("vleid") String vleid,
                               @Field("coupontype") String coupontype);

    @FormUrlEncoded
    @POST("CreatePanServiceAgency")
    Call<JsonObject> createPsa(@Header("Authorization") String auth,
                               @Field("userid") String userid,
                               @Field("vlename") String vlename,
                               @Field("vlelocation") String vlelocation,
                               @Field("vlecontactno") String vlecontactno,
                               @Field("vleemail") String vleemail,
                               @Field("vleshop") String vleshop,
                               @Field("vlestate") String vlestate,
                               @Field("vlepin") String vlepin,
                               @Field("vleaadhar") String vleaadhar,
                               @Field("vlepan") String vlepan);


    @FormUrlEncoded
    @POST("CheckPsaStatusCreateOrNot")
    Call<JsonObject> checkPsaStatus(@Header("Authorization") String auth,
                                    @Field("UserId") String UserId);

    @FormUrlEncoded
    @POST("OnlineBBPSBillFetch")
    Call<JsonObject> fetchBill(@Header("Authorization") String auth,
                               @Field("userid") String userid,
                               @Field("serviceid") String serviceid,
                               @Field("operatorid") String operatorid,
                               @Field("consumerno") String consumerno,
                               @Field("mobileno") String mobileno);

    @FormUrlEncoded
    @POST("RecentReport")
    Call<JsonObject> getRecentReport(@Header("Authorization") String auth,
                                     @Field("userid") String userid,
                                     @Field("filtertype") String filtertype);

    @FormUrlEncoded
    @POST("UserCreditDebit")
    Call<JsonObject> doCreditBalance(@Header("Authorization") String auth,
                                     @Field("userid") String userid,
                                     @Field("crdruserid") String crdruserid,
                                     @Field("amount") String amount,
                                     @Field("remarks") String remarks,
                                     @Field("crdrtype") String crdrtype);

    @FormUrlEncoded
    @POST("FetchParentUser")
    Call<JsonObject> getUsers(@Header("Authorization") String auth,
                              @Field("userid") String userid);

    @FormUrlEncoded
    @POST("OfflinePaymentRequestMaster")
    Call<JsonObject> doPaymentRequest(@Header("Authorization") String auth,
                                      @Field("userid") String userid,
                                      @Field("amount") String amount,
                                      @Field("paymentmode") String paymentmode,
                                      @Field("bankid") String bankid,
                                      @Field("bankname") String bankname,
                                      @Field("depositbybankname") String depositbybankname,
                                      @Field("bankrefno") String bankrefno,
                                      @Field("remarks") String remarks,
                                      @Field("docfile") String docfile);

    @FormUrlEncoded
    @POST("GetBankList")
    Call<JsonObject> getBankList(@Header("Authorization") String auth,
                                 @Field("userID") String userID);

    @Multipart
    @POST("File/DocsFile")
    Call<JsonObject> uploadfile(@Part MultipartBody.Part file);

    @Multipart
    @POST("FileUploading/UploadDocument")
    Call<JsonObject> uploadfileForSettlement(@Part MultipartBody.Part file);

    @FormUrlEncoded
    @POST("RechargeReport")
    Call<JsonObject> getRechargeReport(@Header("Authorization") String auth,
                                       @Field("userid") String userid,
                                       @Field("fromdate") String fromdate,
                                       @Field("todate") String todate);

    @FormUrlEncoded
    @POST("UpiCollectionReport")
    Call<JsonObject> getUpiReport(@Header("Authorization") String auth,
                                  @Field("userid") String userid,
                                  @Field("fromdate") String fromdate,
                                  @Field("todate") String todate);
    @FormUrlEncoded
    @POST("WalletSummary")
    Call<JsonObject> getWalletSummary(@Header("Authorization") String auth,
                                  @Field("userid") String userid,
                                  @Field("fromdate") String fromdate,
                                  @Field("todate") String todate);



    @FormUrlEncoded
    @POST("ConfirmUpiPayment")
    Call<JsonObject> insertUpiDetails(@Header("Authorization") String auth,
                                      @Field("userid") String userid,
                                      @Field("uniqueid") String uniqueid,
                                      @Field("status") String status);

    @FormUrlEncoded
    @POST("UpdateUpiPayments")
    Call<JsonObject> updateUpiDetails(@Header("Authorization") String auth,
                                      @Field("userid") String userid,
                                      @Field("amount") String amount);


    @FormUrlEncoded
    @POST("FetchServices")
    Call<JsonObject> getServices(
            @Header("Authorization") String auth,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo,
            @Field("userID") String userID);

    @FormUrlEncoded
    @POST("FetchOperatorList")
    Call<JsonObject> getOperators(@Header("Authorization") String auth,
                                  @Field("tokenKey") String tokenKey,
                                  @Field("deviceInfo") String deviceInfo,
                                  @Field("userID") String userID,
                                  @Field("serviceid") String serviceID);


    @FormUrlEncoded
    @POST("DoMobileRechargeTransaction")
    Call<JsonObject> doRecharge(
            @Header("Authorization") String auth,
            @Field("userid") String userid,
            @Field("amount") String amount,
            @Field("servicetype") String servicetype,
            @Field("mobileno") String mobileno,
            @Field("operatorid") String operatorid);

    @FormUrlEncoded
    @POST("userauthentication")
    Call<JsonObject> login(
            @Header("Authorization") String auth,
            @Field("UserName") String UserName,
            @Field("Password") String Password,
            @Field("tokenKey") String tokenKey,
            @Field("deviceInfo") String deviceInfo);

    @FormUrlEncoded
    @POST("UserWalletBalanceMaster")
    Call<JsonObject> getBalance(@Header("Authorization") String auth,
                                @Field("userid") String userid,
                                @Field("wallettype") String wallettype);


}
