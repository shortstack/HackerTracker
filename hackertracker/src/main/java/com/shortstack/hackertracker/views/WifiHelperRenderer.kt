package com.shortstack.hackertracker.views

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.Build
import android.security.KeyChain
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pedrogomez.renderers.Renderer
import com.pedrogomez.renderers.RendererContent
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import kotlinx.android.synthetic.main.wifi_item.view.*
import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * Created by Chris on 6/29/2018.
 */
class WifiHelperRenderer : Renderer<RendererContent<Void>>() {

    companion object {
        private const val INSTALL_KEYSTORE_CODE = 1001
        private const val WIFI_URL = "https://wifireg.defcon.org/android.php"
    }

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.wifi_item, parent, false)
    }

    override fun render(payloads: MutableList<Any>?) {


    }

    override fun hookListeners(rootView: View?) {
        rootView?.install?.setOnClickListener { startInstallCert() }
        rootView?.connect?.setOnClickListener { connectWifi() }
        rootView?.url?.setOnClickListener { openUrl() }
    }


    private fun startInstallCert() {
        val data = getCertificateData()

        if (data == null) {
            Toast.makeText(context, "Could not open cert.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = KeyChain.createInstallIntent().also {
            it.putExtra(KeyChain.EXTRA_CERTIFICATE, data)
            it.putExtra(KeyChain.EXTRA_NAME, "Digicert DefCon CA")
        }

        (context as? AppCompatActivity)?.startActivityForResult(intent, INSTALL_KEYSTORE_CODE)
    }

    private fun connectWifi() {
        val wifi = getWifiManager()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

            val certificate = getCertificate()


            val config = WifiConfiguration().apply {
                SSID = "DefCon"
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X)

                enterpriseConfig = WifiEnterpriseConfig().apply {
                    identity = "defcon"
                    password = "defcon"
                    eapMethod = WifiEnterpriseConfig.Eap.PEAP
                    phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
                    caCertificate = certificate
                }
            }

            val network = wifi.addNetwork(config)


            if (network == -1) {
                Toast.makeText(context, "Could not save WiFi", Toast.LENGTH_SHORT).show()
            } else {
                wifi.enableNetwork(network, true)
                Toast.makeText(context, "DEF CON WiFi added", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Android version too low to to connect", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openUrl() {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(WIFI_URL))
        context.startActivity(intent)
    }


    private fun getCertificate(): X509Certificate? {
        val cf = CertificateFactory.getInstance("X509")

        getCertificateData()?.let {
            return cf.generateCertificate(ByteArrayInputStream(it)) as X509Certificate
        }

        return null
    }

    private fun getCertificateData(): ByteArray? {
        val stream = context?.assets?.open("digicertca.cer")
        if (stream != null) {
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            return buffer

        }
        return null
    }


    private fun getWifiManager() = App.application.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
}