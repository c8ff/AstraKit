package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;

public class SkipNewLineComponent extends Component {
	public static SkipNewLineComponent INSTANCE = new SkipNewLineComponent();

	private SkipNewLineComponent() {
	}

	@Override
	public Component setSize(float width, float height) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isInside(float x, float y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean shouldFocus(int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void unfocus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void focus() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isFocused() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void charEvent(char character) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cursorPositionEvent(int x, int y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void mouseButtonEvent(int x, int y, int button, int action) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void scrollEvent(double x, double y) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		throw new UnsupportedOperationException();
	}
}
