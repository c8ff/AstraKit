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

package dev.seeight.astrakit.box.crop;

import dev.seeight.renderer.renderer.gl.OpenGLRenderer2;

@Deprecated
public class OpenGLRenderer2CropContext implements ICropContext {
	private final OpenGLRenderer2 g;
	private boolean cropping;

	public OpenGLRenderer2CropContext(OpenGLRenderer2 g) {
		this.g = g;
	}

	@Override
	public void startCropping(float x, float y, float width, float height) {
		g.crop(x, y, width, height);
		cropping = true;
	}

	@Override
	public void stopCropping() {
		g.stopCrop();
		cropping = false;
	}

	@Override
	public boolean isCropping() {
		return cropping;
	}
}
