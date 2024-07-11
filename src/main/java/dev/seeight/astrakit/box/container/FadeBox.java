package dev.seeight.astrakit.box.container;

import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.UIBoxContext;

public class FadeBox extends ComponentBox {
	private final ComponentBox child;

	private float anim;
	private float speed = 1;
	private boolean visible;

	public FadeBox(UIBoxContext i, ComponentBox child) {
		super(i);
		this.child = child;

		this.setWidth(child.getWidth());
		this.setHeight(child.getHeight());
	}

	public FadeBox setAnim(float anim) {
		this.anim = anim;
		return this;
	}

	public FadeBox setSpeed(float speed) {
		this.speed = speed;
		return this;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (visible) {
			anim += (float) (this.i.getDeltaTime() * 1000F * speed);
		} else {
			anim -= (float) (this.i.getDeltaTime() * 1000F * speed);
		}

		if (anim > 250F) {
			anim = 250F;
		} else if (anim < 0) {
			anim = 0;
		}

		float a = alpha * (anim / 250F);
		if (a > 0.05F) {
			this.child.render(offsetX, offsetY, a);
		}
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.child.setWidth(width);
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		this.child.setHeight(height);
	}
}
