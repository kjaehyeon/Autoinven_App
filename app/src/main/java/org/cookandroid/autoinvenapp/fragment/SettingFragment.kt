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
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import org.cookandroid.autoinvenapp.LoginActivity
import org.cookandroid.autoinvenapp.MainActivity
import org.cookandroid.autoinvenapp.R
import org.cookandroid.autoinvenapp.objects.PrefObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mainActivity = context as MainActivity
        var view =inflater.inflate(R.layout.fragment_setting, container, false)
        var logout = view.findViewById<LinearLayout>(R.id.logout)
        var info = view.findViewById<LinearLayout>(R.id.info)
        logout.setOnClickListener {
            var dlg = activity?.let { it1 -> AlertDialog.Builder(it1) }
            dlg!!.setTitle("로그아웃")
            dlg.setMessage("정말 로그아웃 하시겠습니까?")
            dlg.setNegativeButton("취소", null)
            dlg.setPositiveButton("확인"){ dialog, which ->
                var editor = PrefObject.prefs.edit()
                editor.clear()
                editor.apply()
                startActivity(Intent(mainActivity, LoginActivity::class.java))
                mainActivity.finish()
            }
            dlg.show()
        }
        info.setOnClickListener {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
        }
        return view
    }

}