import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:ola_map_flutter/utils/permission_location.dart';
import 'package:screenshot/screenshot.dart';

import 'ola_map_flutter_platform_interface.dart';

/// An implementation of [OlaMapFlutterPlatform] that uses method channels.
class MethodChannelOlaMapFlutter extends OlaMapFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  var methodChannel = const MethodChannel('ola_map_flutter');

  @override
  Future<void> initializeMap(String apiKey) async {
    try {
      await methodChannel.invokeMethod('initializeMap', {'apiKey': apiKey});
    } on PlatformException catch (e) {
      throw Exception("Failed to initialize map: '${e.message}'");
    }
  }

  @override
  Future<void> addMarker(double latitude, double longitude) async {
    try {
      await methodChannel.invokeMethod('addMarker', {'latitude': latitude, 'longitude': longitude});
    } on PlatformException catch (e) {
      throw Exception("Failed to add marker: '${e.message}'.");
    }
  }

  @override
  Future<void> clearMarkers() async {
    try {
      await methodChannel.invokeMethod('clearMarkers');
    } on PlatformException catch (e) {
      throw Exception("Failed to clear markers: '${e.message}'.");
    }
  }

  @override
  Future<dynamic> getCurrentLocation() async {
    try {
      // await checkLocationPermission();
      var ds = await methodChannel.invokeMethod('getCurrentLocation');
      return ds;
    } on PlatformException catch (e) {
      throw Exception("Failed to get current location: '${e.message}'.");
    }
  }

  @override
  Future<void> showCurrentLocation() async {
    try {
      await checkLocationPermission();
      await methodChannel.invokeMethod('showCurrentLocation');
    } on PlatformException catch (e) {
      throw Exception("Failed to show current location: '${e.message}'.");
    }
  }

  @override
  Future<void> hideCurrentLocation() async {
    try {
      await methodChannel.invokeMethod('hideCurrentLocation');
    } on PlatformException catch (e) {
      throw Exception("Failed to hide current location: '${e.message}'.");
    }
  }

  // zoomin
  @override
  Future<void> zoomIn() async {
    try {
      await methodChannel.invokeMethod('zoomIn');
    } on PlatformException catch (e) {
      throw Exception("Failed to zoom in: '${e.message}'.");
    }
  }

  // zoomout
  @override
  Future<void> zoomOut() async {
    try {
      await methodChannel.invokeMethod('zoomOut');
    } on PlatformException catch (e) {
      throw Exception("Failed to zoom out: '${e.message}'.");
    }
  }

//zoom
  @override
  Future<void> zoomTo({required double zoom}) async {
    try {
      await methodChannel.invokeMethod('zoomTo', {'value': zoom});
    } on PlatformException catch (e) {
      throw Exception("Failed to zoom to: '${e.message}'.");
    }
  }

  @override
  Future<void> moveToCurrentLocation() async {
    try {
      var currentlocation = await checkLocationPermission();

      if (currentlocation == null) {
        throw Exception("Failed to get current location: 'Location not found'.");
      }
      await methodChannel.invokeMethod('moveToCurrentLocation', {'latitude': currentlocation.latitude, 'longitude': currentlocation.longitude});
    } on PlatformException catch (e) {
      throw Exception("Failed to move to current location: '${e.message}'.");
    }
  }

  @override
  Future<void> moveCameraToLocation({required double latitude, required double longitude}) async {
    try {
      await methodChannel.invokeMethod('moveCameraToLocation', {'latitude': latitude, 'longitude': longitude});
    } on PlatformException catch (e) {
      throw Exception("Failed to move camera to location: '${e.message}'.");
    }
  }

  @override
  Future<void> addCustomMarker({
    required Widget child,
    required double latitude,
    required double longitude,
    required String markerId,
    bool? setIsIconClickable = false,
    bool? setIsAnimationEnable = false,
    bool? setIsInfoWindowDismissOnClick = false,
  }) async {
    try {
      ScreenshotController screenshotController = ScreenshotController();
      var capturedImage = await screenshotController.captureFromWidget(child);
      await Future.delayed(Duration(seconds: 1));

      var bytes = capturedImage.buffer.asUint8List();

      await methodChannel.invokeMethod('addMarker', {
        'markerId': markerId,
        'latitude': latitude,
        'longitude': longitude,
        'imageBytes': bytes,
        "setIsIconClickable": setIsIconClickable,
        "setIsAnimationEnable": setIsAnimationEnable,
        "setIsInfoWindowDismissOnClick": setIsInfoWindowDismissOnClick,
      });
    } on PlatformException catch (e) {
      throw Exception("Failed to add custom marker: '${e.message}'.");
    }
  }

  @override
  Future<void> removeMarker({required String markerId}) async {
    try {
      await methodChannel.invokeMethod('removeMarker', {'markerId': markerId});
    } on PlatformException catch (e) {
      throw Exception("Failed to remove marker: '${e.message}'.");
    }
  }

  @override
  Future<void> resetRotation() async {
    try {
      await methodChannel.invokeMethod('resetRotation');
    } on PlatformException catch (e) {
      throw Exception("Failed to reset rotation: '${e.message}'.");
    }
  }

  @override
  void setChannelName(MethodChannel channel) {
    methodChannel = channel;
  }

  @override
  Future<void> handleMethod(MethodCall call) async {
    switch (call.method) {
      case "onError":
        // String error = call.arguments;
        // print("Error: $error");
        // Handle the error
        break;
      case "onMapReady":
        // Handle map ready state
        break;
    }
  }
}
