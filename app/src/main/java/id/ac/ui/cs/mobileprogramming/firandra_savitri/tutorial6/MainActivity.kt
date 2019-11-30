package id.ac.ui.cs.mobileprogramming.firandra_savitri.tutorial6

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(connectionReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        connect.setOnClickListener {
            val wifi = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (!wifi.isWifiEnabled) {
                wifi.isWifiEnabled = true
            }

            connectToNetworkWep("Wi-Fi Name", "password")
        }

        disconnect.setOnClickListener {
            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.disconnect()
            wifiManager.isWifiEnabled = false
        }
    }

    private val connectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val currNetworkInfo = intent.getParcelableExtra<NetworkInfo>(ConnectivityManager.EXTRA_NETWORK_INFO)

            if (currNetworkInfo!!.isConnected) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val wifiInfo = wifiManager.connectionInfo
                Toast.makeText(applicationContext, getString(R.string.connected_to, wifiInfo.ssid), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext, getString(R.string.disconnected), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun connectToNetworkWep(ssid: String, password: String): Boolean {
        try {

            val config = WifiConfiguration()
            config.SSID =
                "\"" + ssid + "\""

            config.preSharedKey = "\"" + password + "\""

            config.status = WifiConfiguration.Status.ENABLED
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)

            val wifiManager =
                this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(config)

            val list = wifiManager.configuredNetworks
            for (i in list) {
                if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                    wifiManager.disconnect()
                    wifiManager.enableNetwork(i.networkId, true)
                    wifiManager.reconnect()
                    break
                }
            }
            return true
        } catch (ex: Exception) {
            return false
        }
    }
}
