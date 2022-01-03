package org.cookandroid.autoinvenapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.cookandroid.autoinvenapp.*
import org.cookandroid.autoinvenapp.objects.ApiClient
import org.cookandroid.autoinvenapp.api.WareHouseAPI
import org.cookandroid.autoinvenapp.data.WareHouseResponse
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class DashBoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity: MainActivity
    lateinit var rv_warehouse_list : RecyclerView
    lateinit var wareHouseAdapter : WareHouseAdapter
    lateinit var token : String
    var datas = mutableListOf<WareHouseResponse>()

//    val BASE_URL = "http://192.168.0.17:4000/"
//    val retrofit = Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    val api = retrofit.create(WareHouseAPI::class.java)
    private val api = ApiClient.getApiClient().create(WareHouseAPI::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        mainActivity = context as MainActivity
        rv_warehouse_list = view.findViewById(R.id.rv_warehouse_list)
        token = PrefObject.prefs.getString("token", "ERROR")!!
        initRecyler()

        return view!!
    }

    private fun initRecyler() {
        wareHouseAdapter = WareHouseAdapter(mainActivity)
        rv_warehouse_list.adapter = wareHouseAdapter

        val callGetWareHouseList = api.getWareHouseList(token=token)
        callGetWareHouseList.enqueue(object : Callback<List<WareHouseResponse>> {
            override fun onResponse(
                call: Call<List<WareHouseResponse>>,
                response: Response<List<WareHouseResponse>>
            ) {
                when(response.code()){
                    200 ->{
                        var iterator : Iterator<WareHouseResponse> = response.body()!!.iterator()
                        while(iterator.hasNext()){
                            var data = iterator.next()
                            datas.apply {
                                add(
                                    WareHouseResponse(wid=data.wid, name=data.name, address=data.address,
                                        usage=data.usage, image=data.image, description = data.description)
                                )

                                wareHouseAdapter.datas = datas
                                wareHouseAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    401 ->{
                        onFailure(call, Exception())
                    }
                    else ->{
                        AlertDialog.Builder(mainActivity)
                            .setTitle("Message") //제목
                            .setMessage("다시 시도해주세요") // 메시지
                            .setPositiveButton("닫기", null)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<List<WareHouseResponse>>, t: Throwable) {
                AlertDialog.Builder(mainActivity)
                    .setTitle("Message") //제목
                    .setMessage("onFaliure") // 메시지
                    .setPositiveButton("닫기", null)
                    .show()
            }
//            override fun onFinalFailure(call: Call<List<WareHouseResponse>>?, t: Throwable?) {
//                AlertDialog.Builder(mainActivity)
//                    .setTitle("Message") //제목
//                    .setMessage("다시 시도해주세요") // 메시지
//                    .setPositiveButton("닫기", null)
//                    .show()
//            }
        })
    }
}

class WareHouseAdapter(private val context: Context): RecyclerView.Adapter<WareHouseAdapter.ViewHolder>(){
    var datas = mutableListOf<WareHouseResponse>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_warehouse_list,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    override fun getItemCount(): Int = datas.size


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val address: TextView = itemView.findViewById(R.id.address)
        private val image: ImageView = itemView.findViewById(R.id.image)
        private val usage : TextView = itemView.findViewById(R.id.usage_percent)
        private val description : TextView = itemView.findViewById(R.id.description)


        fun bind(wareHouse: WareHouseResponse) {
            name.text = wareHouse.name
            address.text = wareHouse.address
            usage.text = wareHouse.usage.toString()
            description.text = wareHouse.description
            Glide.with(itemView).load(wareHouse.image[0]).into(image)

            itemView.setOnClickListener {
                Intent(context, WareHouseActivity::class.java).apply {
                    putExtra("wid", wareHouse.wid)
                    putExtra("warehouse_name", wareHouse.name)
                }.run { context.startActivity(this) }
            }
        }
    }
}