package de.tum.in.tumcampus.models;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Wrapper class holding a list of tuitions  ; based on ExamList
 * <p>
 * Note: This model is based on the TUMOnline web service response format for a
 * corresponding request.
 * 
 * @author NTK
 */
@Root(name = "rowset")
public class TuitionList {

	@ElementList(inline = true)
	private List<Tuition> tuitions;

	public List<Tuition> getTuitions() {
		return tuitions;
	}

}
