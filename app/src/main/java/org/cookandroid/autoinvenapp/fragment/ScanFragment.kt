package org.cookandroid.autoinvenapp.fragment


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.R


class ScanFragment : Fragment() {
    lateinit var mainActivity: MainActivity
    lateinit var openQRbtn : ExtendedFloatingActionButton
    lateinit var itemIn : ExtendedFloatingActionButton
    lateinit var itemOut : ExtendedFloatingActionButton
    lateinit var emptyTextLayout : LinearLayout
    lateinit var itemInfoLayout : LinearLayout

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
        openQRbtn.setOnClickListener {
            openQRScanner()
        }
        itemInfoLayout.visibility = View.INVISIBLE
        itemIn.visibility = View.INVISIBLE
        itemOut.visibility = View.INVISIBLE
        emptyTextLayout.visibility = View.VISIBLE

        return view
    }

    private fun openQRScanner(){
        IntentIntegrator.forSupportFragment(this)
            .setBeepEnabled(false)
            .setOrientationLocked(true)
            .setPrompt("QR코드를 인증해주세요.")
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result !=null) {
            if(result.contents == null) {
                // qr코드에 주소가 없거나, 뒤로가기 클릭 시
                AlertDialog.Builder(mainActivity)
                    .setTitle("Message") //제목
                    .setMessage("Nothing") // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
            } else {
                //qr코드에 주소가 있을때 -> 주소에 관한 Toast 띄우는 함수 호출
                AlertDialog.Builder(mainActivity)
                    .setTitle("Message") //제목
                    .setMessage(result.contents) // 메시지
                    .setNegativeButton("닫기", null)
                    .show()
            }
        }
    }
}