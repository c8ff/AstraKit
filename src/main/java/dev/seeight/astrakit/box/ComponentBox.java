package dev.seeight.astrakit.box;

import dev.seeight.renderer.renderer.Renderer;

public class ComponentBox extends Component {
	protected final UIBoxContext i;
	protected final Renderer renderer;

	public ComponentBox(UIBoxContext i) {
		this.i = i;
		this.renderer = i.getRenderer();
	}

	@Override
	public boolean isOutsideView(float offsetX, float offsetY) {
		float x = offsetX + this.getX();
		if (x + this.getWidth() < 0) {
			return true;
		}

		if (x > this.i.getWindowWidth()) {
			return true;
		}

		float y = offsetY + this.getY();
		if (y + this.getHeight() < 0) {
			return true;
		}

		if (y > this.i.getWindowHeight()) {
			return true;
		}

		return false;
	}
}
