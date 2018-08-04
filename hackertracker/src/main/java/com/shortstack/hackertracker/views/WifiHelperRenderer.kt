package com.shortstack.hackertracker.views

import android.content.Context
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

/**
 * Created by Chris on 6/29/2018.
 */
class WifiHelperRenderer : Renderer<RendererContent<Void>>() {

    companion object {
        private const val INSTALL_KEYSTORE_CODE = 1001
    }

    override fun inflate(inflater: LayoutInflater, parent: ViewGroup): View {
        return inflater.inflate(R.layout.wifi_item, parent, false)
    }

    override fun render(payloads: MutableList<Any>?) {

        rootView.connect
    }

    override fun hookListeners(rootView: View?) {
        rootView?.connect?.setOnClickListener { connectWifi() }
        rootView?.install?.setOnClickListener { startInstallCert() }
    }

    private fun connectWifi() {
        val wifi = getWifiManager()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val config = WifiConfiguration().apply {
                SSID = "DefCon"
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP)
                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X)

                enterpriseConfig = WifiEnterpriseConfig().apply {
                    identity = "defcon"
                    password = "defcon"
                    eapMethod = WifiEnterpriseConfig.Eap.PEAP
                    phase2Method = WifiEnterpriseConfig.Phase2.MSCHAPV2
                }
            }

            val network = wifi.addNetwork(config)
            wifi.enableNetwork(network, true)
        } else {
            Toast.makeText(context, "Android version too low to to connect", Toast.LENGTH_SHORT).show()
        }
    }


    private fun startInstallCert() {
        val stream = context?.assets?.open("digicertca.cer")
        if (stream != null) {
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()

            val intent = KeyChain.createInstallIntent().also {
                it.putExtra(KeyChain.EXTRA_CERTIFICATE, buffer)
                it.putExtra(KeyChain.EXTRA_NAME, "Digicert DefCon CA")
            }

            (context as? AppCompatActivity)?.startActivityForResult(intent, INSTALL_KEYSTORE_CODE)
        }
    }


    private fun getWifiManager() = App.application.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
}