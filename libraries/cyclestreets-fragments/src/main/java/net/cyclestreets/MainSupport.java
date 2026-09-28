package net.cyclestreets;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import net.cyclestreets.routing.Route;
import net.cyclestreets.util.MapPack;

public class MainSupport {
  public static final String TAG = "CS_MAINSUPPORT";

  public static boolean switchMapFile(final Intent launchIntent) {
    Log.d(TAG, "switchMapFile");
    final String mappackage = launchIntent.getStringExtra("mapfile");
    if(mappackage == null) {
      Log.d(TAG, "switchMapFile: no mapfile extra present");
      return false;
    }
    final MapPack pack = MapPack.findByPackage(mappackage);
    if(pack == null) {
      Log.w(TAG, "switchMapFile: no map pack found for package " + mappackage);
      return false;
    }
    Log.i(TAG, "switchMapFile: enabling map file " + pack.path());
    CycleStreetsPreferences.enableMapFile(pack.path());
    return true;
  } // switchMapFile

  public static boolean loadRoute(final Intent launchIntent,
                                  final Context context) {
    Log.d(TAG, "loadRoute");
    final Uri launchUri = launchIntent.getData();
    if (launchUri == null) {
      Log.d(TAG, "loadRoute: launch intent has no data");
      return false;
    }

    final int itinerary = findItinerary(launchUri);
    if (itinerary == -1) {
      Log.w(TAG, "loadRoute: could not determine itinerary from " + launchUri);
      return false;
    }

    Log.i(TAG, "loadRoute: fetching route for itinerary " + itinerary);
    Route.FetchRoute(CycleStreetsPreferences.routeType(),
        itinerary,
        CycleStreetsPreferences.speed(),
        context);
    return true;
  } // loadRoute

  private static int findItinerary(final Uri launchUri) {
    try {
      final String itinerary = extractItinerary(launchUri);
      return Integer.parseInt(itinerary);
    } catch(Exception whatever) {
      Log.e(TAG, "findItinerary: failed to extract itinerary from " + launchUri, whatever);
      return -1;
    } // catch
  } // findItinerary

  private static String extractItinerary(final Uri launchUri) {
    final String host = launchUri.getHost();

    if ("cycle.st".equals(host))
      return launchUri.getPath().substring(2);

    if ("m.cyclestreets.net".equals(host)) {
      final String frag = launchUri.getFragment();
      return frag.substring(0, frag.indexOf('/'));
    }

    final String path = launchUri.getPath().substring(8);
    return path.replace("/", "");
  } // extractItinerary

  private MainSupport() { }
} // MainSupport
