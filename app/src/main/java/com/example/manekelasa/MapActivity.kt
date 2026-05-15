package com.example.manekelasa

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.manekelasa.BuildConfig
import com.example.manekelasa.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val query = intent.getStringExtra("QUERY") ?: "Bengaluru"
        val key = BuildConfig.LOCATIONIQ_KEY

        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        binding.webView.webViewClient = WebViewClient()
        binding.webView.loadDataWithBaseURL("https://locationiq.com", buildHtml(query, key), "text/html", "UTF-8", null)
    }

    private fun buildHtml(query: String, key: String) = """
        <!DOCTYPE html><html><head>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
        <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
        <style>body{margin:0}#map{width:100vw;height:100vh}
        #bar{position:absolute;top:10px;left:50%;transform:translateX(-50%);z-index:1000;
        background:white;padding:8px 12px;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,.3);
        display:flex;gap:8px;width:90%}
        #si{flex:1;border:1px solid #ccc;border-radius:4px;padding:6px;font-size:14px}
        #sb{background:#FF6B35;color:white;border:none;border-radius:4px;padding:6px 12px;font-size:14px}</style>
        </head><body>
        <div id="bar"><input id="si" type="text" value="${query.replace("\"","\\\"")}"/>
        <button id="sb" onclick="doSearch()">Go</button></div>
        <div id="map"></div>
        <script>
        var map=L.map('map').setView([12.9716,77.5946],12);
        L.tileLayer('https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=$key',
        {maxZoom:19,attribution:'© LocationIQ'}).addTo(map);
        var mk=null;
        function doSearch(){
          var q=document.getElementById('si').value;if(!q)return;
          fetch('https://us1.locationiq.com/v1/search?key=$key&q='+encodeURIComponent(q)+'&format=json&limit=1&countrycodes=in')
          .then(r=>r.json()).then(d=>{if(d&&d.length>0){
            var lat=parseFloat(d[0].lat),lon=parseFloat(d[0].lon);
            map.setView([lat,lon],15);if(mk)map.removeLayer(mk);
            mk=L.marker([lat,lon]).addTo(map).bindPopup('<b>'+d[0].display_name+'</b>').openPopup();
          }else alert('Not found');}).catch(()=>alert('Search failed'));
        }
        window.onload=function(){doSearch()};
        </script></body></html>
    """.trimIndent()

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
