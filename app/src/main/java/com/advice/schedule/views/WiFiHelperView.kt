package com.shortstack.hackertracker.views

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.AuthAlgorithm
import android.net.wifi.WifiConfiguration.GroupCipher
import android.net.wifi.WifiConfiguration.KeyMgmt
import android.net.wifi.WifiConfiguration.PairwiseCipher
import android.net.wifi.WifiConfiguration.Protocol
import android.net.wifi.WifiConfiguration.Status
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ViewWifiHelperBinding
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class WiFiHelperView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewWifiHelperBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.save.visibility = View.VISIBLE
        binding.save.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                connectWifiNew()
            } else {
                connectWifi()
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.Q)
    private fun connectWifiNew() {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val network = WifiNetworkSuggestion.Builder()
            .setSsid("\"DefCon\"")
            .setIsHiddenSsid(false)
            .setPriority(40)
            .setWpa2EnterpriseConfig(getEnterpriseConfig())
            .build()

        val suggestions = listOf(network)

        val status = wifi.addNetworkSuggestions(suggestions)

        if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            // do error handling here
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectWifi() {
        val wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifi.isWifiEnabled = true

        val exists = wifi.configuredNetworks.find { it.SSID == "\"DefCon\"" } != null

        val config = WifiConfiguration().apply {
            SSID = "\"DefCon\""
            hiddenSSID = false
            priority = 40
            status = Status.ENABLED

            allowedKeyManagement.clear()
            allowedKeyManagement.set(KeyMgmt.WPA_EAP)

            allowedGroupCiphers.clear()
            allowedGroupCiphers.set(GroupCipher.CCMP)
            allowedGroupCiphers.set(GroupCipher.TKIP)
            allowedGroupCiphers.set(GroupCipher.WEP104)

            allowedPairwiseCiphers.clear()
            allowedPairwiseCiphers.set(PairwiseCipher.CCMP)

            allowedAuthAlgorithms.clear()
            allowedAuthAlgorithms.set(AuthAlgorithm.OPEN)

            allowedProtocols.clear()
            allowedProtocols.set(Protocol.RSN)

            enterpriseConfig = getEnterpriseConfig()
        }

        val network = if (exists) {
            wifi.updateNetwork(config)
        } else {
            wifi.addNetwork(config)
        }

        val result = wifi.enableNetwork(network, true)
        if (result) {
            Toast.makeText(context, "Wifi network saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Could not save network", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getEnterpriseConfig(): WifiEnterpriseConfig {
        val x509Certificate = CertificateFactory.getInstance("X.509")
            .generateCertificate(resources.openRawResource(R.raw.digirootonly)) as X509Certificate

        return WifiEnterpriseConfig().apply {
            identity = "defcon"
            password = "defcon"
            anonymousIdentity = "anonymous"
            eapMethod = WifiEnterpriseConfig.Eap.PEAP
            phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
            caCertificate = x509Certificate
            subjectMatch = "/CN=wifireg.defcon.org"
        }
    }
}