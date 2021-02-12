package com.shortstack.hackertracker.views

import android.annotation.TargetApi
import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.*
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_wifi_helper.view.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class WiFiHelperView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        inflate(context, R.layout.view_wifi_helper, this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            save.visibility = View.VISIBLE
            save.setOnClickListener { connectWifi() }
        } else {
            content.text = context.getString(R.string.wifi_content_legacy)
            save.visibility = View.GONE
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
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


            val x509Certificate = CertificateFactory.getInstance("X.509").generateCertificate(resources.openRawResource(R.raw.digirootonly)) as X509Certificate

            enterpriseConfig = WifiEnterpriseConfig().apply {
                identity = "defcon"
                password = "defcon"
                anonymousIdentity = "anonymous"
                eapMethod = WifiEnterpriseConfig.Eap.PEAP
                phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
                caCertificate = x509Certificate
                subjectMatch = "/CN=wifireg.defcon.org"
            }
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
}