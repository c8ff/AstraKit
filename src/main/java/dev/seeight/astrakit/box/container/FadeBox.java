/*
 * AstraKit
 * Copyright (C) 2024 c8ff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
