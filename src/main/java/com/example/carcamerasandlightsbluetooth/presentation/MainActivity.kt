package com.example.carcamerasandlightsbluetooth.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.carcamerasandlightsbluetooth.App
import com.example.carcamerasandlightsbluetooth.R
import com.example.carcamerasandlightsbluetooth.databinding.ActivityMainBinding
import com.example.carcamerasandlightsbluetooth.domain.model.DeviceState
import com.example.carcamerasandlightsbluetooth.domain.model.Timings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

const val LOG_VIEW_SCROLL_CORRECTION = 2
const val PRESCROLL_DELAY = 30L

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var viewModel: RootViewModel
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!
    private var timingValuesArray: ArrayList<EditText>? = null

    override fun onResume() {
        super.onResume()

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        viewModel.stateToObserve.value?.let {
            if (!viewModel.stateToObserve.value!!.isSetTimings) scrollLogView()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        (applicationContext as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            actionBar?.hide()
        } else
            requestWindowFeature(Window.FEATURE_ACTION_BAR)
        _binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        viewModel.stateToObserve.observe(this) { renderMainState(it) }
        binding.LogView.movementMethod = ScrollingMovementMethod()
        viewModel.serviceLogToObserve.observe(this) {
            binding.LogView.text = it
        }
        setClickListeners()
        timingValuesArray = combineTimingsEditTextToArrayList()
        timingValuesArray!!.forEach { it.doAfterTextChanged { _ -> timingsToSendCheck() } }
    }

    private fun combineTimingsEditTextToArrayList(): ArrayList<EditText> {
        with(binding.timingsSet) {
            return arrayListOf(
                bounceValue,
                repeaterValue,
                frontDelayValue,
                rearDelayValue
            )
        }
    }

    private fun timingsToSendCheck() {
        if (timingValuesArray == null) return
        var isReadyToSend = true
        val errorText = getString(R.string.not_gotten)
        timingValuesArray!!.forEach { editText ->
            val localNum = try {
                editText.text.toString().toInt()
            } catch (e: Exception) {
                65535
            }
            if (editText.text.toString() == errorText
                || localNum >= 65534
            ) {
                isReadyToSend = false
                return@forEach
            }
        }
        binding.timingsSet.sendTimings.isEnabled = isReadyToSend
    }

    private fun setClickListeners() {
        binding.timingsSet.sendTimings.setOnClickListener {
            with(binding.timingsSet) {
                viewModel.sendTimings(
                    Timings(
                        bounce = bounceValue.text.toString().toInt(),
                        repeater = repeaterValue.text.toString().toInt(),
                        frontDelay = frontDelayValue.text.toString().toInt(),
                        rearDelay = rearDelayValue.text.toString().toInt()
                    )
                )
            }
        }
        binding.columnSet.lockButton.setOnClickListener { viewModel.clickLock() }
        binding.columnSet.settingsButton.setOnClickListener { viewModel.clickTimings() }
        binding.columnSet.cautionButton.setOnClickListener { viewModel.clickCaution() }
        binding.bluetoothSign.setOnClickListener { viewModel.reScan() }
        with(binding.commandsBlock) {
            glass.setOnClickListener { viewModel.clickLock() }
            frontCam.setOnClickListener { viewModel.clickFrontCam() }
            rearUnlockedCam.setOnClickListener { viewModel.clickRearCam() }
            leftFog.setOnClickListener { viewModel.clickLeftFog() }
            rightFog.setOnClickListener { viewModel.clickRightFog() }
            rightAngelEye.setOnClickListener { viewModel.clickRightAngelEye() }
            rearCamLeftAngelEye.setOnClickListener { viewModel.clickLeftAngelEye() }
        }
    }

    private fun renderMainState(mainState: MainState) {
        renderShifts(mainState.deviceState)
        renderBluetoothSign(mainState.deviceState)
        renderCommandSet(mainState.deviceState, mainState.isLocked)
        renderLock(mainState.isLocked)
        renderTimingSettingsAndLogView(mainState)
    }

    private fun scrollLogView() {
        lifecycleScope.launch(Dispatchers.IO) {
            delay(PRESCROLL_DELAY)
            with(binding.LogView) {
                val scrollAmount =
                    measuredHeight - lineHeight * (lineCount + LOG_VIEW_SCROLL_CORRECTION)
                if (scrollAmount < 0) {
                    scrollTo(0, scrollAmount * -1)
                }
            }
        }
    }

    private fun renderTimingSettingsAndLogView(mainState: MainState) {
        if (mainState.isSetTimings) {
            binding.timingsSet.root.isGone = false
            binding.LogView.isGone = true
            with(binding.timingsSet) {
                renderTimingValueAndHelper(
                    bounceValue,
                    bounceBlue,
                    mainState.deviceState.timings.bounce
                )
                renderTimingValueAndHelper(
                    repeaterValue,
                    repeaterBlue,
                    mainState.deviceState.timings.repeater
                )
                renderTimingValueAndHelper(
                    frontDelayValue,
                    frontBlue,
                    mainState.deviceState.timings.frontDelay
                )
                renderTimingValueAndHelper(
                    rearDelayValue,
                    rearBlue,
                    mainState.deviceState.timings.rearDelay
                )
            }
            timingsToSendCheck()
        } else {
            binding.LogView.isGone = false
            binding.timingsSet.root.isGone = true
            scrollLogView()
        }
    }

    private fun renderTimingValueAndHelper(editText: EditText, helper: TextView, number: Int) {
        if (number != -1) {
            editText.setText(number.toString())
            helper.text = number.toString()
        } else {
            editText.text.clear()
            helper.text = getString(R.string.not_gotten)
        }
    }

    private fun renderLock(isLocked: Boolean) {
        with(binding.commandsBlock) {
            backCamBack.isVisible = isLocked
            backCamText.isVisible = isLocked
            glass.isVisible = isLocked
            frontCamBack.isVisible = isLocked
            frontCamText.isVisible = isLocked
            rightAngelEye.isVisible = !isLocked
            rearUnlockedCam.isVisible = !isLocked
        }
        binding.columnSet.lockButton.setImageDrawable(
            AppCompatResources.getDrawable(
                this@MainActivity,
                if (isLocked) R.drawable.lock else R.drawable.unlock
            )
        )
    }

    private fun renderCommandSet(state: DeviceState, isLocked: Boolean) {
        with(binding.commandsBlock) {
            if (isLocked) {
                backCamText.isVisible = state.rightAngelEyeIsOn
                frontCamText.isVisible = state.frontCameraIsShown
                rearCamLeftAngelEye.setImageDrawable(
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.camera_void)
                )
                rearCamLeftAngelEye.rotation = 90f
                frontCam.setBackgroundDrawable(
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.camera_void)
                )
                backCamBack.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        this@MainActivity,
                        if (state.rearCameraIsShown) R.drawable.camera_back_back_on else R.drawable.camera_back_back
                    )
                )
                frontCamBack.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        this@MainActivity,
                        if (state.frontCameraIsShown) R.drawable.camera_back_back_on else R.drawable.camera_back_back
                    )
                )
            } else { // unlocked
                rearUnlockedCam.setBackgroundDrawable(
                    AppCompatResources.getDrawable(
                        this@MainActivity,
                        if (state.rearCameraIsShown) R.drawable.camera_on else R.drawable.camera_void
                    )
                )
                rearCamLeftAngelEye.setImageDrawable(
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.wing)
                )
                rearCamLeftAngelEye.rotation = 0f
                rightAngelEye.isActivated = state.rightAngelEyeIsOn
                rearCamLeftAngelEye.isActivated = state.leftAngelEyeIsOn
            }
            // lock independent
            frontCam.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.frontCameraIsShown) R.drawable.camera_on else R.drawable.camera_void
                )
            )
            leftFog.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.leftFogIsOn) R.drawable.fog_lamp_on else R.drawable.fog_lamp
                )
            )
            rightFog.setBackgroundDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.rightFogIsOn) R.drawable.fog_lamp_on else R.drawable.fog_lamp
                )
            )
        }
        // Column set Caution is Here
        binding.columnSet.cautionButton.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this@MainActivity,
                if (state.cautionIsOn) R.drawable.caution_sign_on else R.drawable.caution_sign
            )
        )
    }

    private fun renderBluetoothSign(state: DeviceState) {
        binding.bluetoothSign.setImageDrawable(
            AppCompatResources.getDrawable(
                this,
                when (state.connectionState) {
                    DeviceState.ConnectionState.NOT_CONNECTED -> R.drawable.b_disconnected
                    DeviceState.ConnectionState.SCANNING -> R.drawable.b_scaning
                    DeviceState.ConnectionState.CONNECTED -> R.drawable.b_connected
                    DeviceState.ConnectionState.CONNECTED_NOTIFIED -> R.drawable.b_notified
                }
            )
        )
    }

    private fun renderShifts(state: DeviceState) {
        with(binding.shifts) {
            leftClickImg.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.leftPressed) R.drawable.turn_arrow_is_on else R.drawable.turn_arrow
                )
            )
            leftDblClickImg.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.leftDblPressed) R.drawable.dbl_click_is_on else R.drawable.dbl_click
                )
            )
            rightClickImg.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.rightPressed) R.drawable.turn_arrow_is_on else R.drawable.turn_arrow
                )
            )
            rightDblClickImg.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.rightDblPressed) R.drawable.dbl_click_is_on else R.drawable.dbl_click
                )
            )
            reverseImg.setImageDrawable(
                AppCompatResources.getDrawable(
                    this@MainActivity,
                    if (state.reversePressed) R.drawable.reverse_is_on else R.drawable.reverse
                )
            )
        }
    }
}