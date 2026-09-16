package com.nocomerciales.app

import android.app.role.RoleManager
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nocomerciales.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestRoleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshStatus()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonEnable.setOnClickListener { requestScreeningRole() }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
        refreshBlockedList()
    }

    private fun requestScreeningRole() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        if (roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            !roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            requestRoleLauncher.launch(intent)
        }
    }

    private fun refreshStatus() {
        val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
        val active = roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) &&
            roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)

        if (active) {
            binding.statusText.text = getString(R.string.status_active)
            binding.buttonEnable.isEnabled = false
            binding.buttonEnable.text = getString(R.string.button_active)
        } else {
            binding.statusText.text = getString(R.string.status_inactive)
            binding.buttonEnable.isEnabled = true
            binding.buttonEnable.text = getString(R.string.button_enable)
        }
    }

    private fun refreshBlockedList() {
        val calls = BlockedCallsStore.getBlockedCalls(this)
        binding.blockedListContainer.removeAllViews()

        if (calls.isEmpty()) {
            binding.emptyText.visibility = android.view.View.VISIBLE
            return
        }
        binding.emptyText.visibility = android.view.View.GONE

        val formatter = DateFormat.getDateFormat(this)
        val timeFormatter = DateFormat.getTimeFormat(this)
        for (call in calls) {
            val row = android.widget.TextView(this)
            val date = java.util.Date(call.timestamp)
            row.text = getString(
                R.string.blocked_entry,
                call.number,
                formatter.format(date),
                timeFormatter.format(date)
            )
            row.setPadding(0, 16, 0, 16)
            binding.blockedListContainer.addView(row)
        }
    }
}
