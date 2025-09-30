package com.ola.map.flutter.ola_map_flutter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import com.ola.mapsdk.camera.MapControlSettings
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.ola.mapsdk.interfaces.OlaMapCallback
import com.ola.mapsdk.model.OlaLatLng
import com.ola.mapsdk.model.OlaMarkerOptions
import com.ola.mapsdk.view.OlaMapView
import com.ola.mapsdk.view.OlaMap
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory
import java.io.ByteArrayInputStream

/** OlaMapFlutterPlugin */
class OlaMapFlutterPlugin : FlutterPlugin, MethodCallHandler {

  private lateinit var channel: MethodChannel
  private lateinit var context: Context

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "ola_map_flutter")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
    flutterPluginBinding.platformViewRegistry.registerViewFactory(
      "OlaMapView", OlaMapViewFactory(flutterPluginBinding.binaryMessenger))
  }

  override fun onMethodCall(call: MethodCall, result: Result) {
    Log.d("Erro", call.method);
    when (call.method) {
      "getPlatformVersion" -> {
        result.success("Android ${android.os.Build.VERSION.RELEASE}")
      }

      else -> result.notImplemented()
    }
  }



  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}

class OlaMapViewFactory(private val messenger: BinaryMessenger) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
  override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
    val creationParams = args as? Map<String, Any>
    return OlaMapViewController(context, messenger, viewId, creationParams)
  }
}

