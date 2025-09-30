import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'ola_map_flutter_platform_interface.dart';
export 'package:ola_map_flutter/widget/ola_map.dart';

class OlaMapController {
  final int id;
  final MethodChannel _channel;

  OlaMapController(this.id) : _channel = MethodChannel('ola_map_flutter_$id') {
    OlaMapFlutterPlatform.instance.setChannelName(_channel);
    _channel.setMethodCallHandler(OlaMapFlutterPlatform.instance.handleMethod);
  }

  Future<void> initializeMap(String apiKey) {
    return OlaMapFlutterPlatform.instance.initializeMap(apiKey);
  }

  Future<void> addMarker(double latitude, double longitude) {
    return OlaMapFlutterPlatform.instance.addMarker(latitude, longitude);
  }

  Future<void> clearMarkers() {
    return OlaMapFlutterPlatform.instance.clearMarkers();
  }

  Future<dynamic> getCurrentLocation() {
    return OlaMapFlutterPlatform.instance.getCurrentLocation();
  }

  Future<void> showCurrentLocation() {
    return OlaMapFlutterPlatform.instance.showCurrentLocation();
  }

  Future<void> hideCurrentLocation() {
    return OlaMapFlutterPlatform.instance.hideCurrentLocation();
  }

  Future<void> checkLocationPermission() {
    return OlaMapFlutterPlatform.instance.checkLocationPermission();
  }

  Future<void> requestLocationPermission() {
    return OlaMapFlutterPlatform.instance.requestLocationPermission();
  }

  Future<void> zoomIn() {
    return OlaMapFlutterPlatform.instance.zoomIn();
  }

  Future<void> zoomOut() {
    return OlaMapFlutterPlatform.instance.zoomOut();
  }

  Future<void> zoomTo({required double zoom}) {
    return OlaMapFlutterPlatform.instance.zoomTo(zoom: zoom);
  }

  Future<void> moveToCurrentLocation() {
    return OlaMapFlutterPlatform.instance.moveToCurrentLocation();
  }

  Future<void> moveCameraToLocation({required double latitude, required double longitude}) {
    return OlaMapFlutterPlatform.instance.moveCameraToLocation(latitude: latitude, longitude: longitude);
  }

  Future<void> addCustomMarker({
    required Widget child,
    required double latitude,
    required double longitude,
    required String markerId,
  }) {
    return OlaMapFlutterPlatform.instance.addCustomMarker(
      child: child,
      latitude: latitude,
      longitude: longitude,
      markerId: markerId,
    );
  }

  Future<void> resetRotation() {
    return OlaMapFlutterPlatform.instance.resetRotation();
  }
}
