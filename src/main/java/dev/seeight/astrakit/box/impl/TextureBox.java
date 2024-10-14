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

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.renderer.renderer.Texture;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class TextureBox extends ComponentBox {
	private boolean dirty = true;

	private final Texture texture;

	private double r = 1.0;
	private double g = 1.0;
	private double b = 1.0;
	private double a = 1.0;

	private Runnable click;

	public TextureBox(UIBoxContext i, Texture texture, int size) {
		super(i);
		this.texture = texture;
		this.setMinWidth(size);
		this.setMinHeight(size);
		this.setSize(size, size);
	}

	public TextureBox setClick(Runnable click) {
		this.click = click;
		return this;
	}

	public TextureBox setColor(Color color) {
		return this.setColor(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0);
	}

	public TextureBox setColor(double r, double g, double b, double a) {
		if (r != this.r || g != this.g || b != this.b || a != this.a) {
			this.dirty = true;
		}

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		return this;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;

		Texture t = this.getTexture();

		float x2 = x + this.getWidth();
		float y2 = y + this.getHeight();

		this.renderer.color(r, g, b, a * alpha);
		this.renderer.texRect2f(t, x, y, x2, y2);
		this.dirty = false;
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (this.click == null) return false;

		if (isInside(x, y)) {
			if (this.i.getMouser().isLeft(button) && action == GLFW.GLFW_PRESS) {
				if (this.click != null) {
					this.click.run();
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		this.dirty = true;
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		this.dirty = true;
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.dirty = true;
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.dirty = true;
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	public Texture getTexture() {
		return texture;
	}
}
