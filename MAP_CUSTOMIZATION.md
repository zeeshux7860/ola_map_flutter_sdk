# Ola Map Customization

## Changes Made

### 1. Custom Compass Position (Bottom-Left)
- Added a custom compass widget positioned at bottom-left of the map
- The custom compass appears as a circular button with a navigation icon
- Clicking the compass resets the map rotation to north
- Controlled via `showCompass` parameter (default: true)

### 2. Disable Default POI Markers
- Added ability to hide default Point of Interest (POI) markers like stores, petrol stations, hotels
- Controlled via `showPOI` parameter (default: false)

## Usage

```dart
OlaMap(
  apiKey: "your_api_key",
  showCompass: true,     // Show custom compass in bottom-left
  showPOI: false,        // Hide default POI markers
  showCurrentLocation: true,
  showZoomControls: true,
  showMyLocationButton: true,
  onPlatformViewCreated: (OlaMapController controller) {
    // Your controller logic
  },
)
```

## Implementation Details

### Files Modified:

1. **lib/widget/ola_map.dart**
   - Added `showCompass` and `showPOI` parameters to OlaMap widget
   - Implemented custom compass widget positioned at bottom-left (bottom: 100, left: 20)
   - Passes compass and POI settings to native platform

2. **lib/ola_map_flutter.dart**
   - Added `resetRotation()` method to OlaMapController

3. **lib/ola_map_flutter_platform_interface.dart**
   - Added abstract `resetRotation()` method

4. **lib/ola_map_flutter_method_channel.dart**
   - Implemented `resetRotation()` method channel call

5. **android/src/main/kotlin/.../OlaMapFlutterPlugin.kt**
   - Updated to handle `showCompass` and `showPOI` creation parameters
   - Added `resetRotation` method to reset map bearing to north
   - Configures MapControlSettings based on compass visibility

## Features

### Custom Compass
- Position: Bottom-left corner (20px from left, 100px from bottom)
- Size: 48x48 pixels
- Design: White circular background with navigation icon
- Function: Tap to reset map rotation to north

### POI Control
- Default POI markers (stores, hotels, petrol stations, etc.) can be hidden
- Set `showPOI: false` to hide all default POI markers
- Custom markers added via `addCustomMarker` are not affected

## Notes
- The native Ola Maps SDK compass is disabled when using the custom compass
- The custom compass provides better control over position and appearance
- POI visibility is set during map initialization and cannot be changed dynamically