package dev.seeight.astrakit.box;

public class FloatRect implements IFloatRect {
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	@Override
	public void setX(float x) {
		this.x = x;
	}

	@Override
	public float getX() {
		return this.x;
	}

	@Override
	public void setY(float y) {
		this.y = y;
	}

	@Override
	public float getY() {
		return this.y;
	}

	@Override
	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public float getWidth() {
		return this.width;
	}

	@Override
	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public float getHeight() {
		return this.height;
	}
}
