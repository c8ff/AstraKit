package dev.seeight.astrakit.box;

import org.jetbrains.annotations.Contract;

public interface IFloatRect {
	void setX(float x);
	float getX();

	void setY(float y);
	float getY();

	void setWidth(float width);
	float getWidth();

	void setHeight(float height);
	float getHeight();

	@Contract("_, _ -> this")
	@SuppressWarnings("UnusedReturnValue")
	default IFloatRect setPosition(float x, float y) {
		this.setX(x);
		this.setY(y);
		return this;
	}

	@Contract("_, _ -> this")
	@SuppressWarnings("UnusedReturnValue")
	default IFloatRect setSize(float width, float height) {
		this.setWidth(width);
		this.setHeight(height);
		return this;
	}

	@Contract(value = "_, _, _, _ -> this")
	default IFloatRect setDimensions(float x, float y, float width, float height) {
		this.setPosition(x, y);
		this.setSize(width, height);
		return this;
	}

	default boolean isInside(double pointX, double pointY) {
		return getX() < pointX && getY() < pointY && getX() + getWidth() > pointX && getY() + getHeight() > pointY;
	}
}
