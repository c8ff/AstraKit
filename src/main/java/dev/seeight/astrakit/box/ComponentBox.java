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

import dev.seeight.renderer.renderer.Renderer;

public class ComponentBox extends Component {
	protected final UIBoxContext i;
	protected final Renderer renderer;

	public ComponentBox(UIBoxContext i) {
		this.i = i;
		this.renderer = i.getRenderer();
	}

	@Override
	public boolean isOutsideView(float offsetX, float offsetY) {
		float x = offsetX + this.getX();
		if (x + this.getWidth() < 0) {
			return true;
		}

		if (x > this.i.getWindowWidth()) {
			return true;
		}

		float y = offsetY + this.getY();
		if (y + this.getHeight() < 0) {
			return true;
		}

		if (y > this.i.getWindowHeight()) {
			return true;
		}

		return false;
	}
}
