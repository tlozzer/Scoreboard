package br.com.zipvix.sportsscoreboard

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import br.com.zipvix.sportsscoreboard.adapter.SectionsPagerAdapter
import br.com.zipvix.sportsscoreboard.business.Match
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sectionsPagerAdapter = SectionsPagerAdapter(
            this,
            supportFragmentManager
        )
        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

        ViewModelProviders.of(this)[MainViewModel::class.java].also {
            it.status.observe(this, Observer { status ->
                if (status == Match.Status.RUNNING) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            })
        }
    }
}