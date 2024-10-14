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

package dev.seeight.astrakit.box;

import org.lwjgl.glfw.GLFW;

public interface Mouser {
	double getX();

	double getY();

	default boolean isLeft(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_1;
	}

	default boolean isRight(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_2;
	}

	default boolean isMiddle(int code) {
		return code == GLFW.GLFW_MOUSE_BUTTON_3;
	}

}
