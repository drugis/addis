package nl.rug.escher.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.SortedMap;
import java.util.TreeMap;

public class StudentTTable {
	private static SortedMap<Integer, Double> s_lookup = new TreeMap<Integer, Double>();
	private static int s_maxV = 0;
	
	/**
	 * Get the critical value for the Student's t distribution, for one-sided 0.025 probability of error.
	 * @param v Degrees of freedom.
	 * @return Critical value.
	 */
	public static double getT(int v) {
		Double lookup = s_lookup.get(v > s_maxV ? s_maxV : v);
		if (lookup != null) {
			return lookup;
		}
		return interpolate(v);
	}
	
	private static double interpolate(int v) {
		int beforeV = s_lookup.headMap(v).lastKey();
		int afterV = s_lookup.tailMap(v).firstKey();
		double beforeT = s_lookup.get(beforeV);
		double afterT = s_lookup.get(afterV);
		
		double a = (afterT - beforeT) / (afterV - beforeV);
		return a * (v - beforeV) + beforeT;
	}

	static {
		InputStream table = StudentTTable.class.getResourceAsStream("studentt.csv");
		BufferedReader r = new BufferedReader(new InputStreamReader(table));
		try {
			initialize(r);
		} catch (IOException e) {
			s_lookup = null;
			e.printStackTrace();
		}
	}
	
	private static void initialize(BufferedReader r) throws IOException {
		while (r.ready()) {
			String line = r.readLine();
			String[] tokens = line.split(",");
			if (tokens.length != 2) {
				throw new IOException("Invalid file format");
			}
			int v = Integer.parseInt(tokens[0]);
			double t = Double.parseDouble(tokens[1]);
			if (v > s_maxV) {
				s_maxV = v;
			}
			s_lookup.put(v, t);
		}
	}
}
