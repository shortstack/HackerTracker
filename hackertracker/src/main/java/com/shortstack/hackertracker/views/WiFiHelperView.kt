package com.shortstack.hackertracker.views

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiConfiguration.*
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.Toast
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.view_wifi_helper.view.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate


class WiFiHelperView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {
        private const val WIFI_URL = "https://wifireg.defcon.org/android.php"
    }

    init {
        inflate(context, R.layout.view_wifi_helper, this)

        save.setOnClickListener { connectWifi() }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun connectWifi() {
        val wifi = getWifiManager()

        wifi.isWifiEnabled = true

        val config = WifiConfiguration().apply {
            SSID = "\"DefCon\""
            hiddenSSID = false
            priority = 40
            status = 1

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
                altSubjectMatch = "/CN=wifireg.defcon.org"
            }
        }

        val network = wifi.addNetwork(config)


        val result = wifi.enableNetwork(network, true)
        Toast.makeText(context, result.toString(), Toast.LENGTH_SHORT).show()

    }

    private fun openUrl() {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(WIFI_URL))
        context.startActivity(intent)
    }

    private fun getWifiManager() = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
}