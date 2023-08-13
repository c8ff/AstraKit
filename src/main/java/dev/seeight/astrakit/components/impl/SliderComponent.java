package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import dev.seeight.util.MathUtil;
import org.lwjgl.glfw.GLFW;

public class SliderComponent extends Component {
	private boolean hold;
	public float value;
	public float min;
	public float max;

	private int mx;
	private int my;

	private boolean shift;

	public SliderComponent(float value, float min, float max) {
		this.value = value;
		this.min = min;
		this.max = max;
	}

	@Override
	public void mouseButtonEvent(int x, int y, int button, int action) {
		if (isInside(x, y) || hold) {
			hold = action != GLFW.GLFW_RELEASE;
		}
	}

	@Override
	public void cursorPositionEvent(int x, int y) {
		mx = x;
		my = y;

		if (hold) {
			double v = MathUtil.clamp(1 - (this.width - (x - (this.x + this.parentX))) / this.width, 0, 1);
			if (shift) {
				v = Math.round(v * 15F) / 15F;
			}
			this.setValue((float) (min + (max - min) * v));
		}
	}

	@Override
	public void scrollEvent(double x, double y) {
		boolean inside = isInside(mx, my);
		if (inside) {
			y = MathUtil.clamp(y, -1, 1);
			this.setValue((float) (this.value + y));
		}

	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		if (key == GLFW.GLFW_KEY_LEFT_SHIFT) {
			shift = action != GLFW.GLFW_RELEASE;
		}
	}

	public void setValue(float value) {
		this.value = MathUtil.clamp(value, this.min, this.max);
	}

	public void setMin(float min) {
		this.min = min;
	}

	public void setMax(float max) {
		this.max = max;
	}

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderSlider(this, alpha);
	}
}
