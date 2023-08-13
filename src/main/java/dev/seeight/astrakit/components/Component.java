package dev.seeight.astrakit.components;

import dev.seeight.astrakit.IComponentRenderer;

public class Component {
	public float parentX;
	public float parentY;
	public float x;
	public float y;
	public float width;
	public float height;
	protected boolean focused;

	public Component() {
	}

	public Component setSize(float width, float height) {
		this.width = width;
		this.height = height;

		return this;
	}

	public boolean isInside(float x, float y) {
		return x > this.x + this.parentX && y > this.y + this.parentY && x < this.x + this.parentX + this.width && y < this.y + this.parentY + this.height;
	}

	public boolean shouldFocus(int x, int y) {
		return isInside(x, y);
	}

	public void unfocus() {
		focused = false;
	}

	public void focus() {
		focused = true;
	}

	public boolean isFocused() {
		return focused;
	}

	public void keyEvent(int key, int action, int mods) {
	}

	public void charEvent(char character) {
	}

	public void cursorPositionEvent(int x, int y) {
	}

	public void mouseButtonEvent(int x, int y, int button, int action) {
	}

	public void scrollEvent(double x, double y) {
	}

	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderDefault(this, alpha);
	}
}
