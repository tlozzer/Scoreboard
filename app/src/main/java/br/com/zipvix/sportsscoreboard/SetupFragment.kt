package br.com.zipvix.sportsscoreboard

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import br.com.zipvix.sportsscoreboard.viewmodel.MainViewModel

class SetupFragment : Fragment(), SeekBar.OnSeekBarChangeListener, View.OnFocusChangeListener {
    private lateinit var realTimeSeekBar: SeekBar
    private lateinit var simTimeSeekBar: SeekBar
    private lateinit var realTimeTextView: TextView
    private lateinit var simTimeTextView: TextView
    private lateinit var homeTeamAutoComplete: AutoCompleteTextView
    private lateinit var awayTeamAutoComplete: AutoCompleteTextView
    private lateinit var button: Button
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: ArrayAdapter<String>
    private val teamsAutoComplete = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProviders.of(this)[MainViewModel::class.java]
        } ?: throw Exception(getString(R.string.null_activity_exception))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = activity?.run {
            ArrayAdapter<String>(this, R.layout.team_list_autocomplete_item, teamsAutoComplete)
        } ?: throw Exception(getString(R.string.null_activity_exception))

        viewModel.getTeams().observe(this, Observer { teams ->
            adapter.clear()
            adapter.addAll(teams.list)
        })

        realTimeSeekBar = view?.findViewById(R.id.real_time)!!
        realTimeSeekBar.setOnSeekBarChangeListener(this)

        simTimeSeekBar = view?.findViewById(R.id.sim_time)!!
        simTimeSeekBar.setOnSeekBarChangeListener(this)

        realTimeTextView = view?.findViewById(R.id.time_label)!!
        viewModel.getRealTime().observe(this, Observer<Long> { value ->
            realTimeTextView.text = getString(R.string.real_time_label, value)
        })

        simTimeTextView = view?.findViewById<TextView>(R.id.sim_time_label)?.also {
            viewModel.getSimTime().observe(this, Observer<Long> { value ->
                it.text = getString(R.string.sim_time_label, value)
            })
        } ?: throw Exception("View not found")

        homeTeamAutoComplete =
            view?.findViewById<AutoCompleteTextView>(R.id.home_team_autocomplete)?.also {
                it.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        viewModel.setHomeTeam(s?.toString() ?: "")
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
                it.onFocusChangeListener = this
                it.setAdapter(adapter)
            } ?: throw Exception("View not found")

        awayTeamAutoComplete =
            view?.findViewById<AutoCompleteTextView>(R.id.away_team_autocomplete)?.also {
                it.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        viewModel.setAwayTeam(s?.toString() ?: "")
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }
                })
                it.onFocusChangeListener = this
                it.setAdapter(adapter)
            } ?: throw Exception("View not found")

        button = view?.findViewById(R.id.start)!!
        button.setOnClickListener {
            viewModel.start()
            (activity as MainActivity).viewPager.setCurrentItem(1, true)
        }

        viewModel.loadTeams()
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.sim_time -> viewModel.setSimTimeSeekBarProgress(progress)
            else -> viewModel.setRealTimeSeekBarProgress(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

    override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)
    }

    override fun onFocusChange(view: View?, hasFocus: Boolean) {
        if (!hasFocus) hideKeyboard()
    }
}