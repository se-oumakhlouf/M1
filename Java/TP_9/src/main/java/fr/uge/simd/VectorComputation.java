package fr.uge.simd;
import jdk.incubator.vector.*;

public class VectorComputation {
	private static final VectorSpecies<Integer> SPECIES = IntVector.SPECIES_PREFERRED;
	
	public static int sum(int[] array) {
		var length = array.length;
		var loopBound = length - length % SPECIES.length();
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for (; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		int sum = v1.reduceLanes(VectorOperators.ADD);
		for (; i < length; i++) {
			sum += array[i];
		}
		return sum;
	}
	
	
	public static int min(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.min(v2);
		}
		var min = v1.reduceLanes(VectorOperators.MIN);
		for (; i < length; i++) {
			min = Integer.min(min, array[i]);
		}
		return min;
	}
	
	public static int sumMask(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.zero(SPECIES);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.add(v2);
		}
		var mask = SPECIES.indexInRange(i, length);
		var v3 = IntVector.fromArray(SPECIES, array, i, mask);
		v1 = v1.add(v3, mask);
		return v1.reduceLanes(VectorOperators.ADD);
	}
	
	public static int minMask(int[] array) {
		var length = array.length;
		var loopBound = SPECIES.loopBound(length);
		var v1 = IntVector.broadcast(SPECIES, Integer.MAX_VALUE);
		var i = 0;
		for(; i < loopBound; i += SPECIES.length()) {
			var v2 = IntVector.fromArray(SPECIES, array, i);
			v1 = v1.min(v2);
		}
		var mask = SPECIES.indexInRange(i, length);
		var v3 = IntVector.fromArray(SPECIES, array, i, mask);
		v1 = v1.lanewise(VectorOperators.MIN, v3, mask);
		var min = v1.reduceLanes(VectorOperators.MIN);
		return min;
	}
	
}
