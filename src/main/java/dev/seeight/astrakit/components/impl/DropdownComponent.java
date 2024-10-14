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

public class DropdownComponent extends Component {
	public String[] options;
	public boolean opened;
	private float listHeight;
	private float elementHeight;
	private int selected;

	public DropdownComponent(int selected, String[] options) {
		this.selected = selected;
		this.options = options;
	}

	public DropdownComponent calcSize(FontRenderer font) {
		this.height = font.FONT_HEIGHT_FLOAT;
		this.elementHeight = font.FONT_HEIGHT_FLOAT;
		this.listHeight = this.elementHeight * options.length;
		return this;
	}

	public int getSelected() {
		return selected;
	}

	public String getElmSelected() {
		return this.options[selected];
	}

	public float getListHeight() {
		return listHeight;
	}

	public float getElementHeight() {
		return elementHeight;
	}

	@Override
	public boolean shouldFocus(int x, int y) {
		return !opened ? super.shouldFocus(x, y) : x > this.x + this.parentX && y > this.y + this.parentY && x < this.x + this.parentX + this.width && y < this.y + this.height + getListHeight() + this.parentY;
	}

	@Override
	public void mouseButtonEvent(int x, int y, int button, int action) {
		if (opened) {
			float mx = x - this.parentX - this.x;
			float my = y - this.parentY - this.y - this.getElementHeight();

			if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
				float z = 0;

				String[] strings = this.options;
				for (int i = 0; i < strings.length; i++) {
					if (mx > 0 && my > z && mx < this.width && my < z + this.elementHeight) {
						this.selected = i;
						this.setOpened(false);
						return;
					}

					z += this.elementHeight;
				}

				this.setOpened(!opened);
			}
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
			this.setOpened(true);
		}
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderDropdown(this, alpha);
	}

	protected void setOpened(boolean opened) {
		this.opened = opened;
	}

	public void setOptions(String[] options, FontRenderer font) {
		if (options == null) {
			throw new NullPointerException("options cannot be null");
		}
		if (options.length == 0) {
			throw new IllegalArgumentException("options cannot be empty");
		}

		this.options = options;
		this.setSelected(0);
		this.calcSize(font);
	}

	public String[] getOptions() {
		return options;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
}
