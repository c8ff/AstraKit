package dev.seeight.astrakit.box;

import dev.seeight.astrakit.box.layout.Axis;
import dev.seeight.astrakit.box.layout.Sizing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Component extends FloatRect {
	@NotNull
	protected Sizing sizingX = Sizing.MIN;
	protected Sizing sizingY = Sizing.MIN;
	protected float minWidth;
	protected float minHeight;
	private boolean focused;

	public void setMinWidth(float minWidth) {
		this.minWidth = minWidth;
	}

	public void setMinHeight(float minHeight) {
		this.minHeight = minHeight;
	}

	public float getMinWidth() {
		return minWidth;
	}

	public float getMinHeight() {
		return minHeight;
	}

	public Sizing getSizing(Axis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.sizingX;
			case VERTICAL -> this.sizingY;
		};
	}

	public void setPosition(Axis axis, float value) {
		switch (axis) {
			case HORIZONTAL -> this.setX(value);
			case VERTICAL -> this.setY(value);
			default -> throw new IllegalArgumentException("Unexpected value: " + axis);
		}
	}

	public void setSize(Axis axis, float value) {
		switch (axis) {
			case HORIZONTAL -> this.setWidth(value);
			case VERTICAL -> this.setHeight(value);
			default -> throw new IllegalArgumentException("Unexpected value: " + axis);
		}
	}

	public float getSize(Axis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.getWidth();
			case VERTICAL -> this.getHeight();
		};
	}

	public float getMinSize(Axis axis) {
		return switch (axis) {
			case HORIZONTAL -> this.getMinWidth();
			case VERTICAL -> this.getMinHeight();
		};
	}

	@Deprecated
	public Component setSizing(@NotNull Sizing sizing) {
		this.sizingX = sizing;
		return this;
	}

	public Component setSizing(@Nullable Sizing x, @Nullable Sizing y) {
		this.setSizingX(x);
		this.setSizingY(y);
		return this;
	}

	public Component setSizingX(@Nullable Sizing x) {
		this.sizingX = x == null ? Sizing.MIN : x;
		return this;
	}

	public Component setSizingY(@Nullable Sizing y) {
		this.sizingY = y == null ? Sizing.MIN : y;
		return this;
	}

	public void render(float offsetX, float offsetY, float alpha) {

	}

	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		return false;
	}

	public void keyEvent(int key, int action, int mods) {
	}

	public boolean scrollEvent(double x, double y) {
		return false;
	}

	public void charEvent(int codepoint) {
	}

	/**
	 * @param x The X position relative to the object. Zero being the left of the component.
	 * @param y The Y position relative to the object. Zero being the top of the component.
	 */
	public void cursorPosition(double x, double y) {
	}

	public boolean isDirty() {
		return true;
	}

	public abstract boolean isOutsideView(float offsetX, float offsetY);

	public boolean isFocused() {
		return focused;
	}

	public void setFocused(boolean focused) {
		this.focused = focused;
	}
}
