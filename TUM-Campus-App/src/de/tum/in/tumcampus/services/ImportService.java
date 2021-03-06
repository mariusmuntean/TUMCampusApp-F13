﻿package de.tum.in.tumcampus.services;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.tum.in.tumcampus.Const;
import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.TumCampus;
import de.tum.in.tumcampus.common.Utils;
import de.tum.in.tumcampus.models.Feed;
import de.tum.in.tumcampus.models.FeedManager;
import de.tum.in.tumcampus.models.GalleryManager;
import de.tum.in.tumcampus.models.LectureItem;
import de.tum.in.tumcampus.models.LectureItemManager;
import de.tum.in.tumcampus.models.LectureManager;
import de.tum.in.tumcampus.models.Link;
import de.tum.in.tumcampus.models.LinkManager;
import de.tum.in.tumcampus.models.Location;
import de.tum.in.tumcampus.models.LocationManager;
import de.tum.in.tumcampus.models.TransportManager;

/**
 * Service used to import files from internal sd-card
 */
public class ImportService extends IntentService {

	public static final String IMPORT_SERVICE = "ImportService";

	public static final String ISO = "ISO-8859-1";

	public static final String CSV_TRANSPORTS = "transports.csv";

	public static final String CSV_FEEDS = "feeds.csv";

	public static final String CSV_LOCATIONS = "locations.csv";

	public static final String CSV_HOLIDAYS = "lectures_holidays.csv";

	public static final String CSV_VACATIONS = "lectures_vacations.csv";

	public static final String CSV_LINKS = "links.csv";

	/**
	 * default init (run intent in new thread)
	 */
	public ImportService() {
		super(IMPORT_SERVICE);
	}

	/**
	 * Import broadcast identifier
	 */
	public final static String broadcast = "de.tum.in.newtumcampus.intent.action.BROADCAST_IMPORT";

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getStringExtra(Const.ACTION_EXTRA);
		Utils.log(action);

