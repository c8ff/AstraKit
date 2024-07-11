package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.astrakit.box.layout.Sizing;
import dev.seeight.astrakit.box.util.ReflectionUtil;
import dev.seeight.astrakit.box.util.Scroll2;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.util.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

/**
 * Component that allows user input.
 *
 * @author seeight
 */
public class TextFieldComponent extends ComponentBox {
	/**
	 * Holds all typed characters.
	 */
	@NotNull
	private final ArrayList<Character> characters = new ArrayList<>();
	/**
	 * A selection object, used to prevent creation of objects every 'selection'.
	 */
	@NotNull
	private final Selection _sel = new Selection();
	/**
	 * Set to '_sel' or 'null'.
	 */
	@Nullable
	private Selection selection = null;

	/**
	 * Represents were the text 'cursor' is.
	 */
	protected int headIndex;
	/**
	 * A cached string, rebuilt for every modification to characters.
	 */
	protected String builtString = "";
	/**
	 * Called when ENTER key is pressed.
	 */
	protected Runnable enterEvent;

	protected String emptyText;

	private final IFont font;
	private final IFontRenderer fontRenderer;

	public TextFieldComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, String string, float margin) {
		this(i, font, fontRenderer, string, null, margin);
	}

	public TextFieldComponent(UIBoxContext i, IFont font, IFontRenderer fontRenderer, String string, String emptyText, float margin) {
		super(i);
		this.set(string);
		this.font = font;
		this.fontRenderer = fontRenderer;
		this.headIndex = string.length();
		this.emptyText = emptyText;

		this.setMinWidth(200); // arbitrary number idk
		this.setMinHeight(font.getHeight() + margin * 2);
		this.setWidth(this.getMinWidth());
		this.setHeight(this.getMinHeight());
	}

	@Override
	public TextFieldComponent setSizingX(@Nullable Sizing x) {
		return (TextFieldComponent) super.setSizingX(x);
	}

	@Override
	public boolean mouseEvent(int button, int action, int mods, double x, double y) {
		if (isInside(x, y)) {
			return true;
		}

		return super.mouseEvent(button, action, mods, x, y);
	}

	/**
	 * A key event.
	 *
	 * @param key    A GLFW key code (ex. GLFW_KEY_ESCAPE)
	 * @param action A GLFW action (ex. GLFW_RELEASE, GLFW_PRESS, etc.)
	 * @param mods   The modification code for the current key.
	 */
	@Override
	public void keyEvent(int key, int action, int mods) {
		boolean shift = (mods & GLFW.GLFW_MOD_SHIFT) != 0;
		boolean control = (mods & GLFW.GLFW_MOD_CONTROL) != 0;

		if (key == GLFW.GLFW_KEY_A) {
			if (control) {
				if (action == GLFW.GLFW_PRESS) {
					this.setSelection(0, this.characters.size(), true);
					this.headIndex = this.characters.size();
				}

				return;
			}
		}

		if (key == GLFW.GLFW_KEY_V) {
			if (control) {
				if (action == GLFW.GLFW_PRESS) {
					try {
						Object contents = Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);

						if (contents instanceof String s) {
							for (char c : s.toCharArray()) {
								if (this.isCharacterValid(c)) {
									this.add(c);
								}
							}
						}
					} catch (Exception e) {
						i.catchError("Exception while trying to copy string from clipboard.", e);
					}
				}

				return;
			}
		}

		if (key == GLFW.GLFW_KEY_X) {
			if (control && action == GLFW.GLFW_PRESS && this.selection != null) {
				String clipboard = this.builtString.substring(this.selection.start, this.selection.end);

				this.copyToClipboard(clipboard);

				this.remove(this.selection.start, this.selection.end);
				this.headIndex++;
				this.clearSelection();
			}

			return;
		}

		if (action == GLFW.GLFW_RELEASE) {
			return;
		}

		if (key == GLFW.GLFW_KEY_ENTER) {
			this.onEnterPressed();
		} else if (key == GLFW.GLFW_KEY_BACKSPACE) {
			this.onBackspace();
		} else if (key == GLFW.GLFW_KEY_DELETE) {
			this.onDeleteKey();
		} else if (key == GLFW.GLFW_KEY_LEFT) {
			int prevHeadIndex = this.headIndex;

			if (control) {
				int i = this.headIndex - 2;
				this.headIndex = 0;
				// find nearest space
				for (; i >= 0; i--) {
					if (i < this.characters.size()) {
						char c = this.characters.get(i);
						if (c == ' ' || c == '.') {
							this.headIndex = i + 1;
							break;
						}
					}
				}
			} else if (this.headIndex - 1 >= 0) {
				this.headIndex--;
			}

			if (shift) {
				if (this.selection == null) {
					this.selection = this._sel;
					this.selection.isEndHead = true;
					this.selection.end = prevHeadIndex;
				}

				if (this.selection.isEndHead) {
					this.selection.start = this.headIndex;
				} else {
					this.selection.end = this.headIndex;
				}

				if (this.selection.end < this.selection.start) {
					this.selection.isEndHead = true;
					int start = this.selection.start;
					this.selection.start = this.selection.end;
					this.selection.end = start;
				}
			} else {
				if (this.selection != null && this.selection.start != this.selection.end) {
					this.headIndex = prevHeadIndex;
				}
				this.clearSelection();
			}

		} else if (key == GLFW.GLFW_KEY_RIGHT) {
			int prevHeadIndex = this.headIndex;

			if (control) {
				int i = this.headIndex;
				this.headIndex = this.characters.size();
				for (; i < this.characters.size(); i++) {
					char c = this.characters.get(i);
					if (c == ' ' || c == '.') {
						this.headIndex = i + 1;
						break;
					}
				}
			} else if (this.headIndex + 1 <= characters.size()) {
				this.headIndex++;
			}

			if (shift) {
				if (this.selection == null) {
					this.selection = this._sel;
					this.selection.isEndHead = false;

					this.selection.start = prevHeadIndex;
				}

				if (this.selection.isEndHead) {
					this.selection.start = this.headIndex;
				} else {
					this.selection.end = this.headIndex;
				}

				if (this.selection.end < this.selection.start) {
					this.selection.isEndHead = false;
					int start = this.selection.start;
					this.selection.start = this.selection.end;
					this.selection.end = start;
				}
			} else {
				if (this.selection != null && this.selection.start != this.selection.end) {
					this.headIndex = prevHeadIndex;
				}
				this.clearSelection();
			}

		} else if (key == GLFW.GLFW_KEY_HOME) {
			int prevHeadIndex = this.headIndex;
			this.headIndex = 0;

			if (shift) {
				this.setSelection(this.headIndex, prevHeadIndex, false);
			} else {
				this.clearSelection();
			}

		} else if (key == GLFW.GLFW_KEY_END) {
			int prevHeadIndex = this.headIndex;
			this.headIndex = characters.size();

			if (shift) {
				this.setSelection(prevHeadIndex, this.headIndex, true);
			} else {
				this.clearSelection();
			}
		}
	}

	protected void copyToClipboard(String clipboard) {
		StringSelection s = new StringSelection(clipboard);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, s);
	}

	@Override
	public void charEvent(int codepoint) {
		char character = (char) codepoint;

		if (isCharacterValid(character)) {
			if (this.selection != null) {
				this.onBackspace();
			}

			if (this.headIndex >= this.characters.size()) {
				this.add(character);
			} else {
				this.add(character, this.headIndex);
			}
		}
	}

	@Override
	public String toString() {
		return builtString;
	}

	private float cursorAnimation;
	private float cursorPosX;
	private boolean cursorAnimationDir;

	protected void renderBackground(float x, float y, float x2, float y2, float alpha) {
		if (this.isFocused()) {
			this.renderer.color(0.10F, 0.10F, 0.10F, alpha);
		} else {
			this.renderer.color(0.20F, 0.20F, 0.20F, alpha);
		}
		this.renderer.rect2f(x, y, x2, y2);
	}

	protected void renderText(char[] chars, int x, int y, float alpha) {
		this.renderer.color(1, 1, 1, alpha);
		this.fontRenderer.drawString(this.font, chars, x, y);
	}

	protected void renderEmptyText(int x, int y, float alpha) {
		this.renderer.color(0.35F, 0.35F, 0.35F, alpha);
		this.fontRenderer.drawString(this.font, this.emptyText, x, y);
	}

	protected void renderCursor(float x, float y, float x2, float y2, double alpha) {
		this.renderer.color(1, 1, 1, alpha);
		this.renderer.rect2f(x, y, x2, y2);
	}

	protected void renderSelection(float x, float y, float x2, float y2, double alpha) {
		this.renderer.color(1, 1, 1, alpha * 0.25F);
		this.renderer.rect2f(x, y, x2, y2);
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = this.getY() + offsetY;
		float width = this.getWidth();
		float height = this.getHeight();

		// Background
		this.renderBackground(x, y, x + width, y + height, alpha);

		float fontHeight = font.getHeight();
		// (flooring fixes weird font rendering)
		float fontX = (float) Math.floor(x + 10);
		float fontY = (float) Math.floor(y + (this.getHeight() - fontHeight) / 2f + 2F);

		// Main text
		char[] chars;
		if (this.characters.isEmpty()) {
			chars = new char[0];

			if (this.emptyText != null) {
				this.renderEmptyText((int) fontX, (int) fontY, alpha);
			}
		} else {
			chars = ReflectionUtil.conv(this.characters);
			this.renderText(chars, (int) fontX, (int) fontY, alpha);
		}

		// Cursor
		if (this.isFocused()) {
			float v = chars.length == 0 ? 0 : this.fontRenderer.getWidthFloat(font, chars, 0, this.headIndex);
			this.cursorPosX = Scroll2.animate(this.cursorPosX, v, this.i.getDeltaTime() * 150F);
			float cursorXOffset = this.cursorPosX;
			float cursorX = fontX + cursorXOffset;
			double sin = this.cursorAnimation / 255F;

			this.renderCursor(cursorX, fontY, cursorX + 1, fontY + fontHeight, alpha * sin);

			if (this.cursorAnimationDir) {
				this.cursorAnimation += (float) (this.i.getDeltaTime() * 1250F);
			} else {
				this.cursorAnimation -= (float) (this.i.getDeltaTime() * 1250F);
			}

			if (this.cursorAnimation <= 0) {
				this.cursorAnimation = 0;
				this.cursorAnimationDir = true;
			} else if (this.cursorAnimation >= 255) {
				this.cursorAnimation = 255F;
				this.cursorAnimationDir = false;
			}
		} else {
			this.cursorAnimation = 0;
		}

		// Selection
		if (this.selection != null && !this.characters.isEmpty()) {
			float startX = fontX + this.fontRenderer.getWidthFloat(font, chars, 0, this.selection.start);
			float endX = startX + this.fontRenderer.getWidthFloat(font, chars, this.selection.start, this.selection.end);

			this.renderSelection(startX, fontY, endX, fontY + fontHeight, alpha);
		}
	}

	/**
	 * Called when the DELETE key is pressed.
	 */
	protected void onDeleteKey() {
		if (!this.characters.isEmpty()) {
			if (this.selection != null) {
				this.remove(this.selection.start, this.selection.end);
				this.headIndex = MathUtil.clamp(this.headIndex + 1, 0, this.characters.size());
				this.clearSelection();
			} else {
				boolean isZero = this.headIndex == 0;
				this.remove(MathUtil.clamp(this.headIndex + 1, 1, this.characters.size()));
				this.headIndex = MathUtil.clamp(this.headIndex + (isZero ? 0 : 1), 0, this.characters.size());
			}
		}
	}

	/**
	 * Called when the BACKSPACE key is pressed.
	 */
	protected void onBackspace() {
		if (!this.characters.isEmpty()) {
			if (this.selection != null) {
				boolean isZero = this.selection.start == 0;
				this.remove(this.selection.start, this.selection.end);
				this.headIndex = MathUtil.clamp(this.headIndex + (isZero ? 0 : 1), 0, this.characters.size());
				this.clearSelection();
			} else if (this.headIndex != 0) {
				this.remove(this.headIndex);
			}
		}
	}

	/**
	 * Called when the ENTER key is pressed. Also calls the enter event.
	 */
	protected void onEnterPressed() {
		if (this.enterEvent != null) {
			this.enterEvent.run();
		}
	}

	/**
	 * Removes a character at an index
	 *
	 * @param index Index of the character to remove
	 */
	protected void remove(int index) {
		this.characters.remove(index - 1);
		this.headIndex = MathUtil.clamp(this.headIndex - 1, 0, this.characters.size());
		this.clearSelection();
		this.rebuildString();
	}

	/**
	 * Removes various characters from start to finish
	 *
	 * @param start  Start index of the characters to remove.
	 * @param finish End index of the characters to remove.
	 */
	protected void remove(int start, int finish) {
		this.characters.subList(start, finish).clear();
		this.headIndex = MathUtil.clamp(start - 1, 0, this.characters.size());
		this.clearSelection();
		this.rebuildString();
	}

	/**
	 * Inserts a character at the index.
	 *
	 * @param character The character to be added.
	 * @param index     The index of where the character will be added in.
	 */
	protected void add(char character, int index) {
		this.characters.add(index, character);
		this.headIndex = MathUtil.clamp(this.headIndex + 1, 0, this.characters.size());
		this.clearSelection();
		this.rebuildString();
	}

	/**
	 * Adds a character at the end of the string.
	 *
	 * @param character The character to be added.
	 */
	protected void add(char character) {
		this.characters.add(character);
		this.headIndex = this.characters.size();
		this.clearSelection();
		this.rebuildString();
	}

	/**
	 * Creates a {@link StringBuilder} object and appends all typed characters.
	 * Sets the output to the {@code builtString} variable.
	 */
	protected void rebuildString() {
		StringBuilder builder = new StringBuilder();

		for (Character character : this.characters) {
			builder.append(character);
		}

		this.builtString = builder.toString();
	}

	/**
	 * Sets the string as the typed string. Removes any selection.
	 *
	 * @param string The string to be set.
	 */
	public void set(@NotNull String string) {
		this.characters.clear();

		for (char c : string.toCharArray()) {
			this.characters.add(c);
		}

		this.builtString = string;
		this.headIndex = MathUtil.clamp(this.headIndex, 0, this.characters.size());
		this.clearSelection();
	}

	/**
	 * Clears all characters, and rebuilds the string.
	 */
	public void clear() {
		this.headIndex = 0;
		this.characters.clear();
		this.rebuildString();
		this.clearSelection();
	}

	protected void setSelection(int start, int end, boolean c) {
		this.selection = this._sel;
		this.selection.start = start;
		this.selection.end = end;
		this.selection.isEndHead = c;
	}

	protected void clearSelection() {
		this._sel.start = 0;
		this._sel.end = 0;
		this.selection = null;
	}

	protected boolean isCharacterValid(char character) {
		return !Character.isISOControl(character);
	}

	public int getHeadIndex() {
		return headIndex;
	}

	public @Nullable Selection getSelection() {
		return selection;
	}

	public void setEnterEvent(Runnable enterEvent) {
		this.enterEvent = enterEvent;
	}

	public @Nullable String getEmptyText() {
		return emptyText;
	}

	public int size() {
		return this.characters.size();
	}

	public static class Selection {
		public int start;
		public int end;
		public boolean isEndHead;

		public Selection() {

		}
	}
}
