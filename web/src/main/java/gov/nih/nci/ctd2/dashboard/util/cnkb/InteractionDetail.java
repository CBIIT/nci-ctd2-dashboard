package gov.nih.nci.ctd2.dashboard.util.cnkb;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * The detail of one interaction.
 */
public class InteractionDetail {

	private List<InteractionParticipant> participantList;
	private final String interactionType;
	private final Map<Short, Float> confidence;

	public InteractionDetail(InteractionParticipant participant, String interactionType) {

		participantList = new ArrayList<InteractionParticipant>();
		participantList.add(participant);
		this.interactionType = interactionType;
		confidence = new HashMap<Short, Float>();
	}

	public void addParticipant(InteractionParticipant participant) {
		if (participantList == null)
			participantList = new ArrayList<InteractionParticipant>();
		participantList.add(participant);
	}

	public List<InteractionParticipant> getParticipantList() {
		return participantList;
	}

	public String getParticipantGeneList() {
		String geneSymbolList = participantList.get(0).getGeneName();
		for (int i = 1; i < participantList.size(); i++)
			geneSymbolList = geneSymbolList + "," + participantList.get(i).getGeneName();
		return geneSymbolList;
	}

	public Float getConfidenceValue(int usedConfidenceType) {
		Float f = confidence.get((short) usedConfidenceType);
		if (f != null)
			return f;
		else
			return null; // if usedConfidenceType is not found, return 0.
	}

	public List<Short> getConfidenceTypes() {
		return new ArrayList<Short>(confidence.keySet());
	}

	public void addConfidence(float score, short type) {
		confidence.put(type, score);
	}

	public String getInteractionType() {
		return this.interactionType;
	}

	public Map<Short, Float> getConfidences() {
		return confidence;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("participants:\n");
		for (var x : participantList) {
			sb.append("  " + x + "\n");
		}
		sb.append("confidences:\n");
		for (var x : confidence.keySet()) {
			sb.append("  " + x + ":" + confidence.get(x) + "\n");
		}
		return sb.toString();
	}
}
