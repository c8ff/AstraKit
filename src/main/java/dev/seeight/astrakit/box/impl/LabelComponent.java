package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class LabelComponent extends ComponentBox {
	public final IFontRenderer fontRenderer;
	public IFont font;
	private char[] text;

	public Runnable click;

	public double r = 1;
	public double g = 1;
	public double b = 1;

	public LabelComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, String text) {
		this(i, font, fontRenderer, text.toCharArray());
	}

	public LabelComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, char[] text) {
		super(i);
		this.fontRenderer = fontRenderer;
		this.text = text;
		this.setFont(font); // Update size
	}

	public LabelComponent setClick(Runnable click) {
		this.click = click;
		return this;
	}

	public LabelComponent setFont(IFont font) {
		this.font = font;
		this.setMinWidth((float) Math.ceil(this.fontRenderer.getWidthFloat(this.font, this.getText())));
		this.setMinHeight((float) Math.ceil(this.fontRenderer.getHeightFloat(this.font, this.getText())));
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());
		return this;
	}

	public LabelComponent setColor(Color color) {
		return this.setColor(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
	}

	public LabelComponent setColor(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = (float) Math.floor(this.getY() + offsetY + (this.getHeight() - this.font.getHeight() + 2) / 2f);

		this.renderer.color(r, g, b, alpha);
		this.fontRenderer.drawString(this.font, this.getText(), x, y);
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

	public void setText(char[] text) {
		this.text = text;
		this.setFont(font);
	}

	public char[] getText() {
		return this.text;
	}
}
