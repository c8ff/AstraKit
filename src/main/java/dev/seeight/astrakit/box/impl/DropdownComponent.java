package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public class DropdownComponent<T> extends ComponentBox implements Dropdown {
	private final Texture dropdownTexture;

	private @NotNull T @NotNull [] options;
	private T selected;

	public final IFontRenderer fontRenderer;
	public final IFont font;

	private Function<T, char[]> renderStringGet;

	private Consumer<T> changeEvent;

	@SafeVarargs
	public DropdownComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, float margin, int selected, T... options) {
		super(i);
		this.dropdownTexture = i.getBoxResources().getDropdownTexture();
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
	}

	public DropdownComponent<T> setRenderStringGet(Function<T, char[]> renderStringGet) {
		this.renderStringGet = renderStringGet;
		return this;
	}

	public DropdownComponent<T> setChangeEvent(Consumer<T> changeEvent) {
		this.changeEvent = changeEvent;
		return this;
	}

	public DropdownComponent<T> set(T selected) {
		this.selected = selected;
		if (changeEvent != null)
			changeEvent.accept(selected);
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
		return this.isInside(x, y) && action == GLFW.GLFW_PRESS;
	}

	@Override
	public void mouseEventOver(int button, int action, double x, double y) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
			float x0 = this.getX();
			float y0 = this.getY() + this.getHeight();
			float optionHeight = this.getHeight();

			for (T option : this.options) {
				if (x0 < x && y0 < y && x0 + getWidth() > x && y0 + optionHeight > y) {
					this.selected = option;
					if (changeEvent != null)
						changeEvent.accept(selected);
					break;
				}

				y0 += optionHeight;
			}

			this.setFocused(false);
		}
	}

	@Override
	public void renderOver(float offsetX, float offsetY, float alpha) {
		float x = (float) Math.floor(this.getX() + offsetX);
		float y = (float) Math.floor(this.getY() + offsetY + this.getHeight());

		float optionHeight = this.getHeight();
		float width = this.getWidth();
		float bgHeight = optionHeight * this.options.length;

		this.renderOverBackground(alpha, x, y, width, bgHeight);

		// (flooring fixes weird font rendering)
		float fontX = (float) Math.floor(x + 10);
		float fontOffsetY = (optionHeight - font.getHeight()) / 2f + 1F;

		// Main text
		this.renderer.color(1, 1, 1, alpha);

		for (T option : this.options) {
			this.fontRenderer.drawString(this.font, getOptionStr(option), fontX, y + fontOffsetY);
			y += optionHeight;
		}
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

}