class OlaMapViewController(
  private val context: Context,
  messenger: BinaryMessenger,
  viewId: Int,
  creationParams: Map<String, Any>?
) : PlatformView {

  private val mapView: OlaMapView = OlaMapView(context)
  private val methodChannel: MethodChannel = MethodChannel(messenger, "ola_map_flutter_$viewId")
  private var olaMap: OlaMap? = null
  init {
    Log.d("OlaMapViewController", "OlaMapView initialized with viewId: $viewId")

    val apiKey = creationParams?.get("apiKey") as? String
    val showCompass = creationParams?.get("showCompass") as? Boolean ?: false
    val showPOI = creationParams?.get("showPOI") as? Boolean ?: false
    
    if (apiKey.isNullOrEmpty()) {
      // Return error if apiKey is missing or invalid
      methodChannel.invokeMethod("onError", "API key is missing or invalid")

    }else {
      initializeMap(apiKey, showCompass, showPOI);
    }

    methodChannel.setMethodCallHandler { call, result ->
      Log.d("Erro", call.method);
      when (call.method) {
        "getCurrentLocation" -> {
          getCurrentLocation(result)
        }
        "showCurrentLocation" -> {
          showCurrentLocation(result)
        }
        "hideCurrentLocation" -> {
          hideCurrentLocation(result)
        }
        "zoomIn" -> {
          zoomIn(result)
        }
        "zoomOut" -> {
          zoomOut(result)
        }
        "zoomTo" -> {
          zoom(call,result)
        }
        "moveToCurrentLocation" -> {
          moveToSpecifiedLocation(call,result)
        }
        "addMarker" -> {
          addMarker(call,result)
        }
        "removeMarker" -> {
          removeMarker(call, result)
        }
        "resetRotation" -> {
          resetRotation(result)
        }
        else -> {
          Log.e("OlaMapViewController", "Unknown method called: ${call.method}")
          result.notImplemented()
        }
      }
    }


  }
  private fun initializeMap(apiKey: String, showCompass: Boolean, showPOI: Boolean) {
    try {

      Log.d("OlaMapViewController", "API Key is valid, initializing map...")
      val mapControlSettings = MapControlSettings.Builder()
        .setRotateGesturesEnabled(true)
        .setScrollGesturesEnabled(true)
        .setZoomGesturesEnabled(true)
        .setCompassEnabled(showCompass)
        .setTiltGesturesEnabled(true)
        .setDoubleTapGesturesEnabled(true)
        .build()
      mapView.getMap(apiKey, object : OlaMapCallback {
        override fun onMapReady(map: OlaMap) {
          olaMap = map
          // Disable POI if requested
          if (!showPOI) {
            // Note: The actual method to disable POI depends on the Ola Maps SDK API
            // This is a placeholder - you may need to adjust based on actual SDK methods
            // olaMap?.setPoiEnabled(false) or similar
          }
          methodChannel.invokeMethod("onMapReady", null)
          Log.d("OlaMapFlutterPlugin", "Map initialized successfully")
        }

        override fun onMapError(error: String) {
          methodChannel.invokeMethod("onError", error)
//                result.error("MAP_ERROR", error, null)
          Log.e("OlaMapFlutterPlugin", "Map initialization error: $error")
        }
      },
        mapControlSettings
      )

    } catch (e: Exception) {
      Log.e("OlaMapViewController", "Exception during map initialization: ${e.message}", e)
      methodChannel.invokeMethod("onError",  e.message)
    } finally {
      Log.d("OlaMapViewController", "Finally block executed after map initialization")
    }
  }

  private fun getCurrentLocation(result: MethodChannel.Result) {
    // Ensure we're running on the main thread
//    if (context is Activity) {
    olaMap?.showCurrentLocation()
    val activityContext = context as? Activity
    activityContext?.runOnUiThread {
      if (olaMap == null) {
        result.error("LOCATION_ERROR", "OlaMap instance is not initialized", null)
        return@runOnUiThread
      }

      val currentLocation: OlaLatLng? = olaMap?.getCurrentLocation()
      if (currentLocation != null) {
        Log.d("OlaMapViewController", "Current location: $currentLocation")
        val locationMap = mapOf(
          "latitude" to currentLocation.latitude,
          "longitude" to currentLocation.longitude
        )
        result.success(locationMap)
      } else {
        result.error("LOCATION_ERROR", "Current location is not available", null)
      }
    }

  }

  private fun showCurrentLocation(result: MethodChannel.Result) {
//    val activityContext = context as? Activity
//    activityContext?.runOnUiThread {
//      if (olaMap == null) {
//        result.error("LOCATION_ERROR", "OlaMap instance is not initialized", null)
//        return@runOnUiThread
//      }
    olaMap?.showCurrentLocation()
    result.success("Current location shown")
//    }
  }

  private fun hideCurrentLocation(result: MethodChannel.Result) {

    val activityContext = context as? Activity
    activityContext?.runOnUiThread {
      if (olaMap == null) {
        result.error("LOCATION_ERROR", "OlaMap instance is not initialized", null)
        return@runOnUiThread
      }
      olaMap?.hideCurrentLocation()
      result.success("Current location hidden")
    }
  }

  // Function to zoom in the map
  private fun zoomIn(result: Result) {
    val currentCameraPosition = olaMap?.getCurrentOlaCameraPosition()

    // Check if the current camera position is not null
    if (currentCameraPosition != null) {
      val targetLocation = currentCameraPosition.target
      val currentZoomLevel = currentCameraPosition.zoomLevel

      // Check if targetLocation and currentZoomLevel are not null
      if (targetLocation != null) {
        // Zoom in by increasing the zoom level
        olaMap?.zoomToLocation(targetLocation, currentZoomLevel + 1.0)
      } else {
        result.error("LOCATION_ERROR", "Failed to get target location or zoom level", null)

      }
    } else {
      result.error("LOCATION_ERROR", "Failed to get current camera position", null)
    }
  }

  // Function to zoom in the map
  private fun zoom(call: MethodCall, result: Result) {
    val value = call.argument<Double>("value")
    val currentCameraPosition = olaMap?.getCurrentOlaCameraPosition()

    // Check if the current camera position is not null
    if (currentCameraPosition != null) {
      val targetLocation = currentCameraPosition.target
      val currentZoomLevel = currentCameraPosition.zoomLevel

      // Check if targetLocation and currentZoomLevel are not null
      if (targetLocation != null && value != null) {
        // Zoom in by increasing the zoom level
        olaMap?.zoomToLocation(targetLocation, value)
        result.success(null)
      } else {
        result.error("LOCATION_ERROR", "Failed to get target location or zoom level", null)

      }
    } else {
      result.error("LOCATION_ERROR", "Failed to get current camera position", null)
    }
  }

  // Function to zoom out the map
  private fun zoomOut(result: Result) {
    val currentCameraPosition = olaMap?.getCurrentOlaCameraPosition()

    // Check if the current camera position is not null
    if (currentCameraPosition != null) {
      val targetLocation = currentCameraPosition.target
      val currentZoomLevel = currentCameraPosition.zoomLevel

      // Check if targetLocation and currentZoomLevel are not null
      if (targetLocation != null) {
        // Zoom out by decreasing the zoom level
        olaMap?.zoomToLocation(targetLocation, currentZoomLevel - 1.0)
      } else {
        result.error("LOCATION_ERROR", "Failed to get target location or zoom level", null)
      }
    } else {
      result.error("LOCATION_ERROR", "Failed to get current camera position", null)
    }
  }

  private fun moveToSpecifiedLocation(call: MethodCall, result: Result) {
    val latitude = call.argument<Double>("latitude")
    val longitude = call.argument<Double>("longitude")
    olaMap?.getCurrentLocation();
    if (latitude != null && longitude != null) {
      val location = OlaLatLng(latitude, longitude)
      val zoomLevel = 15.0 // Set the desired zoom level
      olaMap?.moveCameraToLatLong(location, zoomLevel)
      result.success(null)
    } else {
      result.error("LOCATION_ERROR", "Latitude or Longitude is missing or invalid", null)
    }
  }

  private fun addMarker(call: MethodCall, result: Result) {
    val markerId = call.argument<String>("markerId")
    val latitude = call.argument<Double>("latitude")
    val longitude = call.argument<Double>("longitude")
    val imageBytes = call.argument<ByteArray>("imageBytes") // Get the image bytes
    val setIsIconClickable = call.argument<Boolean>("setIsIconClickable")
    val  setIsAnimationEnable = call.argument<Boolean>("setIsAnimationEnable")
    val setIsInfoWindowDismissOnClick  = call.argument<Boolean>("setIsInfoWindowDismissOnClick")

    if (markerId != null && latitude != null && longitude != null && imageBytes != null) {
      val markerOptionsBuilder = OlaMarkerOptions.Builder()
        .setMarkerId(markerId)
        .setPosition(OlaLatLng(latitude, longitude))
        .setIsIconClickable(true)
        .setIconRotation(0f)
        .setIsAnimationEnable(true)
        .setIsInfoWindowDismissOnClick(true)

      // Decode the image bytes and set as marker icon
      val bitmap = BitmapFactory.decodeStream(ByteArrayInputStream(imageBytes))
      markerOptionsBuilder.setIconBitmap(bitmap)

      olaMap?.addMarker(markerOptionsBuilder.build())
      result.success(null)
    } else {
      result.error("INVALID_ARGUMENTS", "Marker ID, latitude, longitude, or imageBytes missing", null)
    }
  }

  private fun removeMarker(call: MethodCall, result: Result) {
    val markerId = call.argument<String>("markerId")

    val markerOptionsBuilder = OlaMarkerOptions.Builder()
      .setMarkerId(markerId ?: "").build()
    val marker1 = olaMap?.addMarker(markerOptionsBuilder)
    marker1?.removeMarker();

  }

  private fun resetRotation(result: Result) {
    val currentCameraPosition = olaMap?.getCurrentOlaCameraPosition()
    
    if (currentCameraPosition != null) {
      val targetLocation = currentCameraPosition.target
      val currentZoomLevel = currentCameraPosition.zoomLevel
      
      if (targetLocation != null) {
        // Reset bearing to 0 (north)
        olaMap?.moveCameraToLatLong(targetLocation, currentZoomLevel)
        result.success(null)
      } else {
        result.error("ROTATION_ERROR", "Failed to get target location", null)
      }
    } else {
      result.error("ROTATION_ERROR", "Failed to get current camera position", null)
    }
  }

  override fun getView(): View {
    Log.d("OlaMapViewController", "Returning mapView")
    return mapView
  }

  override fun dispose() {
    Log.d("OlaMapViewController", "Disposing OlaMapView")
    // Cleanup resources if necessary
  }
}