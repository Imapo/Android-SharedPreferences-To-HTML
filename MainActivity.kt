package com.example.sharedtohtml

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        
        // Настройка WebView
        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("file:///android_asset/Magic.html")
        
        // Передача функций в HTML код
        webView.addJavascriptInterface(WebAppInterface(this), "Android")
    }

    private inner class WebAppInterface(private val context: Context) {
        @JavascriptInterface
        fun getPreference(key: String, defaultValue: String): String {
            return try {
                Log.d("getPreference", "Fetching preference for key: $key")
                val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                // Проверяем, существует ли значение для ключа
                if (sharedPreferences.contains(key)) {
                    // Определяем тип данных и приводим его к строке
                    val value = when (sharedPreferences.all[key]) {
                        is Boolean -> sharedPreferences.getBoolean(key, defaultValue.toBoolean()).toString()
                        is Int -> sharedPreferences.getInt(key, defaultValue.toInt()).toString()
                        is Float -> sharedPreferences.getFloat(key, defaultValue.toFloat()).toString()
                        is Long -> sharedPreferences.getLong(key, defaultValue.toLong()).toString()
                        is String -> sharedPreferences.getString(key, defaultValue) ?: defaultValue
                        else -> defaultValue
                    }
                    Log.d("getPreference", "Returning value: $value")
                    value
                } else {
                    Log.d("getPreference", "Key not found. Returning default value: $defaultValue")
                    defaultValue
                }
            } catch (e: Exception) {
                Log.e("getPreference", "Error accessing preferences", e)
                defaultValue
            }
        }
    }
}
