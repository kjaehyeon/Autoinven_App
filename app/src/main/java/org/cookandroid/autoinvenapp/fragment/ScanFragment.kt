package org.cookandroid.autoinvenapp.fragment


import LoadingActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.R
import org.cookandroid.autoinvenapp.api.ItemDetailAPI
import org.cookandroid.autoinvenapp.api.ScanAPI
import org.cookandroid.autoinvenapp.data.ItemDetailData
import org.cookandroid.autoinvenapp.enums.ItemStatus
import org.cookandroid.autoinvenapp.enums.getItemStatusFromInt
import org.cookandroid.autoinvenapp.objects.ApiClient
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ScanFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var openQRbtn : ExtendedFloatingActionButton
    lateinit var itemIn : ExtendedFloatingActionButton
    lateinit var itemOut : ExtendedFloatingActionButton
    lateinit var emptyTextLayout : LinearLayout
    lateinit var itemInfoLayout : LinearLayout
    lateinit var dialog : LoadingActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = context as MainActivity
        var view : View = inflater.inflate(R.layout.fragment_scan, container, false)
        openQRbtn = view.findViewById(R.id.openQRbtn)
        itemIn = view.findViewById(R.id.item_in)
        itemOut = view.findViewById(R.id.item_out)
        emptyTextLayout = view.findViewById(R.id.emptyText_layout)
        itemInfoLayout = view.findViewById(R.id.item_info_layout)
        dialog = LoadingActivity(mainActivity)
        openQRbtn.setOnClickListener {
            openQRScanner()
        }
        showEmptyLayout()
        return view
    }

    private fun openQRScanner(){
        IntentIntegrator.forSupportFragment(this)
            .setBeepEnabled(false)
            .setOrientationLocked(true)
            .setPrompt("QR코드를 스캔해주세요.")
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .initiateScan()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result.contents != null){
            sendQR(result.contents)
        }else{
            showEmptyLayout()
        }
    }

    private fun fillItemInfo(data : ItemDetailData){
        //TODO("layout과 매핑시켜주기")
    }
    private fun showEmptyLayout(){
        itemInfoLayout.visibility = View.INVISIBLE
        itemIn.visibility = View.INVISIBLE
        itemOut.visibility = View.INVISIBLE
        emptyTextLayout.visibility = View.VISIBLE
    }

    private fun sendQR(qrContents : String){
        val api = ApiClient.getApiClient().create(ItemDetailAPI::class.java)
        val callGetItemDetail = api.getItemDetail(QR=qrContents)
        dialog.show()
        callGetItemDetail.enqueue(object : Callback<ItemDetailData> {
            override fun onResponse(
                call: Call<ItemDetailData>,
                response: Response<ItemDetailData>
            ) {
                when(response.code()){
                    200 -> {
                        //fillItemInfo(response.body()!!) //layout에 아이템 정보 채움
                        val api = ApiClient.getApiClient().create(ScanAPI::class.java)
                        when(getItemStatusFromInt(response.body()!!.current_status)){
                            //View들의 Visibility 설정
                            ItemStatus.BEFORE_RECEIVING ->{ // 입고 전 상태일 경우
                                itemIn.visibility = View.VISIBLE
                                itemOut.visibility = View.INVISIBLE
                                emptyTextLayout.visibility = View.INVISIBLE
                                itemInfoLayout.visibility = View.VISIBLE

                                val callItemIn = api.itemIn(qr=qrContents)
                                setInOutButtonListener(itemIn, "입고 완료", callItemIn)
                            }
                            ItemStatus.RECEIVED ->{ // 입고 완료 상태일 경우
                                itemIn.visibility = View.INVISIBLE
                                itemOut.visibility = View.VISIBLE
                                emptyTextLayout.visibility = View.INVISIBLE
                                itemInfoLayout.visibility = View.VISIBLE

                                val callItemOut = api.itemOut(qr=qrContents)
                                setInOutButtonListener(itemOut, "출고 완료", callItemOut)
                            }
                            ItemStatus.RELEASED ->{ //출고 완료 상태일 경우
                                itemIn.visibility = View.INVISIBLE
                                itemOut.visibility = View.INVISIBLE
                                emptyTextLayout.visibility = View.INVISIBLE
                                itemInfoLayout.visibility = View.VISIBLE
                            }
                        }
                        dialog.dismiss()
                    }
                    400 ->{
                        showEmptyLayout()
                        AlertDialog.Builder(mainActivity)
                            .setTitle("Message") //제목
                            .setMessage("잘못된 QR 코드 입니다.") // 메시지
                            .setNegativeButton("닫기", null)
                            .show()
                        dialog.dismiss()
                    }
                    401 ->{
                        PrefObject.sendLoginApi(
                            PrefObject.prefs.getString("id", "").toString(),
                            PrefObject.prefs.getString("pw", "").toString(),
                            mainActivity
                        )
                        call.clone().enqueue(this)
                    }
                }
            }
            override fun onFailure(call: Call<ItemDetailData>, t: Throwable) {
                showEmptyLayout()
                AlertDialog.Builder(mainActivity)
                    .setTitle("Message") //제목
                    .setMessage("다시 시도해주세요.") // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
                dialog.dismiss()
            }
        })
    }
    private fun setInOutButtonListener(
        button : ExtendedFloatingActionButton,
        dialogMessage : String,
        call : Call<Response<Void>>){

        button.setOnClickListener {
            call.enqueue(object : Callback<Response<Void>>{
                override fun onResponse(
                    call: Call<Response<Void>>,
                    response: Response<Response<Void>>
                ) {
                    when(response.code()){
                        200 ->{
                            AlertDialog.Builder(mainActivity)
                                .setTitle("Message") //제목
                                .setMessage(dialogMessage) // 메시지
                                .setNegativeButton("닫기", null)
                                .show()
                            showEmptyLayout()
                        }
                        401 ->{
                            PrefObject.sendLoginApi(
                                PrefObject.prefs.getString("id", "").toString(),
                                PrefObject.prefs.getString("pw", "").toString(),
                                mainActivity
                            )
                            call.clone().enqueue(this)
                            //TODO("토큰 갱신 시도 횟수 제한 로직 추가 바람")
                        }
                    }
                }
                override fun onFailure(
                    call: Call<Response<Void>>,
                    t: Throwable
                ) {
                    AlertDialog.Builder(mainActivity)
                        .setTitle("Message") //제목
                        .setMessage("실패하였습니다.") // 메시지
                        .setNegativeButton("닫기", null)
                        .show()
                    showEmptyLayout()
                }
            })
        }
    }
}