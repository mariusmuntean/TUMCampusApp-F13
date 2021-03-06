package de.tum.in.tumcampus;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SlidingDrawer;

/**
 * Activity to show plans
 */
public class Plans extends Activity implements OnItemClickListener {

	private static int position = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plans);

		String[] plans = new String[] { getString(R.string.campus_garching), getString(R.string.campus_klinikum),
				getString(R.string.campus_olympiapark), getString(R.string.campus_olympiapark_gyms),
				getString(R.string.campus_main), getString(R.string.campus_weihenstephan),
				getString(R.string.mvv_fast_train_net), getString(R.string.mvv_nightlines) };

		ListView lv = (ListView) findViewById(R.id.listView);
		lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, plans));
		lv.setOnItemClickListener(this);

		if (position == -1) {
			SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slider);
			sd.open();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// refresh current selected plan on resume (rotate)
		if (position != -1) {
			onItemClick(null, null, position, 0);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> aview, View view, int pos, long id) {
		position = pos;

		WebView browser = (WebView) findViewById(R.id.webView);
		// activate zoom controls
		browser.getSettings().setBuiltInZoomControls(true);
		// activate double tab to zoom
		browser.getSettings().setUseWideViewPort(true);
		// reset zoom
		browser.clearView();

		// draw image from assets directory in webview
		String file = "";
		int width = getWindowManager().getDefaultDisplay().getWidth();

		if (position == 0) {
			file = "plans/CampusGarching.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_garching));
			browser.setInitialScale(100 * width / 1024);

		} else if (position == 1) {
			file = "plans/CampusKlinikum.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_klinikum));
			browser.setInitialScale(100 * width / 1024);

		} else if (position == 2) {
			file = "plans/CampusOlympiapark.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_olympiapark));
			browser.setInitialScale(100 * width / 900);

		} else if (position == 3) {
			file = "plans/CampusOlympiaparkHallenplan.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_olympiapark_gyms));
			browser.setInitialScale(100 * width / 800);

		} else if (position == 4) {
			file = "plans/CampusStammgelaende.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_main));
			browser.setInitialScale(100 * width / 1024);

		} else if (position == 5) {
			file = "plans/CampusWeihenstephan.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.campus_weihenstephan));
			browser.setInitialScale(100 * width / 1110);

		} else if (position == 6) {
			file = "plans/mvv.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.mvv_fast_train_net));
			browser.setInitialScale(100 * width / 1454);

		} else {
			file = "plans/mvv_night.jpg";
			setTitle(getString(R.string.plan) + getString(R.string.mvv_nightlines));
			browser.setInitialScale(100 * width / 1480);
		}

		String data = "<body style='margin:0px;'><img src='" + file + "'/></body>";
		browser.loadDataWithBaseURL("file:///android_asset/", data, "text/html", "UTF-8", null);
		browser.forceLayout();

		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slider);
		if (sd.isOpened()) {
			sd.animateClose();
		}
	}
}