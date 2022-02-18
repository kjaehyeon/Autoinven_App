package org.cookandroid.autoinvenapp.fragment

import LoadingActivity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.R
import org.cookandroid.autoinvenapp.WareHouseActivity
import org.cookandroid.autoinvenapp.api.WareHouseAPI
import org.cookandroid.autoinvenapp.data.WareHouseResponse
import org.cookandroid.autoinvenapp.objects.ApiClient
import org.cookandroid.autoinvenapp.objects.PrefObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashBoardFragment : Fragment() {
    private lateinit var swipeRefreshLayout : SwipeRefreshLayout
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
        var view = inflater.inflate(R.layout.fragment_dash_board, container, false)
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        mainActivity = context as MainActivity
        rv_warehouse_list = view.findViewById(R.id.rv_warehouse_list)
        token = PrefObject.prefs.getString("token", "ERROR")!!
        emptyText = view.findViewById(R.id.emptyText)
        dialog = LoadingActivity(mainActivity)
        swipeRefreshLayout.setOnRefreshListener{
            datas.clear()
            wareHouseAdapter.notifyDataSetChanged()
            initRecycler()
            swipeRefreshLayout.isRefreshing = false
        }
        initRecycler()

        return view!!
    }

    private fun initRecycler() {
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
                                    WareHouseResponse(warehouse_id=data.warehouse_id, name_ko=data.name_ko, address1_ko=data.address1_ko,
                                         WarehouseImages=data.WarehouseImages, note_ko = data.note_ko)
                                )
                                wareHouseAdapter.datas = datas
                                wareHouseAdapter.notifyDataSetChanged()
                            }
                        }
                        dismissLoadingBar()
                    }
                    406 ->{
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
    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
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
        private val description : TextView = itemView.findViewById(R.id.description)


        fun bind(wareHouse: WareHouseResponse) {
            name.text = wareHouse.name_ko
            address.text = wareHouse.address1_ko
            description.text = wareHouse.note_ko

            if(wareHouse.WarehouseImages!!.isEmpty()){
                image.setImageResource(R.drawable.default_img)
            }else{
                Glide.with(itemView).load(ApiClient.BASE_URL+wareHouse.WarehouseImages[0].url).into(image)
            }
            itemView.setOnClickListener {
                Intent(context, WareHouseActivity::class.java).apply {
                    putExtra("wid", wareHouse.warehouse_id)
                    putExtra("warehouse_name", wareHouse.name_ko)
                }.run { context.startActivity(this) }
            }
        }
    }
}