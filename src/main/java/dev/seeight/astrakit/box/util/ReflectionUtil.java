package dev.seeight.astrakit.box.util;

import java.util.List;

public class ReflectionUtil {
	public static char[] conv(List<Character> arr) {
		char[] c = new char[arr.size()];

		for (int i = 0; i < arr.size(); i++) {
			c[i] = arr.get(i);
		}

		return c;
	}

	public static char[] conv(Character[] arr) {
		char[] c = new char[arr.length];

		for (int i = 0; i < arr.length; i++) {
			c[i] = arr[i];
		}

		return c;
	}
}
