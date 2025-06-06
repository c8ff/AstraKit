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

import dev.seeight.astrakit.box.UIBoxContext;
import org.lwjgl.opengl.GL11;

public class GenericOpenGLCropContext implements ICropContext {
	private final UIBoxContext ctx;
	private final boolean invertedY;
	private boolean cropping;

	public GenericOpenGLCropContext(UIBoxContext ctx, boolean invertedY) {
		this.ctx = ctx;
		this.invertedY = invertedY;
	}

	@Override
	public void startCropping(float x, float y, float width, float height) {
		cropping = true;
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		if (this.invertedY) {
			GL11.glScissor((int) x, (int) (this.ctx.getWindowHeight() - y - height), (int) width, (int) height);
		} else {
			GL11.glScissor((int) x, (int) y, (int) width, (int) height);
		}
	}

	@Override
	public void stopCropping() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		cropping = false;
	}

	@Override
	public boolean isCropping() {
		return cropping;
	}
}
