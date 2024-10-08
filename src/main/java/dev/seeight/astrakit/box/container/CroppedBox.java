package dev.seeight.astrakit.box.container;

import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.impl.PrioritizedRenderComponent;
import dev.seeight.astrakit.box.layout.Axis;
import dev.seeight.astrakit.box.layout.Sizing;
import dev.seeight.astrakit.box.util.Scroll2;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class CroppedBox extends ComponentBox implements PrioritizedRenderComponent {
	protected final ParentBox child;
	private final Axis axis;
	private final Scroll2 scroll;

	protected boolean compensateRenderOffset;
	protected float px;
	protected float py;
	protected float offsetX;
	protected float offsetY;

	protected double scrollPositionY;
	protected double scrollPositionX;
	protected boolean scrolling;

	protected float scrollSensitivity = 250;

	public CroppedBox(UIBoxContext i, ParentBox child, Axis scrollAxis) {
		super(i);
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
		this.py = py;

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

		this.activateCropping();
		try {
			this.child.render(x, py, alpha);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.disableCropping();

		if (this.scrolling) {
			this.renderer.color(1F, 1F, 1F, alpha);
			this.renderer.rect2d(scrollPositionX - 4, scrollPositionY - 4, scrollPositionX + 4, scrollPositionY + 4);
		}

		if (this.i.boxBoundView()) {
			this.renderer.color(1, 0, 0, alpha * 0.5F);
			this.renderer.hollowRect2f(x, y, this.getWidth(), this.getHeight(), 1F);
		}
	}

	@Override
	public void renderOver(float offsetX, float offsetY, float alpha) {
		this.child.renderOver(offsetX + this.getX(), offsetY + this.getY(), alpha);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (this.scrolling && button == GLFW.GLFW_MOUSE_BUTTON_3) {
			this.scrollPositionX = -1;
			this.scrollPositionY = -1;
			this.scrolling = false;
			this.setFocused(false);
			return false;
		}

		if (this.compensateRenderOffset) {
			// Scroll
			if (button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS) {
				this.scrollPositionX = this.i.getMouser().getX();
				this.scrollPositionY = this.i.getMouser().getY();
				this.scrolling = true;
				this.setFocused(true);
				return true;
			}

			if (!isInside(x - this.offsetX, y - this.offsetY)) return false;
		} else {
			if (!isInside(x, y)) return false;

			// Scroll
			if (button == GLFW.GLFW_MOUSE_BUTTON_3 && action == GLFW.GLFW_PRESS) {
				this.scrollPositionX = this.i.getMouser().getX();
				this.scrollPositionY = this.i.getMouser().getY();
				this.scrolling = true;
				this.setFocused(true);
				return true;
			}
		}

		if (this.compensateRenderOffset) {
			x -= this.px;
			y -= this.py;
		} else {
			if (axis == Axis.VERTICAL) {
				y -= this.scroll.get();
			} else {
				x -= this.scroll.get();
			}
		}

		boolean b = child.mouseEvent(button, action, mods, x - this.getX(), y - this.getY());
		child.setFocused(b);
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
		child.scrollEvent(x, y);
		scroll.onScrollEvent(axis == Axis.HORIZONTAL ? x : y);
		return true;
	}

	@Override
	public void charEvent(int codepoint) {
		child.charEvent(codepoint);
	}

	@Override
	public void cursorPosition(double x, double y) {
		child.cursorPosition(x - this.getX() - child.getX(), y - this.getY() - child.getY());
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
	}

	public Scroll2 getScroll() {
		return scroll;
	}

	public ParentBox getChild() {
		return child;
	}

	protected void activateCropping() {
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		GL11.glScissor((int) x, (int) (this.i.getWindowHeight() - y - this.getHeight()), (int) this.getWidth(), (int) this.getHeight());
	}

	protected void disableCropping() {
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}
}
