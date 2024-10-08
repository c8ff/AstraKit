package dev.seeight.astrakit.box;

import dev.seeight.astrakit.box.container.CroppedBox;
import dev.seeight.astrakit.box.container.ParentBox;
import dev.seeight.astrakit.box.container.TabBox;
import dev.seeight.astrakit.box.impl.*;
import dev.seeight.astrakit.box.layout.Axis;
import dev.seeight.astrakit.box.layout.Layout;
import dev.seeight.astrakit.box.layout.SpreadLayout;
import dev.seeight.astrakit.box.layout.StackLayout;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Consumer;

public class BoxBuilder {
	protected final UIBoxContext context;

	protected @NotNull IFont font;
	protected @NotNull IFontRenderer fontRenderer;

	protected float margin = 10;
	protected float tabLabelMargin = 14;

	public BoxBuilder(UIBoxContext context, @NotNull IFont defaultFont, @NotNull IFontRenderer defaultFontRenderer) {
		this.context = context;

		this.font = defaultFont;
		this.fontRenderer = defaultFontRenderer;
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

		return new CroppedBox(context, box, axis);
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
		return new DropdownComponent<>(context, font, fontRenderer, margin, i, values);
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
