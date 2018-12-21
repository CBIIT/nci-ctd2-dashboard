package gov.nih.nci.ctd2.dashboard.importer.internal;

import java.util.List;

import gov.nih.nci.ctd2.dashboard.model.Subject;
import gov.nih.nci.ctd2.dashboard.model.Xref;

public class XrefData {

	protected Xref xref;
	protected List<? extends Subject> subjects;

	public XrefData(Xref xref, List<? extends Subject> subjects) {
		this.xref = xref;
		this.subjects = subjects;
	}
}
