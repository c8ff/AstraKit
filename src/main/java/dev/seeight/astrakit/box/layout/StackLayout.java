package dev.seeight.astrakit.box.layout;

import dev.seeight.astrakit.box.container.ParentBox;

import java.util.HashSet;
import java.util.Set;

public class StackLayout implements Layout {
	protected float margin;
	protected float padding;
	protected boolean fit = true;

	protected Alignment alignment = Alignment.LEFT_OR_UP;

	public final Axis axis;

	public StackLayout(Axis axis) {
		this.axis = axis;
	}

	public StackLayout setMargin(float margin) {
		this.margin = margin;
		return this;
	}

	public float getMargin() {
		return margin;
	}

	public StackLayout setPadding(float padding) {
		this.padding = padding;
		return this;
	}

	public StackLayout setFit(boolean fit) {
		this.fit = fit;
		return this;
	}

	public StackLayout setAlignment(Alignment alignment) {
		this.alignment = alignment;
		return this;
	}

	@Override
	public void arrange(ParentBox box) {
		Axis axis = this.axis;
		Axis axis1 = axis.opposite();

		float margin = this.margin;

		// The pool of child boxes that will be refreshed.
		Set<ParentBox> refresh = new HashSet<>(box.children().size());

		//region Main Axis Filling
		// Get minimum size of the box, from main axis.
		float remainingSize = box.getSize(axis) - margin * 2;
		if (remainingSize <= 0) remainingSize = box.getMinSize(axis) - margin * 2;

		// This is a count for the boxes that need filling.
		int fillCount = 0;

		// Compensate for the first element.
		remainingSize += padding;

		// Subtract all the other boxes' size and count how many fill boxes there are.
		for (var child : box.children()) {
			remainingSize -= padding;
			if (child.getSizing(axis) == Sizing.FILL) {
				fillCount++;
				continue;
			}
			float f = child.getSize(axis);
			if (f <= 0) f = child.getMinSize(axis);
			remainingSize -= f;
		}

		// Calculate children size.
		float childrenSize = remainingSize / fillCount;

		// Assign children size.
		for (var child : box.children()) {
			if (child.getSizing(axis) != Sizing.FILL) continue;
			child.setSize(axis, childrenSize);

			// Add to refresh pool
			if (child instanceof ParentBox b) refresh.add(b);
		}
		//endregion

		// Get minimum size of the box, from the opposite axis.
		float size = box.getSize(axis1) - margin * 2;
		if (size <= 0) size = box.getMinSize(axis1) - margin * 2;

		// Apply sizing depending on the set preference.
		for (var child : box.children()) {
			Sizing sizing = child.getSizing(axis1);
			switch (sizing) {
				case FILL -> child.setSize(axis1, size);
				case MIN -> {
					float s = child.getMinSize(axis1);
					child.setSize(axis1, s);
				}
				default -> throw new UnsupportedOperationException("Unknown sizing type: " + sizing);
			}

			if (child instanceof ParentBox b) refresh.add(b);
		}

		// Refresh the boxes' layout.
		for (ParentBox b : refresh) {
			b.applyLayout();
		}
		// Clean for further use.
		refresh.clear();

		// Set the size to the bare minimum (largest child)
		if (box.getSize(axis1) <= margin * 2) {
			float maxSize = 0;

			for (var child : box.children()) {
				float s = child.getSize(axis1);
				if (maxSize < s) {
					maxSize = s;
				}
			}

			box.setSize(axis1, maxSize + margin * 2);
		}

		switch (alignment) {
			case LEFT_OR_UP -> {
				for (var child : box.children()) {
					child.setPosition(axis1, margin);
				}
			}
			case CENTER -> {
				float s = box.getSize(axis1);

				for (var child : box.children()) {
					child.setPosition(axis1, (s - child.getSize(axis1)) / 2f);
				}
			}
			case RIGHT_OR_DOWN -> {
				float s = box.getSize(axis1);

				for (var child : box.children()) {
					child.setPosition(axis1, s - child.getSize(axis1) - margin);
				}
			}
		}

		float position = margin;

		for (var child : box.children()) {
			child.setPosition(axis, position);
			position += child.getSize(axis) + padding;
		}

		if (this.fit && (box.getSizing(axis) != Sizing.FILL || box.getSize(axis) <= 0)) {
			position -= padding;
			position += margin;
			box.setSize(axis, position);
		}
	}

	@Override
	public void onResize(ParentBox box, float width, float height) {

	}

	@Override
	public float getMinWidth(ParentBox parentBox) {
		final Axis axis1 = axis.opposite();

		float w = 0;
		boolean _f = true;
		if (axis1 == Axis.HORIZONTAL) {
			for (var child : parentBox.children()) {
				float s = child.getMinSize(axis1);
				if (w < s) {
					w = s;
				}
			}
		} else {
			for (var child : parentBox.children()) {
				float s = child.getMinSize(axis1);

				if (!_f) w += padding; else _f = false;
				w += s;
			}
		}

		return w + margin * 2;
	}

	@Override
	public float getMinHeight(ParentBox parentBox) {
		final Axis axis1 = axis.opposite();

		float h = margin;

		boolean _f = true;
		if (axis1 == Axis.VERTICAL) {
			for (var child : parentBox.children()) {
				float s = child.getSize(axis1);
				if (h < s) {
					h = s;
				}
			}
		} else {
			for (var child : parentBox.children()) {
				float s = child.getSize(axis1);

				if (!_f) h += padding; else _f = false;
				h += s;
			}
		}

		return h + margin;
	}

	@Override
	public Axis getMainAxis() {
		return this.axis;
	}

}
