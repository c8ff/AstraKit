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

import dev.seeight.astrakit.box.res.BoxResources;
import dev.seeight.renderer.renderer.Renderer;

public interface UIBoxContext {
	int getWindowWidth();

	int getWindowHeight();

	Renderer getRenderer();

	default boolean boxBoundView() {
		return false;
	}

	Mouser getMouser();

	Keyboarder getKeyboarder();

	double getDeltaTime();

	void catchError(String message, Throwable throwable);

	BoxResources getBoxResources();
}
