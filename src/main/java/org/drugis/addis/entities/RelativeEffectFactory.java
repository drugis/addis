package org.drugis.addis.entities;

public class RelativeEffectFactory {
	public static <T extends RelativeEffect<?>> RelativeEffect<?> buildRelativeEffect(
			Study s, Endpoint e, Drug base, Drug subj, Class<T> type) {
		if (type.equals(StandardisedMeanDifference.class)) {
			return buildStandardisedMeanDifference(s, e, base, subj);
		}
		if (type.equals(MeanDifference.class)) {
			return buildMeanDifference(s, e, base, subj);
		}
		if (type.equals(OddsRatio.class)) {
			return buildOddsRatio(s, e, base, subj);
		}
		if (type.equals(LogOddsRatio.class)) {
			return buildLogOddsRatio(s, e, base, subj);
		}
		if (type.equals(RiskRatio.class)) {
			return buildRiskRatio(s, e, base, subj);
		}
		if (type.equals(LogRiskRatio.class)) {
			return buildLogRiskRatio(s, e, base, subj);
		}
		if (type.equals(RiskDifference.class)) {
			return buildRiskDifference(s, e, base, subj);
		}
		return null;
	}
	
	private static RelativeEffect<?> buildRiskDifference(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new RiskDifference(
				findRateMeasurement(s, e, base),
				findRateMeasurement(s, e, subj));
	}

	private static RelativeEffect<?> buildRiskRatio(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new RiskRatio(
				findRateMeasurement(s, e, base),
				findRateMeasurement(s, e, subj));
	}

	private static RelativeEffect<?> buildLogRiskRatio(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new LogRiskRatio(
				findRateMeasurement(s, e, base),
				findRateMeasurement(s, e, subj));
	}
	
	private static RelativeEffect<?> buildOddsRatio(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new OddsRatio(
				findRateMeasurement(s, e, base),
				findRateMeasurement(s, e, subj));
	}
	
	private static RelativeEffect<?> buildLogOddsRatio(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new LogOddsRatio(
				findRateMeasurement(s, e, base),
				findRateMeasurement(s, e, subj));
	}

	private static RelativeEffect<?> buildMeanDifference(Study s, Endpoint e,
			Drug base, Drug subj) {
		return new MeanDifference(
				findContinuousMeasurement(s, e, base),
				findContinuousMeasurement(s, e, subj));
	}

	private static RelativeEffect<?> buildStandardisedMeanDifference(Study s,
			Endpoint e, Drug base, Drug subj) {
		return new StandardisedMeanDifference(
				findContinuousMeasurement(s, e, base),
				findContinuousMeasurement(s, e, subj));
	}
	
	private static ContinuousMeasurement findContinuousMeasurement(Study s, Endpoint e, Drug d) {
		if (!e.getType().equals(Endpoint.Type.CONTINUOUS)) {
			throw new IllegalArgumentException("Endpoint should be Continuous");
		}
		return (ContinuousMeasurement)findMeasurement(s, e, d);
	}
	
	private static RateMeasurement findRateMeasurement(Study s, Endpoint e, Drug d) {
		if (!e.getType().equals(Endpoint.Type.RATE)) {
			throw new IllegalArgumentException("Endpoint should be Rate");
		}
		return (RateMeasurement)findMeasurement(s, e, d);
	}

	private static Measurement findMeasurement(Study s, Endpoint e, Drug drug) {
		for (PatientGroup g : s.getPatientGroups()) {
			if (g.getDrug().equals(drug)) {
				return s.getMeasurement(e, g);
			}
		}
		return null;
	}
}
