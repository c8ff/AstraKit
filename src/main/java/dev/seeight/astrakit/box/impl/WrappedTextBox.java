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

package dev.seeight.astrakit.box.impl;

import dev.seeight.astrakit.box.UIBoxContext;
import dev.seeight.astrakit.box.ComponentBox;
import dev.seeight.common.lwjgl.font.IFont;
import dev.seeight.common.lwjgl.fontrenderer.BufferedFontRenderer;
import dev.seeight.common.lwjgl.fontrenderer.IFontRenderer;
import dev.seeight.common.lwjgl.util.StringWrapper;

import java.util.Arrays;
import java.util.function.Supplier;

public class WrappedTextBox extends ComponentBox {
	private final IFontRenderer fontRenderer;
	private final IFont font;

	private final Supplier<String> stringSupplier;
	private char[] text;

	private double r = 1.0;
	private double g = 1.0;
	private double b = 1.0;

	private final int chunkSize;
	private char[][] chunks;

	public WrappedTextBox(UIBoxContext i, IFontRenderer fontRenderer, IFont font, Supplier<String> stringSupplier) {
		super(i);
		this.fontRenderer = fontRenderer;

		if (this.fontRenderer instanceof BufferedFontRenderer f) {
			this.chunkSize = f.getCharacterCapacity();
		} else {
			this.chunkSize = 0;
		}

		this.chunks = new char[0][];

		this.font = font;
		this.stringSupplier = stringSupplier;
		this.setMinWidth(100); // IDK
		this.setWidth(this.getMinWidth());
		this.calculateText();
	}

	private void calculateText() {
		String string = this.stringSupplier.get();
		if (string == null || string.isBlank()) {
			this.text = new char[0];
			return;
		}
		this.text = StringWrapper.wrapString(this.font, this.fontRenderer, string, this.getWidth()).toCharArray();
		float s = this.fontRenderer.getHeightFloat(this.font, this.text);
		this.setMinHeight(s);
		this.setHeight(this.getMinHeight());

		if (this.chunkSize == 0 || this.text.length < this.chunkSize) {
			this.chunks = new char[][] { this.text };
			return;
		}

		int l = 0;
		char[] chunk = new char[this.chunkSize];
		char[][] chunks = new char[(int) Math.floor((double) this.text.length / this.chunkSize + 1)][];
		int chunkCount = 0;
		for (char c : this.text) {
			if (l >= this.chunkSize) {
				l = 0;
				chunks[chunkCount] = chunk;
				chunk = new char[this.chunkSize];
				chunkCount++;
			}

			chunk[l] = c;
			l++;
		}

		// Trim null characters
		if (l < chunk.length) {
			chunk = Arrays.copyOfRange(chunk, 0, l);
		}

		// Assign last character count
		chunks[chunkCount] = chunk;

		this.chunks = chunks;
	}

	public WrappedTextBox setColor(double r, double g, double b) {
		this.r = r;
		this.g = g;
		this.b = b;
		return this;
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		this.calculateText();
	}

	@Override
	public void render(float offsetX, float offsetY, float alpha) {
		if (isOutsideView(offsetX, offsetY)) return;

		float x = this.getX() + offsetX;
		float y = (float) Math.ceil(this.getY() + offsetY);
		float fx = x;

		this.renderer.color(r, g, b, alpha);
		for (char[] chunk : this.chunks) {
			if (chunk == null) continue;
			fx = this.fontRenderer.drawString(this.font, chunk, fx, y, x, 0, chunk.length);
			y += this.fontRenderer.getHeightFloat(this.font, chunk) - this.font.getHeight();
		}
	}
}
