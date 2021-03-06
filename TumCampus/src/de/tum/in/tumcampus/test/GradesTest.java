package de.tum.in.tumcampus.test;

import android.content.pm.ActivityInfo;
import android.test.ActivityInstrumentationTestCase2;
import com.jayway.android.robotium.solo.Solo;

import de.tum.in.tumcampus.R;
import de.tum.in.tumcampus.TumCampus;

/**
 * @author Florian Schulz
 * @sovles GradesTest
 * TODO Review Vasyl
 */

public class GradesTest extends ActivityInstrumentationTestCase2<TumCampus> {

	private Solo solo; // simulates the user of the app

	public GradesTest() {
		super("de.tum.in.newtumcampus", TumCampus.class);
	}

	@Override
	public void setUp() {
		solo = new Solo(getInstrumentation(), getActivity());

		assertTrue(solo.searchText(solo.getString(R.string.grades)));
		solo.clickOnText(solo.getString(R.string.grades));
		solo.sleep(5000);
		solo.goBack();
		//solo.scrollDown();
	}

	private void _testSpinner() {
		solo.clickOnText(solo.getString(R.string.all_programs));
		solo.clickOnText("1630 17 043");
		solo.scrollDown();
		solo.scrollUp();
		solo.clickOnText("1630 17 043");
		solo.clickOnText(solo.getString(R.string.all_programs));
		solo.scrollDown();
	}
	
	public void testResults(){
		assertTrue(solo.searchText(solo.getString(R.string.grades)));
		solo.clickOnText(solo.getString(R.string.grades));
		assertTrue(solo.searchText("Datenbanken"));
		assertTrue(solo.searchText(solo.getString(R.string.semester)));
		assertTrue(solo.searchText("11W"));
		assertTrue(solo.searchText(solo.getString(R.string.examiner)));
		assertTrue(solo.searchText("Neumann"));
		assertTrue(solo.searchText(solo.getString(R.string.mode)));
		assertTrue(solo.searchText("Schriftlich"));
	}

	
	public void testPortrait() {
		assertTrue(solo.searchText(solo.getString(R.string.grades)));
		solo.clickOnText(solo.getString(R.string.grades));
		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		_testSpinner();
	}
	
	public void testLandscape() {
		assertTrue(solo.searchText(solo.getString(R.string.grades)));
		solo.clickOnText(solo.getString(R.string.grades));
		solo.setActivityOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		_testSpinner();
	}
	
}
