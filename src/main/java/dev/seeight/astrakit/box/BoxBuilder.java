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

import dev.seeight.astrakit.box.container.CroppedBox;
import dev.seeight.astrakit.box.container.ParentBox;
import dev.seeight.astrakit.box.container.TabBox;
import dev.seeight.astrakit.box.crop.GenericOpenGLCropContext;
import dev.seeight.astrakit.box.crop.ICropContext;
import dev.seeight.astrakit.box.impl.*;
import dev.seeight.astrakit.box.layout.Axis;
import dev.seeight.astrakit.box.layout.Layout;
import dev.seeight.astrakit.box.layout.SpreadLayout;
import dev.seeight.astrakit.box.layout.StackLayout;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import dev.seeight.renderer.renderer.gl.OpenGLRenderer2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.util.function.Consumer;

public class BoxBuilder {
	protected final ICropContext cropContext;
	protected final UIBoxContext context;

	protected @NotNull IFont font;
	protected @NotNull IFontRenderer fontRenderer;

	protected float margin = 10;
	protected float tabLabelMargin = 14;

	public BoxBuilder(UIBoxContext context, @Nullable ICropContext cropContext, @NotNull IFont defaultFont, @NotNull IFontRenderer defaultFontRenderer) {
		this.context = context;
		this.font = defaultFont;
		this.fontRenderer = defaultFontRenderer;

		if (cropContext == null) {
			try {
				boolean invertedY = true;
				if (this.context.getRenderer() instanceof OpenGLRenderer2 s) {
					invertedY = s.INVERT_V_COORDINATES;
				}

				// Will throw if the current thread doesn't have an OpenGL context.
				GL.getCapabilities();
				this.cropContext = new GenericOpenGLCropContext(context, invertedY);
			} catch (Exception e) {
				throw new RuntimeException("No crop context implementation available.");
			}
		} else {
			this.cropContext = cropContext;
		}
	}

	public ICropContext getCropContext() {
		return cropContext;
	}

	public UIBoxContext getContext() {
		return context;
	}

	public BoxBuilder setFont(@NotNull IFont font) {
		this.font = font;
		return this;
	}

	public BoxBuilder setFontRenderer(@NotNull IFontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		return this;
	}

	public void setMargin(float margin) {
		this.margin = margin;
	}

	public BoxBuilder setTabLabelMargin(float tabLabelMargin) {
		this.tabLabelMargin = tabLabelMargin;
		return this;
	}

	public CroppedBox cropped(ParentBox box) {
		Axis axis = Axis.VERTICAL;

		if (box.getLayout() instanceof StackLayout k) {
			axis = k.axis;
		} else if (box.getLayout() instanceof SpreadLayout) {
			axis = Axis.HORIZONTAL;
		}

		return new CroppedBox(cropContext, context, box, axis);
	}

	public TabBox tabs(TabBox.Tab... tabs) {
		return tabs(0, tabs);
	}

	public TabBox tabs(int index, TabBox.Tab... tabs) {
		return new TabBox(context, font, fontRenderer, this.tabLabelMargin, index, tabs);
	}

	public ParentBox box(Layout layout, Component... boxes) {
		return new ParentBox(context, layout).addBoxes(boxes);
	}

	public LabelComponent label(String str) {
		return new LabelComponent(context, font, fontRenderer, str);
	}

	public LabelComponent label(String str, IFont font) {
		return new LabelComponent(context, font, fontRenderer, str).setFont(font);
	}

	public LabelComponent label(String str, Color color) {
		return new LabelComponent(context, font, fontRenderer, str).setColor(color);
	}

	public LabelComponent label(String str, Color color, Runnable click) {
		return new LabelComponent(context, font, fontRenderer, str).setColor(color).setClick(click);
	}

	public LabelComponent label(String str, IFont font, Color color) {
		return new LabelComponent(context, font, fontRenderer, str).setFont(font).setColor(color);
	}

	public WrappedTextBox wrapped(String text) {
		return this.wrapped(text, this.font);
	}

	public WrappedTextBox wrapped(String text, IFont font) {
		return new WrappedTextBox(context, fontRenderer, font, () -> text);
	}

	public WrappedTextBox wrapped(String text, float width) {
		WrappedTextBox f = new WrappedTextBox(context, fontRenderer, font, () -> text);
		f.setWidth(width);
		return f;
	}

	public ButtonBox button(String str, Runnable action) {
		return new ButtonBox(context, font, fontRenderer, str, this.margin, action);
	}

	@SafeVarargs
	public final <T> DropdownComponent<T> dropdown(int i, T... values) {
		return new DropdownComponent<>(context, font, fontRenderer, margin, cropContext, i, values);
	}

	public ComponentBox checkbox(String string, boolean value, CheckBox.ChangeEvent changeEvent) {
		return new CheckBox(context, font, fontRenderer, string, value).setStatusChangeEvent(changeEvent);
	}

	public TextFieldComponent textField() {
		return new TextFieldComponent(context, font, fontRenderer, "", null, margin);
	}

	public TextFieldComponent textField(String str, String emptyText) {
		return new TextFieldComponent(context, font, fontRenderer, str, emptyText, margin);
	}

	public SliderBox intSlider(int value, int min, int max) {
		return new SliderBox(context, font, fontRenderer, margin, new SliderBox.IntVal(value, min, max));
	}

	public SliderBox intSlider(int value, int min, int max, Consumer<Integer> onChange) {
		return new SliderBox(context, font, fontRenderer, margin, new SliderBox.IntVal(value, min, max).setChangeEvent(onChange));
	}

	public SliderBox floatSlider(float value, float min, float max) {
		return new SliderBox(context, font, fontRenderer, margin, new SliderBox.FloatVal(value, min, max));
	}

	public SliderBox floatSlider(float value, float min, float max, Consumer<Float> onChange) {
		return new SliderBox(context, font, fontRenderer, margin, new SliderBox.FloatVal(value, min, max).setChangeEvent(onChange));
	}

	public TextureBox texture(Texture texture) {
		return texture(texture, Math.min(texture.getWidth(), texture.getHeight()));
	}

	public TextureBox texture(Texture texture, Color color) {
		return texture(texture, Math.min(texture.getWidth(), texture.getHeight())).setColor(color);
	}

	public TextureBox texture(Texture texture, int size) {
		return new TextureBox(context, texture, size);
	}

	public FillerBox filler() {
		return new FillerBox();
	}
}