		// import all defaults or only one action
		if (action.equals(Const.DEFAULTS)) {
			try {
				// get current app version
				String version = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;

				// check if database update is needed
				boolean update = false;
				File f = new File(getFilesDir() + "/" + version);
				if (!f.exists()) {
					updateDatabase();
					update = true;
				}
				importTransportsDefaults();
				importFeedsDefaults();
				importLinksDefaults();
				importLectureItemsDefaults(update);
				importLocationsDefaults(update);
				f.createNewFile();
			} catch (Exception e) {
				Utils.log(e, "");
			}
		} else {
			// show import notification
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager nm = (NotificationManager) getSystemService(ns);

			Notification notification = new Notification(android.R.drawable.stat_sys_download,
					getString(R.string.importing), System.currentTimeMillis());
// TODO ATHOME check this!!!
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TumCampus.class), 0);

			notification.setLatestEventInfo(this, getString(R.string.tum_campus_import), "", contentIntent);
			nm.notify(1, notification);

			// added by Daniel G. Mayr
			if (action.equals(Const.LECTURES_TUM_ONLINE)) {
				Log.d(getString(R.string.service), getString(R.string.import_from_tumonline));
				importLectureItemsFromTUMOnline();
			} else {

				try {
					// check if sd card available
					Utils.getCacheDir("");

					if (action.equals(Const.FEEDS)) {
						importFeeds();
					}
					if (action.equals(Const.LINKS)) {
						importLinks();
					}
					if (action.equals(Const.LECTURES)) {
						importLectureItems();
					}

					message(getString(R.string.completed), getString(R.string.completed));
				} catch (Exception e) {
					message(e, "");
				}
			}
			nm.cancel(1);
		}
	}

	/**
	 * Update database schema
	 */
	public void updateDatabase() {
		GalleryManager gm = new GalleryManager(this);
		gm.update();
	}

	/**
	 * Import feeds from internal directory
	 */
	public void importFeeds() {
		FeedManager nm = new FeedManager(this);
		try {
			nm.importFromInternal();
		} catch (Exception e) {
			message(e, nm.lastInfo);
		}
	}

	/**
	 * Import links from internal directory
	 */
	public void importLinks() {
		LinkManager lm = new LinkManager(this);
		try {
			lm.importFromInternal();
		} catch (Exception e) {
			message(e, lm.lastInfo);
		}
	}

	/**
	 * Import lectures and lecture items from internal directory
	 */
	public void importLectureItems() {
		LectureItemManager lim = new LectureItemManager(this);
		try {
			lim.importFromInternal();
		} catch (Exception e) {
			message(e, lim.lastInfo);
		}

		LectureManager lm = new LectureManager(this);
		lm.updateLectures();
	}

	/**
	 * imports lecture items from TUMOnline HINT: access token have to be set
	 * 
	 * @author Daniel G. Mayr
	 */
	public void importLectureItemsFromTUMOnline() {
		LectureItemManager lim = new LectureItemManager(this);
		try {
			lim.importFromTUMOnline(this);
		} catch (Exception e) {
			message(e, lim.lastInfo);
		}

		LectureManager lm = new LectureManager(this);
		lm.updateLectures();
	}

	/**
	 * Import default stations from assets
	 * 
	 * @throws Exception
	 */
	public void importTransportsDefaults() throws Exception {

		TransportManager tm = new TransportManager(this);
		if (tm.empty()) {
			List<String[]> rows = Utils.readCsv(getAssets().open(CSV_TRANSPORTS), ISO);

			for (String[] row : rows) {
				tm.replaceIntoDb(row[0]);
			}
		}
	}

	/**
	 * Import default feeds from assets
	 * 
	 * @throws Exception
	 */
	public void importFeedsDefaults() throws Exception {

		FeedManager nm = new FeedManager(this);
		if (nm.empty()) {
			List<String[]> rows = Utils.readCsv(getAssets().open(CSV_FEEDS), ISO);

			for (String[] row : rows) {
				nm.insertUpdateIntoDb(new Feed(row[0], row[1]));
			}
		}
	}

	/**
	 * Import default location and opening hours from assets
	 * 
	 * <pre>
	 * @param force boolean force import of locations
	 * @throws Exception
	 * </pre>
	 */
	public void importLocationsDefaults(boolean force) throws Exception {

		LocationManager lm = new LocationManager(this);
		if (lm.empty() || force) {
			List<String[]> rows = Utils.readCsv(getAssets().open(CSV_LOCATIONS), ISO);

			for (String[] row : rows) {
				lm.replaceIntoDb(new Location(Integer.parseInt(row[0]), row[1], row[2], row[3], row[4], row[5], row[6],
						row[7], row[8]));
			}
		}
	}

	/**
	 * Import default lectures, lecture items (holidays, vacations) from assets
	 * 
	 * <pre>
	 * @param force boolean force import of lecture items
	 * @throws Exception
	 * </pre>
	 */
	public void importLectureItemsDefaults(boolean force) throws Exception {
		LectureItemManager lim = new LectureItemManager(this);
		if (lim.empty() || force) {
			List<String[]> rows = Utils.readCsv(getAssets().open(CSV_HOLIDAYS), ISO);

			for (String[] row : rows) {
				lim.replaceIntoDb(new LectureItem.Holiday(row[0], Utils.getDate(row[1]), row[2]));
			}
// TODO ATHOME CHECK OUTDATED??
			rows = Utils.readCsv(getAssets().open(CSV_VACATIONS), ISO);

			for (String[] row : rows) {
				lim.replaceIntoDb(new LectureItem.Vacation(row[0], Utils.getDate(row[1]), Utils.getDate(row[2]), row[3]));
			}
		}

		LectureManager lm = new LectureManager(this);
		lm.updateLectures();
	}

	/**
	 * Import default links from assets
	 * 
	 * @throws Exception
	 */
	public void importLinksDefaults() throws Exception {
		LinkManager lm = new LinkManager(this);
		if (lm.empty()) {
			List<String[]> rows = Utils.readCsv(getAssets().open(CSV_LINKS), ISO);

			for (String[] row : rows) {
				lm.insertUpdateIntoDb(new Link(row[0], row[1]));
			}
		}
	}

	/**
	 * Send notification message to service caller
	 * 
	 * <pre>
	 * @param e Exception, get message and stacktrace from 
	 * @param info Notification info, append to exception message
	 * </pre>
	 */
	public void message(Exception e, String info) {
		Utils.log(e, info);

		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));

		String message = e.getMessage();
		if (Utils.getSettingBool(this, Const.Settings.debug)) {
			message += sw.toString();
		}
		message(info + " " + message, getString(R.string.error));
	}

	/**
	 * Send notification message to service caller
	 * 
	 * <pre>
	 * @param message Notification message
	 * @param action Notification action (e.g. error, completed)
	 * </pre>
	 */
	public void message(String message, String action) {
		Intent intentSend = new Intent();
		intentSend.setAction(broadcast);
		intentSend.putExtra(Const.MESSAGE_EXTRA, message);
		intentSend.putExtra(Const.ACTION_EXTRA, action);
		sendBroadcast(intentSend);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Utils.log("");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Utils.log("");
	}
}