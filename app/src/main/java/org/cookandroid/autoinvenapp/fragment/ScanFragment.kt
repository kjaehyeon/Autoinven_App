package org.cookandroid.autoinvenapp.fragment


import LoadingActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.R
import org.cookandroid.autoinvenapp.api.ItemDetailAPI
import org.cookandroid.autoinvenapp.api.ScanAPI
import org.cookandroid.autoinvenapp.data.ItemDetailData
import org.cookandroid.autoinvenapp.enums.getItemStatusFromInt
import org.cookandroid.autoinvenapp.objects.ApiClient
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.cookandroid.autoinvenapp.enums.ItemStatus.*
import java.text.SimpleDateFormat
import java.time.LocalDate


class ScanFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var openQRbtn : ExtendedFloatingActionButton
    lateinit var itemIn : ExtendedFloatingActionButton
    lateinit var itemOut : ExtendedFloatingActionButton
    lateinit var emptyTextLayout : LinearLayout
    lateinit var itemInfoLayout : LinearLayout
    lateinit var dialog : LoadingActivity

    lateinit var itemImage : ImageView
    lateinit var itemStateBadge : TextView
    lateinit var itemName : TextView
    lateinit var buyerName : TextView
    lateinit var warehouseName : TextView
    lateinit var datetimeTitle : TextView
    lateinit var datetime : TextView
    lateinit var description : TextView

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

        itemImage = view.findViewById(R.id.item_image)
        itemStateBadge = view.findViewById(R.id.item_state_badge)
        itemName = view.findViewById(R.id.item_name)
        buyerName = view.findViewById(R.id.buyer_name)
        datetimeTitle = view.findViewById(R.id.datetime_title)
        datetime = view.findViewById(R.id.datetime)
        description = view.findViewById(R.id.description)

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
    private fun sendQR(qrContents : String){
        val api = ApiClient.getApiClient().create(ItemDetailAPI::class.java)
        val callGetItemDetail = api.getItemDetail(item_id =qrContents)
        dialog.show()
        callGetItemDetail.enqueue(object : Callback<ItemDetailData> {
            override fun onResponse(
                call: Call<ItemDetailData>,
                response: Response<ItemDetailData>
            ) {
                when(response.code()){
                    200 -> {

                        fillItemInfo(response.body()!!) //layout에 아이템 정보 채움
                        val api = ApiClient.getApiClient().create(ScanAPI::class.java)
                        when(getItemStatusFromInt(response.body()!!.i_state_id)){
                            //View들의 Visibility 설정
                            BEFORE_RECEIVING ->{ // 입고 전 상태일 경우
                                itemIn.visibility = View.VISIBLE
                                itemOut.visibility = View.INVISIBLE
                                emptyTextLayout.visibility = View.INVISIBLE
                                itemInfoLayout.visibility = View.VISIBLE

                                val callItemIn = api.itemIn(item_id= response.body()!!.item_id)
                                setInOutButtonListener(itemIn, "입고 완료", callItemIn)
                            }
                            RECEIVED ->{ // 입고 완료 상태일 경우
                                itemIn.visibility = View.INVISIBLE
                                itemOut.visibility = View.VISIBLE
                                emptyTextLayout.visibility = View.INVISIBLE
                                itemInfoLayout.visibility = View.VISIBLE

                                val callItemOut = api.itemOut(item_id=response.body()!!.item_id)
                                setInOutButtonListener(itemOut, "출고 완료", callItemOut)
                            }
                            RELEASED ->{ //출고 완료 상태일 경우
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
                    406 ->{
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
    private fun fillItemInfo(data : ItemDetailData){
        itemName.text = data.name
        buyerName.text = data.User.name
        datetime.text = data.createdAt
        description.text = data.note

        if(data.note != null)
            description.text = data.note
        else
            description.text="입력하지 않은 정보입니다."

        if(data.ItemImages!!.isEmpty()){
            itemImage.setImageResource(R.drawable.default_img)
        }else{
            Glide.with(this).load(ApiClient.BASE_URL+data.ItemImages[0].url).into(itemImage)

        }
        when(getItemStatusFromInt(data.i_state_id)){
            BEFORE_RECEIVING ->{
                itemStateBadge.text = BEFORE_RECEIVING.description
                itemStateBadge.background = ContextCompat.getDrawable(mainActivity, R.drawable.state0_background)
                datetimeTitle.text = BEFORE_RECEIVING.datetime_title
            }
            RECEIVED ->{
                itemStateBadge.text = RECEIVED.description
                itemStateBadge.background = ContextCompat.getDrawable(mainActivity, R.drawable.state1_background)
                datetimeTitle.text = RECEIVED.datetime_title
            }
            RELEASED ->{
                itemStateBadge.text = RELEASED.description
                itemStateBadge.background = ContextCompat.getDrawable(mainActivity, R.drawable.state2_background)
                datetimeTitle.text = RELEASED.datetime_title
            }
        }
    }
    private fun showEmptyLayout(){
        itemInfoLayout.visibility = View.INVISIBLE
        itemIn.visibility = View.INVISIBLE
        itemOut.visibility = View.INVISIBLE
        emptyTextLayout.visibility = View.VISIBLE
    }

    private fun setInOutButtonListener(
        button : ExtendedFloatingActionButton,
        dialogMessage : String,
        call : Call<Response<Void>>){
        button.setOnClickListener {
            dialog.show()
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
                        dialog.dismiss()
                        }
                        406 ->{
                            PrefObject.sendLoginApi(
                                PrefObject.prefs.getString("id", "").toString(),
                                PrefObject.prefs.getString("pw", "").toString(),
                                mainActivity
                            )
                            call.clone().enqueue(this)
                            //TODO("토큰 갱신 시도 횟수 제한 로직 추가 바람")
                            dialog.dismiss()
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
                    dialog.dismiss()
                }
            })
        }
    }

}