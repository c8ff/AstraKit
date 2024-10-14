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

package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import dev.seeight.common.lwjgl.font.FontRenderer;
import org.lwjgl.glfw.GLFW;

public abstract class ButtonComponent extends Component {
	private final String string;

	public ButtonComponent(final String string) {
		this.string = string;
	}

	@Override
	public void mouseButtonEvent(int x, int y, int button, int action) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
			this.click();
		}
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		if (key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_PRESS) {
			this.click();
		}
	}

	public abstract void click();

	public ButtonComponent calcSize(FontRenderer font, float marginX, float marginY) {
		this.width = font.getWidthFloat(this.getString()) + marginX * 2;
		this.height = font.FONT_HEIGHT_FLOAT + marginY * 2;

		return this;
	}

	public String getString() {
		return string;
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderButton(this, alpha);
	}
}
