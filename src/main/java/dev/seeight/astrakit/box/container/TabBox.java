package dev.seeight.astrakit.box.container;

import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class TabBox extends ComponentBox {
	private final List<Tab> tabs;

	private final IFont font;
	private final IFontRenderer fontRenderer;

	private Tab selected;

	private final float titlesHeight;

	private float renderX;
	private float renderY;

	public TabBox(UIBoxContext i, IFont font, IFontRenderer fontRenderer, float margin, int selectedTab, Tab... tabs) {
		super(i);
		this.tabs = List.of(tabs);

		this.font = font;
		this.fontRenderer = fontRenderer;

		this.selected = this.tabs.get(selectedTab);

		float w = 0;
		for (Tab tab : tabs) {
			w += this.fontRenderer.getWidthFloat(this.font, tab.label);
		}
		float h = this.titlesHeight = this.font.getHeight() + margin * 2;
		this.setMinWidth(w + 50);
		this.setMinHeight(h + 100);
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (this.isOutsideView(offsetX, offsetY)) return;

		// Render titles
		float x = offsetX + this.getX();
		float y = offsetY + this.getY();

		this.renderX = offsetX;
		this.renderY = offsetY;

		float w = this.getWidth();

		float tw = w / this.tabs.size();

		this.renderer.color(0.25F, 0.25F, 0.25F, alpha);
		this.renderer.rect2f(x, y, x + w, y + this.titlesHeight);
		this.renderer.color(1, 1, 1, alpha);

		float tabX = x;
		for (Tab tab : this.tabs) {
			tab.selected = tab == this.selected;

			tab.x = tabX;
			tab.x2 = tabX + tw;
			tab.y = y;
			tab.y2 = y + this.titlesHeight;

			if (tab.selected) {
				this.renderer.color(0.15F, 0.15F, 0.15F, alpha);
				this.renderer.rect2f(tab.x, tab.y, tab.x2, tab.y2);
				this.renderer.color(1, 1, 1, alpha);
			}

			float fx = tabX + (tw - this.fontRenderer.getWidthFloat(this.font, tab.label)) / 2f;
			float fy = y + (this.titlesHeight - this.fontRenderer.getHeightFloat(this.font, tab.label)) / 2f;
			this.fontRenderer.drawString(this.font, tab.label, (float) Math.floor(fx), (float) Math.floor(fy));
			tabX += tw;
		}

		y += this.titlesHeight;

		this.selected.box.render(x, y, alpha);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		// TODO: Make this an option
		float rx = this.getX() + this.renderX;
		float ry = this.getY() + this.renderY;

		if (rx < x && ry < y && rx + this.getWidth() > x && ry + this.titlesHeight > y) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
				if (action == GLFW.GLFW_PRESS) {
					for (Tab tab : this.tabs) {
						if (tab == this.selected) continue;

						if (tab.x < x && tab.y < y && tab.x2 > x && tab.y2 > y) {
							this.selected = tab;
							break;
						}
					}

					return true;
				}
			}

			return false;
		}

		return this.selected.box.mouseEvent(button, action, mods, x - this.renderX, y - this.renderY - this.titlesHeight);
	}

	@Override
	public void keyEvent(int key, int action, int mods) {
		this.selected.box.keyEvent(key, action, mods);
	}

	@Override
	public boolean scrollEvent(double x, double y) {
		return this.selected.box.scrollEvent(x, y);
	}

	@Override
	public void charEvent(int codepoint) {
		this.selected.box.charEvent(codepoint);
	}

	@Override
	public void cursorPosition(double x, double y) {
		this.selected.box.cursorPosition(x - this.getX() - this.selected.box.getX(), y - this.getY() - this.selected.box.getY());
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		for (Tab tab : this.tabs) {
			tab.box.setWidth(width);

			if (tab.box instanceof ParentBox b) {
				b.applyLayout();
			} else if (tab.box instanceof CroppedBox b) {
				b.getChild().applyLayout();
			}
		}
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		for (Tab tab : this.tabs) {
			tab.box.setHeight(height - this.titlesHeight);
		}
	}

	public static class Tab {
		public char[] label;
		public ComponentBox box;

		private float x;
		private float y;
		private float x2;
		private float y2;

		public Tab(String label, ComponentBox box) {
			this.label = label.toCharArray();
			this.box = box;
		}

		public void setLabel(String str) {
			this.label = str.toCharArray();
		}

		protected boolean selected;
	}
}
