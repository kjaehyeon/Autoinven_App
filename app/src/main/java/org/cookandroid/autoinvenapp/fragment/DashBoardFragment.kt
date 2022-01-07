package org.cookandroid.autoinvenapp.fragment

import LoadingActivity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
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

class DashBoardFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mainActivity: MainActivity
    lateinit var rv_warehouse_list : RecyclerView
    lateinit var wareHouseAdapter : WareHouseAdapter
    lateinit var token : String
    lateinit var emptyText : TextView
    lateinit var dialog : LoadingActivity
    var datas = mutableListOf<WareHouseResponse>()

    private val api = ApiClient.getApiClient().create(WareHouseAPI::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        mainActivity = context as MainActivity
        rv_warehouse_list = view.findViewById(R.id.rv_warehouse_list)
        token = PrefObject.prefs.getString("token", "ERROR")!!
        emptyText = view.findViewById(R.id.emptyText)
        dialog = LoadingActivity(mainActivity)
        initRecyler()

        return view!!
    }

    private fun initRecyler() {
        wareHouseAdapter = WareHouseAdapter(mainActivity)
        rv_warehouse_list.adapter = wareHouseAdapter

        val callGetWareHouseList = api.getWareHouseList()
        dialog.show()
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
                                        usage=data.usage, images=data.images, description = data.description)
                                )
                                wareHouseAdapter.datas = datas
                                wareHouseAdapter.notifyDataSetChanged()
                            }
                        }
                        dismissLoadingBar()
                    }
                    401 ->{
                        Log.d("test", "401")
                        PrefObject.sendLoginApi(
                            PrefObject.prefs.getString("id", "").toString(),
                            PrefObject.prefs.getString("pw", "").toString(),
                            mainActivity
                        )
                        call.clone().enqueue(this)
                    }
                    else ->{
                        AlertDialog.Builder(mainActivity)
                            .setTitle("Message") //제목
                            .setMessage("다시 시도해주세요") // 메시지
                            .setNegativeButton("닫기", null)
                            .show()
                        dismissLoadingBar()
                    }
                }
            }
            override fun onFailure(call: Call<List<WareHouseResponse>>, t: Throwable) {
                AlertDialog.Builder(mainActivity)
                    .setTitle("Message") //제목
                    .setMessage("실패.") // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
                dismissLoadingBar()
            }
        })
    }
    fun dismissLoadingBar(){
        if(datas.isEmpty()){
            rv_warehouse_list.isVisible = false
            emptyText.isVisible = true
        }
        dialog.dismiss()
    }
}

class WareHouseAdapter(private val context: Context): RecyclerView.Adapter<WareHouseAdapter.ViewHolder>(){
    var datas = mutableListOf<WareHouseResponse>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_warehouse_list_element,parent,false)
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
            usage.text = wareHouse.usage.toString() + "%"
            description.text = wareHouse.description
            Glide.with(itemView).load(wareHouse.images[0]).into(image)

            itemView.setOnClickListener {
                Intent(context, WareHouseActivity::class.java).apply {
                    putExtra("wid", wareHouse.wid)
                    putExtra("warehouse_name", wareHouse.name)
                }.run { context.startActivity(this) }
            }
        }
    }
}