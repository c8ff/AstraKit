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
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class ButtonBox extends ComponentBox {
	public final IFont font;
	public final IFontRenderer fontRenderer;
	protected char[] text;
	public final float margin;
	@Nullable
	public Runnable click;
	protected boolean disabled = false;

	public ButtonBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, String text, float margin, @Nullable Runnable click) {
		this(i, font, fontRenderer, text.toCharArray(), margin, click);
	}

	public ButtonBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, char[] text, float margin, @Nullable Runnable click) {
		super(i);
		this.font = font;
		this.fontRenderer = fontRenderer;
		this.margin = margin;
		this.click = click;
		this.setText(text); // Define size
	}

	protected void renderBackground(float x, float y, float x2, float y2, float alpha) {
		float m = this.disabled ? 0.25F : 1F;

		float brightness;
		if (!this.isFocused()) {
			brightness = 0.25F * m;
		} else {
			brightness = 0.10F * m;
		}

		this.renderer.color(brightness, brightness, brightness, alpha);
		this.renderer.rect2f(x, y, x2, y2);
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;

		float fontX = (float) Math.floor(x + (this.getWidth() - this.fontRenderer.getWidthFloat(this.font, this.text)) / 2f);
		float fontY = (float) Math.floor(y + (this.getHeight() - this.font.getHeight()) / 2f + 2F);

		this.renderBackground(x, y, x + this.getWidth(), y + this.getHeight(), alpha);

		float m = this.disabled ? 0.25F : 1F;

		this.renderer.color(m, m, m, alpha);
		this.fontRenderer.drawString(this.font, this.text, fontX, fontY);
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		if (key == GLFW.GLFW_KEY_ENTER && action == GLFW.GLFW_RELEASE && this.isFocused()) {
			this.execute();
		}
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (this.disabled) {
			return false;
		}

		if (this.i.getMouser().isLeft(button) && action == GLFW.GLFW_RELEASE && this.isFocused()) {
			if (isInside(x, y)) {
				this.execute();
			}

			return false;
		}

		if (isInside(x, y)) {
			if (this.i.getMouser().isLeft(button) && action == GLFW.GLFW_PRESS) {
				return true;
			}
		}

		return super.mouseEvent(button, action, mods, x, y);
	}

	protected void execute() {
		if (this.click != null) {
			this.click.run();
		}
	}

	public void setText(char[] text) {
		this.text = text;
		this.setMinWidth((float) Math.ceil(Math.max(this.fontRenderer.getWidthFloat(this.font, this.text), 50) + this.margin * 2));
		this.setMinHeight(this.font.getHeight() + this.margin * 2);
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());
	}

	@Contract("_->this")
	public ButtonBox setDisabled(boolean disabled) {
		this.disabled = disabled;
		return this;
	}
}
