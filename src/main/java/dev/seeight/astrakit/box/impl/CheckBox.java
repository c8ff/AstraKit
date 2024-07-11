package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.util.Scroll2;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import org.lwjgl.glfw.GLFW;

public class CheckBox extends ComponentBox {
	private final Texture checkboxTexture;

	public final char[] text;
	public final IFont font;
	public final IFontRenderer fontRenderer;

	private boolean value;

	private float animation;

	public ChangeEvent statusChangeEvent;

	public CheckBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, String text, boolean value) {
		this(i, font, fontRenderer, text.toCharArray(), value);
	}

	public CheckBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, char[] text, boolean value) {
		super(i);
		this.checkboxTexture = i.getBoxResources().getCheckboxTexture();
		this.text = text;
		this.font = font;
		this.fontRenderer = fontRenderer;
		this.value = value;

		this.setMinWidth((float) (18 + 10 + Math.floor(this.fontRenderer.getWidthFloat(this.font, text))));
		this.setMinHeight(Math.max(18, this.font.getHeight()));
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = (float) Math.floor(this.getX() + offsetX);
		float y = (float) Math.floor(this.getY() + offsetY);

		// box
		{
			if (this.value) {
				this.animation = Scroll2.animate(this.animation, 255, this.i.getDeltaTime() * 250F);
			} else {
				this.animation = Scroll2.animate(this.animation, 0, this.i.getDeltaTime() * 250F);
			}

			float anim = this.animation / 255F;
			float anim0 = 1F - anim;

			Texture t = this.checkboxTexture;
			float boxX = (float) Math.floor(x);
			float boxY = (float) Math.floor(y + (this.getHeight() - t.getHeight()) / 2f + 1);
			float boxX2 = boxX + 18;
			float boxY2 = boxY + t.getHeight();

			float u = 0F / t.getWidth();
			float u2 = u + 18F / t.getWidth();

			if (anim0 > 0.1F) {
				this.renderer.color(1, 1, 1, alpha * anim0);
				this.renderer.texRect2f(t, boxX, boxY, boxX2, boxY2, u, 0, u2, 1F);
			}

			if (anim > 0.1F) {
				float u0 = 18.0f / t.getWidth();
				float u20 = u0 + 18F / t.getWidth();

				this.renderer.color(1, 1, 1, alpha * anim);
				this.renderer.texRect2f(t, boxX, boxY, boxX2, boxY2, u0, 0, u20, 1F);

				u = (18F * 2) / t.getWidth();
				u2 = u + 18F / t.getWidth();

				this.renderer.color(0, 0, 0, alpha * anim);
				this.renderer.texRect2f(t, boxX, boxY, boxX2, boxY2, u, 0, u2, 1F);
			}

			x = boxX2;
		}

		// Text
		{
			float textX = x + 10;
			float textY = (float) Math.floor(y + (this.getHeight() - this.font.getHeight()) / 2f + 2);

			this.renderer.color(1, 1, 1, alpha);
			this.fontRenderer.drawString(this.font, this.text, textX, textY);
		}
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (isInside(x, y)) {
			if (this.i.getMouser().isLeft(button) && action == GLFW.GLFW_PRESS) {
				value = !value;
				if (statusChangeEvent != null) {
					statusChangeEvent.ev(value);
				}
			}

			return true;
		}

		return false;
	}

	public CheckBox set(boolean value) {
		boolean v = this.value != value;
		this.value = value;
		if (statusChangeEvent != null && v) statusChangeEvent.ev(value);

		return this;
	}

	public CheckBox setStatusChangeEvent(ChangeEvent statusChangeEvent) {
		this.statusChangeEvent = statusChangeEvent;
		return this;
	}

	@Override
	public float getMinWidth() {
		return super.getMinWidth();
	}

	@Override
	public void setMinHeight(float minHeight) {
		super.setMinHeight(minHeight);
	}

	public interface ChangeEvent {
		void ev(boolean value);
	}
}
