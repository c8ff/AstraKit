package dev.seeight.astrakit.box.container;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.*;
import dev.seeight.astrakit.box.impl.Dropdown;
import dev.seeight.astrakit.box.layout.Layout;
import dev.seeight.astrakit.box.layout.Sizing;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;

public class ParentBox extends ComponentBox {
	final Layout layout;
	final List<Component> children;
	final List<Component> immutableChildren;

	protected Border border;
	protected ComponentColor background;

	protected boolean compensateRenderOffset;
	protected boolean automaticLayoutChange = true;

	protected Runnable clickAction;

	protected Component forceFocus;

	protected float px;
	protected float py;

	private double mx;
	private double my;

	public ParentBox(UIBoxContext i, Layout layout) {
		super(i);
		this.children = new ArrayList<>();
		this.immutableChildren = Collections.unmodifiableList(this.children);
		this.layout = layout;
	}

	public ParentBox applyLayout() {
		this.layout.arrange(this);
		return this;
	}

	public ParentBox setAutomaticLayoutChange(boolean automaticLayoutChange) {
		this.automaticLayoutChange = automaticLayoutChange;
		return this;
	}

	public ParentBox setBorder(Border border) {
		this.border = border;
		return this;
	}

	public ParentBox setBackground(ComponentColor background) {
		this.background = background;
		return this;
	}

	public ParentBox setForceFocus(ComponentBox forceFocus) {
		if (!this.children.contains(forceFocus)) {
			throw new IllegalArgumentException("Component is not a child.");
		}

		this.forceFocus = forceFocus;
		if (forceFocus != null) {
			forceFocus.setFocused(true);
		}
		return this;
	}

	public ParentBox compensateRenderOffset(boolean compensateRenderOffset) {
		this.compensateRenderOffset = compensateRenderOffset;
		return this;
	}

	public ParentBox setClickAction(Runnable clickAction) {
		this.clickAction = clickAction;
		return this;
	}

	public ParentBox addBoxes(Component box) {
		if (box == this) throw new IllegalArgumentException("The box cannot be inside itself.");
		if (box == null) return this;
		this.children.add(box);
		if (this.automaticLayoutChange) this.applyLayout();
		return this;
	}

	public ParentBox addBoxes(Component... boxes) {
		if (boxes == null) return this;
		for (var box : boxes) {
			if (box == null) continue;
			this.children.add(box);
		}
		if (this.automaticLayoutChange) this.applyLayout();
		return this;
	}

	public ParentBox addBoxes(List<Component> boxes) {
		if (boxes == null) return this;
		for (var box : boxes) {
			if (box == null) continue;
			this.children.add(box);
		}
		if (this.automaticLayoutChange) this.applyLayout();
		return this;
	}

	public ParentBox addBoxesAtStart(List<Component> boxes) {
		if (boxes == null) return this;
		for (int i = boxes.size() - 1; i >= 0; i--) {
			var s = boxes.get(i);
			if (s == null) continue;
			this.children.add(0, s);
		}
		if (this.automaticLayoutChange) this.applyLayout();
		return this;
	}

	@Deprecated
	@Override
	public ParentBox setSizing(@NotNull Sizing sizing) {
		super.setSizing(sizing);
		return this;
	}

	@Override
	public ParentBox setSizing(@Nullable Sizing x, @Nullable Sizing y) {
		super.setSizing(x, y);
		return this;
	}

	@Override
	public ParentBox setSizingX(@Nullable Sizing x) {
		super.setSizingX(x);
		return this;
	}

	@Override
	public ParentBox setSizingY(@Nullable Sizing y) {
		super.setSizingY(y);
		return this;
	}

	@Override
	public void setMinWidth(float minWidth) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setMinHeight(float minHeight) {
		throw new UnsupportedOperationException();
	}

	@Override
	public float getMinWidth() {
		float s = layout.getMinWidth(this);
		return s;
	}

	@Override
	public float getMinHeight() {
		float s = layout.getMinHeight(this);
		return s;
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;

		this.px = offsetX;
		this.py = offsetY;

		if (isOutsideView(offsetX, offsetY)) return;

		Dropdown dropdown = null;

		boolean debugView = this.i.boxBoundView();

		this.renderBackground(x, y);

		// Fuck you concurrent modification exception
		while (true) {
			try {
				for (var box : this.children) {
					box.render(x, y, alpha);

					if (dropdown == null && box.isFocused() && box instanceof Dropdown b) {
						dropdown = b;
					}

					if (debugView && !box.isOutsideView(x, y) && !(box instanceof ParentBox)) {
						this.renderer.color(0, 0, 1, alpha);
						this.renderer.hollowRect2f(x + box.getX(), y + box.getY(), x + box.getX() + box.getWidth(), y + box.getY() + box.getHeight(), 1F);
					}
				}
				break;
			} catch (ConcurrentModificationException ignored) {}
		}

		if (debugView) {
			this.renderer.color(0, 1, 0, alpha * 0.25F);
			this.renderer.hollowRect2f(x, y, x + this.getWidth(), y + this.getHeight(), 1F);
		}

		if (dropdown != null) {
			dropdown.renderOver(x, y, alpha);
		}

		if (this.border != null) {
			this.renderer.color(this.border.r, this.border.g, this.border.b, this.border.a * alpha);
			this.renderer.hollowRect2f(x, y, x + this.getWidth(), y + this.getHeight(), this.border.size);
		}
	}

