package dev.seeight.astrakit.box.res;

import dev.seeight.renderer.renderer.Texture;

public interface BoxResources {
	Texture getCheckboxTexture();

	Texture getDropdownTexture();

	Texture getMarkerTexture();

	Texture getSmallTriangleTexture();

	void delete();
}
