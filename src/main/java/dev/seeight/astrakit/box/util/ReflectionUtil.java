/*
 * AstraKit
 * Copyright (C) 2024 c8ff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