	private void renderBackground(float x, float y) {
		if (this.background != null) {
			this.renderer.color(this.background.r, this.background.g, this.background.b, this.background.a);
			this.renderer.rect2f(x, y, x + this.getWidth(), y + this.getHeight());
		}
	}

	@Override
	public ParentBox setSize(float width, float height) {
		super.setSize(width, height);
		return this;
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		double mx = x - this.getX();
		double my = y - this.getY();
		double mx0 = x;
		double my0 = y;

		if (this.compensateRenderOffset) {
			mx -= px;
			my -= py;
			mx0 -= px;
			my0 -= py;
		}

		if (this.forceFocus != null) {
			if (this.forceFocus.mouseEvent(button, action, mods, mx, my)) {
				return true;
			}

			this.forceFocus.setFocused(true);
		} else {
			for (var component : this.children) {
				if (component.isFocused() && component instanceof Dropdown s) {
					s.mouseEventOver(button, action, mx, my);
					return true;
				}
			}

			if (action == GLFW.GLFW_RELEASE) {
				for (var component : this.children) {
					if (component.isFocused()) {
						if (!component.mouseEvent(button, action, mods, mx, my)) {
							component.setFocused(false);
						}
						return false;
					}
				}
			}

			for (var component : this.children) {
				component.setFocused(false);
			}

			for (var component : this.children) {
				if (component.mouseEvent(button, action, mods, mx, my)) {
					component.setFocused(true);
					return true;
				}
			}
		}

		if (this.clickAction != null) {
			if (this.isInside(mx0, my0)) {
				if (button == GLFW.GLFW_MOUSE_BUTTON_1 && action == GLFW.GLFW_RELEASE && this.isFocused()) {
					this.clickAction.run();
					return false;
				}

				if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
					this.setFocused(true);
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		if (this.forceFocus != null) {
			this.forceFocus.keyEvent(key, action, mods);
			return;
		}

		if (key == GLFW.GLFW_KEY_TAB && mods == 0) {
			if (action == GLFW.GLFW_RELEASE) {
				boolean f = false;
				for (var box : this.children) {
					if (box.isFocused()) {
						box.setFocused(false);
						f = true;
					} else if (f) {
						box.setFocused(true);
						break;
					}
				}

				if (!f) {
					this.children.get(0).setFocused(true);
				}
			}
			return;
		}

		for (var box : this.children) {
			if (box.isFocused()) {
				box.keyEvent(key, action, mods);
				break;
			}
		}
	}

	@Override
	public boolean scrollEvent(double x, double y) {
		if (this.forceFocus != null) {
			return this.forceFocus.scrollEvent(x, y);
		}

		double mx = this.mx + this.getX();
		double my = this.my + this.getY();

		if (this.compensateRenderOffset) {
			mx -= this.px;
			my -= this.py;
		}

		for (Component child : this.children) {
			child.setFocused(false);
		}

		for (var box : this.children) {
			if (box.isInside(mx, my) && box.scrollEvent(x, y)) {
				box.setFocused(true);
				return true;
			}
		}

		return false;
	}

	@Override
	public void charEvent(int codepoint) {
		if (this.forceFocus != null) {
			this.forceFocus.charEvent(codepoint);
			return;
		}

		for (var box : this.children) {
			if (box.isFocused()) {
				box.charEvent(codepoint);
				break;
			}
		}
	}

	@Override
	public void cursorPosition(double x, double y) {
		mx = x;
		my = y;

		if (this.compensateRenderOffset) {
			x -= this.px;
			y -= this.py;
			x -= this.getX();
			y -= this.getY();
		}

		if (this.forceFocus != null) {
			this.forceFocus.cursorPosition(x, y);
			return;
		}

		for (var box : this.children) {
			if (box.isFocused()) {
				box.cursorPosition(x - box.getX(), y - box.getY());
				break;
			}
		}
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		if (!this.children.isEmpty())
			this.layout.onResize(this, width, 0);
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		if (!this.children.isEmpty())
			this.layout.onResize(this, 0, height);
	}

	public List<Component> children() {
		return this.immutableChildren;
	}

	public void clear() {
		this.children.clear();
	}

	public Layout getLayout() {
		return this.layout;
	}
}
