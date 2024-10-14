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

package dev.seeight.astrakit.box.util;

import dev.seeight.astrakit.box.UIBoxContext;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.lwjgl.glfw.GLFW;

public class Scroll2 {
	private final UIBoxContext i;

	protected float scroll = 0;
	protected float animatedScroll = 0;

	protected float size = 0;

	protected double lastLoadedTime;
	protected double reachBottomCoolDown = 1; // seconds (or the value of GLFW.glfwGetTime)

	protected Runnable reachBottomEvent;
	protected Runnable reachTopEvent;

	/**
	 * The size of the display. Depending on this being a horizontal scroll or vertical scroll. Width or height.
	 */
	protected float maxSize;

	protected boolean wasInBounds = true;

	public Scroll2(UIBoxContext i) {
		this.i = i;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public void setMaxSize(float maxSize) {
		this.maxSize = maxSize;
	}

	@Contract("_->this")
	public Scroll2 setReachTopEvent(Runnable reachTopEvent) {
		this.reachTopEvent = reachTopEvent;
		return this;
	}

	@Contract("_->this")
	public Scroll2 setReachBottomEvent(Runnable event) {
		this.reachBottomEvent = event;
		return this;
	}

	@Contract("_->this")
	public Scroll2 setReachBottomCoolDown(double reachBottomCoolDown) {
		this.reachBottomCoolDown = reachBottomCoolDown;
		return this;
	}

	public void update() {
		double speed = (Math.min(this.i.getDeltaTime(), 0.01F) * (!this.wasInBounds ? 8F : 20F));
		this.animatedScroll = animate(this.animatedScroll, this.scroll, speed);

		if (this.scroll > 0) {
			this.scroll = 0;
		} else if (this.scroll < this.maxSize - this.size) {
			this.scroll = this.maxSize - this.size;
		}

		if (this.animatedScroll > 0) {
			if (this.wasInBounds) {
				if (this.reachTopEvent != null) {
					double f = GLFW.glfwGetTime();
					if (f - this.lastLoadedTime > this.reachBottomCoolDown) {
						this.reachTopEvent.run();
						this.defLastLoadedTime();
					}
				}

				// System.out.println("Reached top.");
			}

			this.wasInBounds = false;
		} else if (this.animatedScroll < this.maxSize - this.size) {
			if (this.wasInBounds) {
				if (this.reachBottomEvent != null) {
					double f = GLFW.glfwGetTime();
					if (f - this.lastLoadedTime > this.reachBottomCoolDown) {
						this.reachBottomEvent.run();
						this.defLastLoadedTime();
					}
				}
			}

			this.wasInBounds = false;
		} else {
			this.wasInBounds = true;
		}
	}

	public void defLastLoadedTime() {
		this.lastLoadedTime = GLFW.glfwGetTime();
	}

	public void onScrollEvent(double y) {
		// (y * n) is the step size in pixels. n being the multiplier.
		this.scroll += (float) y * 90F;
	}

	public float get() {
		return animatedScroll;
	}

	public void reset() {
		this.animatedScroll = 0;
		this.scroll = 0;
		this.wasInBounds = true;
	}

	public static float animate(float value, float target, @Range(from = 0, to = 1) double speed) {
		float diff = target - value;

		if (Math.abs(diff) < 0.5F) {
			return target;
		}

		float newValue = (float) (diff * speed);
		return value + newValue;
	}

	@Contract("_, _ -> this")
	public Scroll2 setScroll(float scroll, boolean instant) {
		if (scroll < maxSize - size) {
			scroll = maxSize - size;
		}

		this.scroll = scroll;
		if (instant) {
			this.animatedScroll = scroll;
		}
		return this;
	}

	public interface FloatSupplier {
		double get();
	}
}
