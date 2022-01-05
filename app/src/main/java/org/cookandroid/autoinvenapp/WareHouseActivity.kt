package org.cookandroid.autoinvenapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.cookandroid.autoinvenapp.objects.ApiClient
import org.cookandroid.autoinvenapp.api.ItemListAPI
import org.cookandroid.autoinvenapp.data.ItemListResponseData
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WareHouseActivity : AppCompatActivity() {
    lateinit var wid : String
    lateinit var wareHouseName : String
    lateinit var token : String
    lateinit var rv_item_list : RecyclerView
    lateinit var itemListAdapter : ItemListAdapter

    var datas = mutableListOf<ItemListResponseData>()
    private val api = ApiClient.getApiClient().create(ItemListAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ware_house)

        wid = intent.getStringExtra("wid").toString()
        wareHouseName = intent.getStringExtra("wareHouseName").toString()
        rv_item_list = findViewById(R.id.rv_item_list)
        token = PrefObject.prefs.getString("token", "ERROR")!!
        initRecyler()
    }
    private fun initRecyler() {
        itemListAdapter = ItemListAdapter(this@WareHouseActivity)
        rv_item_list.adapter = itemListAdapter

        val callGetWareHouseList = api.getItemList(wid=wid)
        callGetWareHouseList.enqueue(object : Callback<List<ItemListResponseData>> {
            override fun onResponse(
                call: Call<List<ItemListResponseData>>,
                response: Response<List<ItemListResponseData>>
            ) {
                when(response.code()){
                    200 -> {
                        var iterator: Iterator<ItemListResponseData> = response.body()!!.iterator()
                        while (iterator.hasNext()) {
                            var data = iterator.next()
                            datas.apply {
                                add(
                                    ItemListResponseData(
                                        it_id = data.it_id,
                                        name = data.name,
                                        status = data.status,
                                        datetime = data.datetime,
                                        buyer_name = data.buyer_name,
                                        image = data.image
                                    )
                                )

                                itemListAdapter.datas = datas
                                itemListAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    400 ->{
                        AlertDialog.Builder(this@WareHouseActivity)
                            .setTitle("Message") //제목
                            .setMessage("비밀번호가 변경되었습니다.") // 메시지
                            .setNegativeButton("닫기", null)
                            .show()
                        //TODO("자동으로 로그아웃 되는 로직 추가 바람")
                    }
                    401 ->{
                        PrefObject.sendLoginApi(
                            PrefObject.prefs.getString("id", "").toString(),
                            PrefObject.prefs.getString("pw", "").toString(),
                            this@WareHouseActivity
                        )
                        call.clone().enqueue(this)
                    }
                    else ->{
                        AlertDialog.Builder(this@WareHouseActivity)
                            .setTitle("Message") //제목
                            .setMessage("다시 시도해 주세요.") // 메시지
                            .setNegativeButton("닫기", null)
                            .show()
                    }
                }
            }
            override fun onFailure(call: Call<List<ItemListResponseData>>, t: Throwable) {
                AlertDialog.Builder(this@WareHouseActivity)
                    .setTitle("Message") //제목
                    .setMessage("실패") // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
            }
        })
    }
}

class ItemListAdapter(private val context: Context): RecyclerView.Adapter<ItemListAdapter.ViewHolder>(){
    var datas = mutableListOf<ItemListResponseData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_item_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val datetimeName: TextView = itemView.findViewById(R.id.datetime_name)
        private val datetime : TextView = itemView.findViewById(R.id.datetime)
        private val buyerName : TextView = itemView.findViewById(R.id.buyer_name)
        private val statusBadge : Button = itemView.findViewById(R.id.status_badge)

        @SuppressLint("ResourceAsColor")
        fun bind(item: ItemListResponseData) {
            Glide.with(itemView).load(item.image).into(itemImage)
            itemName.text = item.name
            datetime.text = item.datetime
            buyerName.text = item.datetime

            when(item.status){
                0 -> {
                    datetimeName.text ="등록일"
                    statusBadge.setBackgroundColor(R.color.gray)
                    statusBadge.text="입고전"
                }
                1 ->{
                    datetimeName.text ="입고일"
                    statusBadge.setBackgroundColor(R.color.light_green)
                    statusBadge.text="입고"
                }
                2 ->{
                    datetimeName.text ="출고일"
                    statusBadge.setBackgroundColor(R.color.light_red)
                    statusBadge.text="출고"
                }
            }
        }
    }
}