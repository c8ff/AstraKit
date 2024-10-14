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

package dev.seeight.astrakit.components.impl;

import dev.seeight.astrakit.components.Component;
import dev.seeight.astrakit.IComponentRenderer;
import dev.seeight.util.MathUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;

/**
 * Represents a text field.
 *
 * @author seeight
 */
public class TextFieldComponent extends Component {
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

	public TextFieldComponent(String string) {
		this.set(string);
		this.headIndex = string.length();
	}

	public TextFieldComponent(String string, String emptyText) {
		this.set(string);
		this.headIndex = string.length();
		this.emptyText = emptyText;
	}

	public TextFieldComponent(String string, String emptyText, float width, float height) {
		this(string, emptyText);
		this.width = width;
		this.height = height;
	}

	@Override
	public void unfocus() {
		super.unfocus();
		this.clearSelection();
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
								this.add(c);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return;
			}
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

	/**
	 * A char event.
	 *
	 * @param character The typed character.
	 */
	@Override
	public void charEvent(char character) {
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

	@Override
	public void render(IComponentRenderer componentRenderer, float alpha) {
		componentRenderer.renderTextField(this, alpha);
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

		builtString = builder.toString();
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
