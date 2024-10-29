package fr.uge.runlist;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Main {

	public static void main(String[] args) {
		var runList = RunList.<String>newBinarySearchList();
		runList.addRun("foo", 2);
		runList.addRun("bar", 1);
		runList.addRun("baz", 4);
		runList.size();
		runList.get(0);
		runList.get(1);
//		runList.get(2);
//		runList.get(3);
//		runList.get(4);
//		runList.get(5);
//		runList.get(6);
	}

}
