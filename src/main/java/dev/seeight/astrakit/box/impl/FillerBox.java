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

package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.Component;
import dev.seeight.astrakit.box.layout.Sizing;

public class FillerBox extends Component {
	public final float width;
	public final float height;

	public FillerBox() {
		this(10, 10);
	}

	public FillerBox(float width, float height) {
		this.width = width;
		this.height = height;
		this.setMinWidth(width);
		this.setMinHeight(height);
		this.setSizingX(Sizing.FILL);
		this.setSizingY(Sizing.FILL);
	}

	@Override
	public boolean isOutsideView(float offsetX, float offsetY) {
		return true;
	}
}
