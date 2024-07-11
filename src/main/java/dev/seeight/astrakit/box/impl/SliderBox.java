package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.renderer.renderer.Texture;
import dev.seeight.util.MathUtil;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

public class SliderBox extends ComponentBox {
	protected final Texture smallTriangleTexture;
	protected final Texture markerTexture;

	protected final float dialogMargin;
	protected final NumberVal<? extends Number> value;

	protected final IFontRenderer fontRenderer;
	protected final IFont labelFont;

	protected @Nullable Runnable startDragEvent;
	protected @Nullable DoubleConsumer dragEvent;
	protected @Nullable DoubleConsumer stopDragEvent;

	private float renderX;
	private float renderX2;

	private float labelAlpha;

	public SliderBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, float dialogMargin, NumberVal<? extends Number> value) {
		super(i);
		this.smallTriangleTexture = i.getBoxResources().getSmallTriangleTexture();
		this.markerTexture = i.getBoxResources().getMarkerTexture();
		this.dialogMargin = dialogMargin;
		this.value = value;
		this.fontRenderer = fontRenderer;
		this.labelFont = font;

		this.setMinWidth(100); // Arbritrary number lol
		this.setMinHeight(this.getBarHeightPx() + markerTexture.getHeight());
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());
	}

	public SliderBox setStartDragEvent(@Nullable Runnable startDragEvent) {
		this.startDragEvent = startDragEvent;
		return this;
	}

	public SliderBox setDragEvent(@Nullable DoubleConsumer dragEvent) {
		this.dragEvent = dragEvent;
		return this;
	}

	public SliderBox setStopDragEvent(@Nullable DoubleConsumer stopDragEvent) {
		this.stopDragEvent = stopDragEvent;
		return this;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		this.renderX = offsetX + this.getX();
		this.renderX2 = offsetX + this.getX() + this.getWidth();

		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.renderX;
		float y = offsetY + this.getY();

		double min = this.value.min.doubleValue();
		double max = this.value.max.doubleValue();
		double val = this.value.value.doubleValue();
		double progress = (val - min) / (max - min);

		// Calculate bar position
		float barHeight = this.getBarHeightPx(); // px
		float barY = y + (this.getHeight() - barHeight) / 2f;
		float barY2 = barY + barHeight;

		float progressiveWidth = (float) (this.getWidth() * progress);

		// Calculate marker position
		Texture marker = this.markerTexture;
		float markerX = (float) Math.floor(x + (progressiveWidth - marker.getWidth() / 2F));
		float markerY = barY + (barHeight - marker.getHeight()) / 2f;

		// Progress indicators
		this.renderer.color(0.20F, 0.20F, 0.20F, alpha);
		this.renderer.rect2f(x, barY, x + this.getWidth(), barY2);
		this.renderer.color(1, 1, 1, alpha);
		this.renderer.rect2f(x, barY, x + progressiveWidth, barY2);

		// Marker
		this.renderer.texRect2f(marker, markerX, markerY, markerX + marker.getWidth(), markerY + marker.getHeight());

		// Label for when the value is changed.
		if (this.isFocused() || this.labelAlpha > 0.1F) {
			// Animate fade-in or fade-out.
			if (!this.isFocused()) {
				this.labelAlpha -= (float) (this.i.getDeltaTime() * 1000F);
			} else {
				this.labelAlpha += (float) (this.i.getDeltaTime() * 1000F);
			}
			this.labelAlpha = MathUtil.clamp(this.labelAlpha, 0, 200F);

			// Calculate alpha
			float a = alpha * (labelAlpha / 200F);

			char[] valueStr = this.value.asString().toCharArray();
			float width = this.fontRenderer.getWidthFloat(this.labelFont, valueStr) + this.dialogMargin * 2 + 2;
			float height = this.labelFont.getHeight() + this.dialogMargin * 2;
			float px = x + progressiveWidth - width / 2f;
			float py = (float) Math.floor(y - height - this.getTriangleHeight() - this.dialogMargin);

			this.renderBackground(px, py, width, height, a);
			this.renderTriangle(x, py, progressiveWidth, height);
			this.renderText(valueStr, px, py, a);
		}
	}

	protected float getTriangleHeight() {
		return this.smallTriangleTexture.getHeight();
	}

	protected void renderTriangle(float x, float py, float progressiveWidth, float height) {
		Texture triangle = this.smallTriangleTexture;

		float x1 = x + progressiveWidth - triangle.getWidth() / 2f;
		float x12 = x1 + triangle.getWidth();
		this.renderer.texRect2f(triangle, x1, py + height, x12, py + height + triangle.getHeight());
	}

	protected void renderText(char[] chars, float x, float y, float alpha) {
		this.renderer.color(1F, 1F, 1F, alpha);
		this.fontRenderer.drawString(this.labelFont, chars, x + dialogMargin, y + dialogMargin);
	}

	private void renderBackground(float x, float y, float width, float height, float alpha) {
		// Background
		this.renderer.color(0.15F, 0.15F, 0.15F, alpha);
		this.renderer.rect2f(x, y, x + width, y + height);
	}

	@Override
	public void cursorPosition(double x, double y) {
		double min = this.value.min.doubleValue();
		double max = this.value.max.doubleValue();

		double def = x / this.getWidth();
		double mouseProgress = MathUtil.clamp(def, 0, 1);

		// Approach decimal values into 1/2s or 1/4s when a modified key is down.
		// This isn't required on integer values as they are already round.
		if (!(this.value instanceof IntVal)) {
			if (this.i.getKeyboarder().isShiftDown()) {
				mouseProgress = Math.round(mouseProgress * 20) / 20D;
			} else if (this.i.getKeyboarder().isControlDown()) {
				mouseProgress = Math.round(mouseProgress * 40) / 40D;
			}
		}

		double progress = mouseProgress * (max - min) + min;
		this.onDrag(progress);
		this.value.setDouble(progress);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (action == GLFW.GLFW_RELEASE) {
			if (this.isFocused()) {
				this.onStopDragging(this.value.value.doubleValue());
			}
			return false;
		}

		if (isInside(x, y)) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_PRESS) {
				this.onStartDragging();
				this.cursorPosition(x - this.getX(), y - this.getY());
			}
			return true;
		}

		return false;
	}

	protected void onStartDragging() {
		if (this.startDragEvent != null) {
			this.startDragEvent.run();
		}
	}

	protected void onDrag(double value) {
		if (this.dragEvent != null) {
			this.dragEvent.accept(value);
		}
	}

	protected void onStopDragging(double value) {
		if (this.stopDragEvent != null) {
			this.stopDragEvent.accept(value);
		}
	}

	protected float getBarHeightPx() {
		return 2;
	}

	/**
	 * Wrapper class to represent a changeable number within a range.
	 */
	public static abstract class NumberVal<T extends Number> {
		protected T value;
		protected T min;
		protected T max;

		protected Consumer<T> change;

		public NumberVal(T value, T min, T max) {
			if (Objects.equals(min, max)) throw new IllegalArgumentException("min cannot be the same as max.");
			if (min.doubleValue() >= max.doubleValue()) throw new IllegalArgumentException("min cannot be bigger than max.");

			this.min = min;
			this.max = max;
			this.value = this.clamp(value);
		}

		public NumberVal<T> setChangeEvent(Consumer<T> change) {
			this.change = change;
			if (change != null) {
				this.change.accept(this.value);
			}
			return this;
		}

		public void set(T value) {
			boolean changed = !Objects.equals(this.value, value);
			this.value = this.clamp(value);
			if (changed && this.change != null) {
				this.change.accept(this.value);
			}
		}

		protected abstract T clamp(T value);

		protected abstract void setDouble(double value);

		public String asString() {
			if (this.value instanceof Integer) {
				return String.valueOf(this.value.intValue());
			} else if (this.value instanceof Float) {
				return String.valueOf(Math.floor(this.value.floatValue() * 100) / 100F);
			}

			return String.valueOf(this.value.doubleValue());
		}
	}

	public static class IntVal extends NumberVal<Integer> {
		public IntVal(int value, int min, int max) {
			super(value, min, max);
		}

		@Override
		protected Integer clamp(Integer value) {
			return MathUtil.clamp(value, this.min, this.max);
		}

		@Override
		protected void setDouble(double value) {
			this.set((int) value);
		}
	}

	public static class FloatVal extends NumberVal<Float> {
		public FloatVal(float value, float min, float max) {
			super(value, min, max);
		}

		@Override
		protected Float clamp(Float value) {
			return MathUtil.clamp(value, this.min, this.max);
		}

		@Override
		protected void setDouble(double value) {
			this.set((float) value);
		}
	}
}
