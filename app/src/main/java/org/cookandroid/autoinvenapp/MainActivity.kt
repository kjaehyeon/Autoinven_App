package org.cookandroid.autoinvenapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import org.cookandroid.autoinvenapp.fragment.DashBoardFragment
import org.cookandroid.autoinvenapp.fragment.ScanFragment
import org.cookandroid.autoinvenapp.fragment.SettingFragment

class MainActivity : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var pager : ViewPager2
    lateinit var loginActivity: LoginActivity
    private val tablayoutTextArray = arrayOf("SCAN", "DASH BOARD", "SETTINGS")
    private val tablayoutIconArray = arrayOf(R.drawable.ic_baseline_qr_code_scanner_24,
        R.drawable.ic_baseline_view_list_24,
        R.drawable.ic_baseline_settings_24)
    private val PAGE_CNT = 3
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tabLayout = findViewById(R.id.tab_bar)
        pager = findViewById(R.id.pager)
        pager.adapter = ViewPagerAdapter(this)
        TabLayoutMediator(tabLayout, pager){tab, position ->
            tab.text = tablayoutTextArray[position]
            tab.setIcon(tablayoutIconArray[position])
        }.attach()
    }
    private inner class ViewPagerAdapter(fragment : FragmentActivity):FragmentStateAdapter(fragment){
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ScanFragment()
                1 -> DashBoardFragment()
                2 -> SettingFragment()
                else -> throw RuntimeException("Runtime Error")
            }
        }
        override fun getItemCount():Int = PAGE_CNT
    }
}