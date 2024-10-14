package dev.seeight.astrakit.box.container;

import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.crop.ICropContext;
import dev.seeight.astrakit.box.impl.PrioritizedRenderComponent;
import dev.seeight.astrakit.box.layout.Axis;
import dev.seeight.astrakit.box.layout.Sizing;
import dev.seeight.astrakit.box.util.Scroll2;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class CroppedBox extends ComponentBox implements PrioritizedRenderComponent {
	private final ICropContext cropContext;
	protected final ParentBox child;
	private final Axis axis;
	private final Scroll2 scroll;

	protected boolean compensateRenderOffset;
	protected float px;
	protected float py;
	protected float pScrollY;
	protected float offsetX;
	protected float offsetY;

	protected double scrollPositionY;
	protected double scrollPositionX;
	protected boolean scrolling;

	protected float scrollSensitivity = 250;

	public CroppedBox(ICropContext cropContext, UIBoxContext i, ParentBox child, Axis scrollAxis) {
		super(i);
		this.cropContext = cropContext;
		this.child = child;
		this.axis = scrollAxis;
		this.scroll = new Scroll2(i);
		this.setSize(0, 0);
	}

	public CroppedBox setReachBottomEvent(Runnable onReachBottom) {
		this.scroll.setReachBottomEvent(onReachBottom);
		return this;
	}

	public CroppedBox setReachTopEvent(Runnable onReachTop) {
		this.scroll.setReachTopEvent(onReachTop);
		return this;
	}

	public CroppedBox setCompensateRenderOffset(boolean compensateRenderOffset) {
		this.compensateRenderOffset = compensateRenderOffset;
		return this;
	}

	public CroppedBox setScrollSensitivity(float scrollSensitivity) {
		this.scrollSensitivity = scrollSensitivity;
		return this;
	}

	@Override
	public CroppedBox setSizingX(@Nullable Sizing x) {
		return (CroppedBox) super.setSizingX(x);
	}

	@Override
	public CroppedBox setSizingY(@Nullable Sizing y) {
		return (CroppedBox) super.setSizingY(y);
	}

	@Override
	public CroppedBox setSizing(@Nullable Sizing x, @Nullable Sizing y) {
		return (CroppedBox) super.setSizing(x, y);
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;
		float py = (float) Math.floor(y + this.scroll.get());
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.px = x;
		this.py = y;
		this.pScrollY = py;

		if (this.isOutsideView(offsetX, offsetY)) return;

		if (this.scrolling) {
			double current = axis == Axis.HORIZONTAL ? this.i.getMouser().getX() : this.i.getMouser().getY();
			double previous = axis == Axis.HORIZONTAL ? scrollPositionX : scrollPositionY;
			double s = (previous - current) / scrollSensitivity * (this.i.getDeltaTime() * 20F);
			this.scroll.onScrollEvent(s);
		}

		this.scroll.update();
		this.scroll.setSize(this.axis == Axis.HORIZONTAL ? Math.max(this.child.getWidth(), this.getWidth()) : Math.max(this.child.getHeight(), this.getHeight()));
		this.scroll.setMaxSize(this.axis == Axis.HORIZONTAL ? this.getWidth() : this.getHeight());

		this.cropContext.startCropping(x, y, this.getWidth(), this.getHeight());
		try {
			this.child.render(x, py, alpha);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.cropContext.stopCropping();

		if (this.scrolling) {
			this.renderer.color(1F, 1F, 1F, alpha);
			this.renderer.rect2d(scrollPositionX - 4, scrollPositionY - 4, scrollPositionX + 4, scrollPositionY + 4);
		}

		if (this.i.boxBoundView()) {
			this.renderer.color(isFocused() ? 0.5F : 1, 0, 0, alpha * 0.5F);
			this.renderer.hollowRect2f(x + 1, y + 1, x - 1 + this.getWidth(), y - 1 + this.getHeight(), 1F);
		}
	}

	@Override
	public void renderOver(float offsetX, float offsetY, float alpha) {
		this.child.renderOver(offsetX + this.getX(), offsetY + this.getY(), alpha);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (this.scrolling && button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_RELEASE) {
			this.scrollPositionX = -1;
			this.scrollPositionY = -1;
			this.scrolling = false;
			this.setFocused(false);
			return false;
		}

		if (this.compensateRenderOffset) {
			if (!isInside(x - this.offsetX, y - this.offsetY) && !this.isFocused()) return false;
		} else {
			if (!isInside(x, y) && !this.isFocused()) return false;
		}

		if (this.compensateRenderOffset) {
			x -= this.px;
			y -= this.pScrollY;
		} else {
			if (axis == Axis.VERTICAL) {
				y -= this.scroll.get();
			} else {
				x -= this.scroll.get();
			}

			x -= this.getX();
			y -= this.getY();
		}

		boolean b = child.mouseEvent(button, action, mods, x, y);

		if (!b && button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS) {
			this.scrollPositionX = this.i.getMouser().getX();
			this.scrollPositionY = this.i.getMouser().getY();
			this.scrolling = true;
			this.setFocused(true);
			return true;
		}

		this.setFocused(b);
		return b;
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		if (key == GLFW.GLFW_KEY_R) {
			this.scroll.reset();
			return;
		}

		child.keyEvent(key, action, mods);
	}

	@Override
	public boolean scrollEvent(double x, double y) {
		if (!child.scrollEvent(x, y)) {
			scroll.onScrollEvent(axis == Axis.HORIZONTAL ? x : y);
		}
		return true;
	}

	@Override
	public void charEvent(int codepoint) {
		child.charEvent(codepoint);
	}

	@Override
	public void cursorPosition(double x, double y) {
		// for some reason it doesn't work when including the parent position.
		child.cursorPosition(x - child.getX(), y - child.getY());
	}

	@Override
	public void setWidth(float width) {
		if (width <= 0) {
			width = child.getWidth();
		}

		super.setWidth(width);

		if (axis == Axis.HORIZONTAL) {
			this.scroll.setMaxSize(width);
		} else {
			child.setWidth(width);
		}

		child.applyLayout();
	}

	@Override
	public void setHeight(float height) {
		if (height <= 0) {
			height = child.getHeight();
		}

		super.setHeight(height);

		if (axis == Axis.VERTICAL) {
			this.scroll.setMaxSize(height);
		} else {
			child.setHeight(height);
		}

		child.applyLayout();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(focused);
		child.setFocused(focused);
	}

	public Scroll2 getScroll() {
		return scroll;
	}

	public ParentBox getChild() {
		return child;
	}
}
