import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ola_map_flutter_method_channel.dart';

abstract class OlaMapFlutterPlatform extends PlatformInterface {
  /// Constructs a OlaMapFlutterPlatform.
  OlaMapFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static OlaMapFlutterPlatform _instance = MethodChannelOlaMapFlutter();

  /// The default instance of [OlaMapFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelOlaMapFlutter].
  static OlaMapFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [OlaMapFlutterPlatform] when
  /// they register themselves.
  static set instance(OlaMapFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initializeMap(String apiKey) {
    throw UnimplementedError('initializeMap() has not been implemented.');
  }

  Future<void> addMarker(double latitude, double longitude) {
    throw UnimplementedError('addMarker() has not been implemented.');
  }

  Future<void> clearMarkers() {
    throw UnimplementedError('clearMarkers() has not been implemented.');
  }

  Future<dynamic> getCurrentLocation() {
    throw UnimplementedError('getCurrentLocation() has not been implemented.');
  }

  Future<void> showCurrentLocation() {
    throw UnimplementedError('showCurrentLocation() has not been implemented.');
  }

  Future<void> hideCurrentLocation() {
    throw UnimplementedError('hideCurrentLocation() has not been implemented.');
  }

  Future<void> checkLocationPermission() {
    throw UnimplementedError(
        'checkLocationPermission() has not been implemented.');
  }

  Future<void> requestLocationPermission() {
    throw UnimplementedError(
        'requestLocationPermission() has not been implemented.');
  }

  Future<void> zoomIn() {
    throw UnimplementedError('zoomIn() has not been implemented.');
  }

  Future<void> zoomOut() {
    throw UnimplementedError('zoomOut() has not been implemented.');
  }

  Future<void> zoomTo({required double zoom}) {
    throw UnimplementedError('zoomTo() has not been implemented.');
  }

  Future<void> moveToCurrentLocation() {
    throw UnimplementedError(
        'moveToCurrentLocation() has not been implemented.');
  }

  Future<void> moveCameraToLocation({required double latitude, required double longitude}) {
    throw UnimplementedError(
        'moveCameraToLocation() has not been implemented.');
  }

  Future<void> addCustomMarker({
    required Widget child,
    required double latitude,
    required double longitude,
    required String markerId,
    bool? setIsIconClickable = false,
    bool? setIsAnimationEnable = false,
    bool? setIsInfoWindowDismissOnClick = false,
  }) {
    throw UnimplementedError('addCustomMarker() has not been implemented.');
  }

  Future<void> removeMarker({required String markerId}) {
    throw UnimplementedError('removeCustomMarker() has not been implemented.');
  }

  Future<void> resetRotation() {
    throw UnimplementedError('resetRotation() has not been implemented.');
  }

  Future<void> handleMethod(MethodCall call) {
    throw UnimplementedError('handleMethod() has not been implemented.');
  }

  void setChannelName(MethodChannel channel) {
    throw UnimplementedError('setChannel() has not been implemented.');
  }
}
