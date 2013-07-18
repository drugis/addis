package com.jgraph.layout.hierarchical;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class JGraphCoordinateAssignmentTest {
	@Test
	public void test() {
		int[] in = {
				2, 2, 8, 8, 8, 8, 2, 3, 8, 2,
				8, 2, 8, 8, 2, 8, 2, 2, 8, 2,
				2, 3, 2, 2, 2, 2, 2, 8, 2, 8,
				2, 2, 8, 2, 8, 8, 8, 8, 2, 8,
				8};
		int[] out = {
				8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
				8, 8, 8, 8, 8, 8, 8, 8, 8, 3,
				3, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
				2};
		final int n = in.length;
		
		JGraphCoordinateAssignment.WeightedCellSorter[] actual = new JGraphCoordinateAssignment.WeightedCellSorter[n];
		JGraphCoordinateAssignment.WeightedCellSorter[] expected = new JGraphCoordinateAssignment.WeightedCellSorter[n];
		for (int i = 0; i < n; ++i) {
			actual[i] = new JGraphCoordinateAssignment.WeightedCellSorter();
			actual[i].weightedValue = in[i];
			
			expected[i] = new JGraphCoordinateAssignment.WeightedCellSorter();
			expected[i].weightedValue = out[i];
		}
		
		Arrays.sort(actual);
		Assert.assertArrayEquals(expected, actual);
	}
}
