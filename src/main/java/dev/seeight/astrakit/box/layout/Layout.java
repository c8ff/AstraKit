package dev.seeight.astrakit.box.layout;

import dev.seeight.astrakit.box.container.ParentBox;

public interface Layout {
	/**
	 * Applies the layout into the specified box by rearranging the child boxes.
	 *
	 * @param box The box to apply the layout to.
	 */
	void arrange(ParentBox box);

	/**
	 * Called when the box is resized. This method must be called AFTER the size of the box has been set.
	 *
	 * @param box The box that was resized.
	 * @param width A positive float number, if changed. Otherwise, 0 will be used.
	 * @param height A positive float number, if changed. Otherwise, 0 will be used.
	 */
	void onResize(ParentBox box, float width, float height);

	float getMinWidth(ParentBox parentBox);

	float getMinHeight(ParentBox parentBox);

	Axis getMainAxis();
}
