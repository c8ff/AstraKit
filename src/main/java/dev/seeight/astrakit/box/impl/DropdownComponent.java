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
import dev.seeight.astrakit.box.crop.ICropContext;
import dev.seeight.astrakit.box.util.Scroll2;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import dev.seeight.util.ListUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class DropdownComponent<T> extends ComponentBox implements Dropdown {
	private final ICropContext cropContext;
	private final Texture dropdownTexture;

	private @NotNull T @NotNull [] options;
	private T selected;

	public final IFontRenderer fontRenderer;
	public final IFont font;

	private Function<T, char[]> renderStringGet;

	private Consumer<T> changeEvent;
	private ChangeEvent<T> itemChangeEvent;

	private boolean o_ignoreFirstClick = false;
	private final int o_maxElementDisplay = 20;
	private final Scroll2 o_scroll;
	private T o_hovering;

	private boolean o_scrolling;
	protected double o_scrollStartY;
	protected final float o_scrollSensitivity = 250;

	@SafeVarargs
	public DropdownComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, float margin, ICropContext cropContext, int selected, T... options) {
		super(i);
		this.dropdownTexture = i.getBoxResources().getDropdownTexture();
		this.cropContext = cropContext;
		this.options = options;
		this.selected = options[selected];

		this.fontRenderer = fontRenderer;
		this.font = font;

		float maxWidth = 0;

		for (T option : this.options) {
			char[] o = getOptionStr(option);

			float w = this.fontRenderer.getWidthFloat(this.font, o);

			if (maxWidth < w) {
				maxWidth = w;
			}
		}

		this.setMinWidth(maxWidth + margin * 2);
		this.setMinHeight(this.font.getHeight() + margin * 2);
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());

		this.o_scroll = new Scroll2(i);
	}

	public DropdownComponent<T> setRenderStringGet(Function<T, char[]> renderStringGet) {
		this.renderStringGet = renderStringGet;
		return this;
	}

	@Deprecated
	public DropdownComponent<T> setChangeEvent(Consumer<T> changeEvent) {
		this.changeEvent = changeEvent;
		return this;
	}

	public DropdownComponent<T> setItemChangeEvent(ChangeEvent<T> ev) {
		this.itemChangeEvent = ev;
		return this;
	}

	public DropdownComponent<T> set(T selected) {
		var index = ListUtil.indexOf(this.options, selected);
		if (index == -1) throw new IllegalArgumentException("New value not present in options.");

		this.selected = selected;
		if (changeEvent != null)
			changeEvent.accept(selected);
		if (itemChangeEvent != null)
			itemChangeEvent.onDropdownChange(index, selected, this);
		return this;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;
		float width = this.getWidth();
		float height = this.getHeight();

		renderBackground(x, y, width, height, alpha);

		float fontHeight = font.getHeight();
		// (flooring fixes weird font rendering)
		float fontX = (float) Math.floor(x + 10);
		float fontY = (float) Math.floor(y + (this.getHeight() - fontHeight) / 2f + 1F);

		// Main text
		this.renderer.color(1, 1, 1, alpha);
		this.fontRenderer.drawString(font, getOptionStr(this.selected), fontX, fontY);

		// Dropdown icon
		float dropdownX = x + width - 12 - 10;
		float dropdownY = y + (height - 12) / 2f;
		boolean open = this.isFocused();
		this.renderer.texRect2f(this.dropdownTexture, dropdownX, dropdownY, dropdownX + 12, dropdownY + 12, 0, open ? 1 : 0, 1, open ? 0 : 1);
	}

	protected void renderBackground(float x, float y, float width, float height, float alpha) {
		if (this.isFocused()) {
			this.renderer.color(0.10F, 0.10F, 0.10F, alpha);
		} else {
			this.renderer.color(0.20F, 0.20F, 0.20F, alpha);
		}
		this.renderer.rect2f(x, y, x + width, y + height);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (isFocused()) return true;

		boolean b = this.isInside(x, y) && action == GLFW.GLFW_PRESS;
		if (b) o_ignoreFirstClick = true;
		return b;
	}

	@Override
	public void mouseEventOver(int button, int action, double cursorX, double cursorY) {
		if (o_ignoreFirstClick && action == GLFW.GLFW_RELEASE) {
			o_ignoreFirstClick = false;
			return;
		}

		if (button == GLFW.GLFW_MOUSE_BUTTON_3) {
			//noinspection AssignmentUsedAsCondition
			if (this.o_scrolling = action != GLFW.GLFW_RELEASE) {
				this.o_scrollStartY = this.i.getMouser().getY();
			}
		}

		if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE) {
			float x0 = this.getX();
			float y0 = this.o_scroll.get();
			float optionHeight = this.getHeight();
			float bgHeight = optionHeight * Math.min(this.o_maxElementDisplay, this.options.length);
			
			cursorY -= this.getY() + this.getHeight();

			for (T option : this.options) {
				if (y0 > bgHeight) break;

				if (y0 >= 0) {
					if (cursorX > x0 && cursorY > y0 &&
						cursorX < x0 + getWidth() && cursorY < y0 + optionHeight
					) {
						setSelected(option);
						break;
					}
				}

				y0 += optionHeight;
			}

			this.setFocused(false);
		}
	}

	@Override
	public void cursorPosition(double x, double y) {
		if (this.isFocused()) {
			float x0 = 0;
			float y0 = this.o_scroll.get();
			float optionHeight = this.getHeight();

			y -= this.getHeight();

			for (T option : this.options) {
				if (x0 < x && y0 < y && x0 + getWidth() > x && y0 + optionHeight > y) {
					this.o_hovering = option;
					break;
				}

				y0 += optionHeight;
			}
		} else {
			this.o_hovering = null;
		}
	}

	@Override
	public void scrollOver(double x, double y) {
		this.o_scroll.onScrollEvent(y);
	}

	private void setSelected(T selected) {
		this.selected = selected;
		if (changeEvent != null)
			changeEvent.accept(this.selected);
		if (itemChangeEvent != null)
			itemChangeEvent.onDropdownChange(ListUtil.indexOf(this.options, this.selected), this.selected, this);
	}

	@Override
	public void renderOver(float offsetX, float offsetY, float alpha) {
		float x = (float) Math.floor(this.getX() + offsetX);
		float y = (float) Math.floor(this.getY() + offsetY + this.getHeight());

		float optionHeight = this.getHeight();
		float width = this.getWidth();
		float bgHeight = optionHeight * Math.min(this.o_maxElementDisplay, this.options.length);
		if (y + bgHeight > this.i.getWindowHeight()) {
			bgHeight = this.i.getWindowHeight() - y;
		}

		float y2 = y + bgHeight;

		// Shared with CroppedBox
		if (this.o_scrolling) {
			double current = this.i.getMouser().getY();
			double previous = o_scrollStartY;
			double s = (previous - current) / o_scrollSensitivity * (this.i.getDeltaTime() * 20F);
			this.o_scroll.onScrollEvent(s);
		}

		this.o_scroll.update();
		this.o_scroll.setMaxSize(bgHeight);
		this.o_scroll.setSize(optionHeight * this.options.length);

		this.renderOverBackground(alpha, x, y, width, bgHeight);

		// (flooring fixes weird font rendering)
		float fontX = (float) Math.floor(x + 10);
		float fontOffsetY = (optionHeight - font.getHeight()) / 2f + 1F;

		// Main text
		this.renderer.color(1, 1, 1, alpha);

		this.cropContext.startCropping(x, y, width, bgHeight);

		float oy = y + o_scroll.get();
		for (T option : this.options) {
			if (oy + optionHeight > y && oy <= y2) {
				if (option == selected) {
					this.renderer.color(0, 0, 0, alpha * 0.25F);
					this.renderer.rect2f(x, oy, x + width, oy + optionHeight);
					this.renderer.color(1, 1, 1, alpha);
				} else if (option == this.o_hovering) {
					this.renderer.color(1, 1, 1, alpha * 0.15F);
					this.renderer.rect2f(x, oy, x + width, oy + optionHeight);
					this.renderer.color(1, 1, 1, alpha);
				}

				this.fontRenderer.drawString(this.font, getOptionStr(option), Math.round(fontX), Math.round(oy + fontOffsetY));
			}
			oy += optionHeight;
		}

		this.cropContext.stopCropping();
	}

	private void renderOverBackground(float alpha, float x, float y, float width, float height) {
		this.renderer.color(0.20F, 0.20F, 0.20F, alpha);
		this.renderer.rect2f(x, y, x + width, y + height);
	}

	protected char[] getOptionStr(T o) {
		if (this.renderStringGet != null) {
			return this.renderStringGet.apply(o);
		}

		return o.toString().toCharArray();
	}

	public T getSelected() {
		return selected;
	}

	@Contract("_ -> this")
	@NotNull
	public DropdownComponent<T> setOptions(@NotNull T @NotNull [] options) {
		this.options = options;

		for (T option : options) {
			if (Objects.equals(selected, option)) {
				return this;
			}
		}

		// In case the selected option isn't on the options list
		// TODO: This might not be desired
		this.setSelected(options[0]);
		return this;
	}

	@Override
	public void setFocused(boolean focused) {
		if (focused) {
			this.o_scroll.setScroll(-ListUtil.indexOf(this.options, this.selected) * this.getHeight(), true);
			this.o_hovering = null;
		}
		super.setFocused(focused);
	}

	public interface ChangeEvent<T> {
		void onDropdownChange(int index, T value, DropdownComponent<T> component);
	}
}
