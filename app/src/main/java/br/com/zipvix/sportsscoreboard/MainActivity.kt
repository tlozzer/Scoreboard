package br.com.zipvix.sportsscoreboard

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import br.com.zipvix.sportsscoreboard.adapter.SectionsPagerAdapter
import br.com.zipvix.sportsscoreboard.business.Scoreboard
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var fabNext: FloatingActionButton

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

        val viewModel = ViewModelProviders.of(this)[MainViewModel::class.java].also {
            it.status.observe(this, Observer { status ->
                fabNext.let { fab ->
                    if (status == Scoreboard.Status.RUNNING) {
                        fab.hide()
                    } else {
                        fab.show()
                    }
                }


                if (status == Scoreboard.Status.RUNNING) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                } else {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                }
            })
        }

        fabNext = findViewById<FloatingActionButton>(R.id.fab_next)?.also {
            it.setOnClickListener {
                viewModel.start()
                viewPager.setCurrentItem(1, true)
            }
        } ?: throw Exception("FAB view not found.")
    }
}