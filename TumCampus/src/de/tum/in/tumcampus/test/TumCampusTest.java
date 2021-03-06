﻿package de.tum.in.tumcampus.test;

import android.test.ActivityInstrumentationTestCase2;

import com.jayway.android.robotium.solo.Solo;

import de.tum.in.tumcampus.Const;
import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.TumCampus;
import de.tum.in.tumcampus.common.Utils;

/*
 * Florian Schulz
 * changed of xml-external reasons
 * TODO Review Vasyl
 */
public class TumCampusTest extends ActivityInstrumentationTestCase2<TumCampus> {
	
	
	private Solo solo; // simulates the user of the app

	public TumCampusTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());
	
	}

	/**
	 * @author Florian Schulz
	 * @solves Test all required modules
	 */
	public void testMenu() {
		assertTrue(solo.searchText(solo.getString(R.string.update)));
		assertTrue(solo.searchText(solo.getString(R.string.lectures)));
		assertTrue(solo.searchText(solo.getString(R.string.person_search)));
		//assertTrue(solo.searchText(solo.getString(R.string.organisations)));
		assertTrue(solo.searchText(solo.getString(R.string.grades)));
		assertTrue(solo.searchText(solo.getString(R.string.documents)));
		assertTrue(solo.searchText(solo.getString(R.string.study_plans)));
		assertTrue(solo.searchText(solo.getString(R.string.menues)));
		assertTrue(solo.searchText(solo.getString(R.string.mvv)));
		assertTrue(solo.searchText(solo.getString(R.string.rss_feeds)));
		assertTrue(solo.searchText(solo.getString(R.string.events)));
		assertTrue(solo.searchText(solo.getString(R.string.gallery)));
		assertTrue(solo.searchText(solo.getString(R.string.area_maps)));
		assertTrue(solo.searchText(solo.getString(R.string.roomfinder)));
		assertTrue(solo.searchText(solo.getString(R.string.opening_hours)));
		assertTrue(solo.searchText(solo.getString(R.string.news)));
		assertTrue(solo.searchText(solo.getString(R.string.links)));
		assertTrue(solo.searchText(solo.getString(R.string.facebook)));
		assertTrue(solo.searchText(solo.getString(R.string.app_info)));
	}

	public void testRefresh() {
		assertTrue(solo.searchText(solo.getString(R.string.update)));
		solo.clickOnText(solo.getString(R.string.update));

		int duration = 0;
		while (!solo.searchText(solo.getString(R.string.completed)) && duration <= 60) {
			assertFalse(solo.searchText("Exception"));
			solo.sleep(1000);
			duration++;
		}
	}

	public void testManual() {
		solo.sendKey(Solo.MENU);

		assertTrue(solo.searchText(solo.getString(R.string.manual)));
		solo.clickOnText(solo.getString(R.string.manual));

		solo.goBack();
	}
	
	
	public void testClearCache() {
		solo.sendKey(Solo.MENU);

		assertTrue(solo.searchText(solo.getString(R.string.empty_cache)));
		solo.clickOnText(solo.getString(R.string.empty_cache));
	}

	public void testAppinfo() {
		assertTrue(solo.searchText(solo.getString(R.string.app_info)));

		solo.clickOnText(solo.getString(R.string.app_info));
		assertTrue(solo.searchText(solo.getString(R.string.full_app_info)));
		solo.goBack();
	}
}