package dev.seeight.astrakit.box.res;

import dev.seeight.renderer.renderer.Texture;
import dev.seeight.renderer.renderer.gl.components.GLTexture;

public class GLBoxResources implements BoxResources {
	private final Texture checkboxTexture;
	private final Texture dropdownTexture;
	private final Texture markerTexture;
	private final Texture smallTriangleTexture;
	private boolean deleted;

	@SuppressWarnings("DataFlowIssue")
	public GLBoxResources() {
		try {
			this.checkboxTexture = GLTexture.fromInputStream(GLBoxResources.class.getResourceAsStream("/assets/astrakit/checkbox.png"));
			this.dropdownTexture = GLTexture.fromInputStream(GLBoxResources.class.getResourceAsStream("/assets/astrakit/dropdown.png"));
			this.markerTexture = GLTexture.fromInputStream(GLBoxResources.class.getResourceAsStream("/assets/astrakit/marker.png"));
			this.smallTriangleTexture = GLTexture.fromInputStream(GLBoxResources.class.getResourceAsStream("/assets/astrakit/small_triangle.png"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Texture getCheckboxTexture() {
		this.assertNotDeleted();
		return this.checkboxTexture;
	}

	@Override
	public Texture getDropdownTexture() {
		this.assertNotDeleted();
		return this.dropdownTexture;
	}

	@Override
	public Texture getMarkerTexture() {
		this.assertNotDeleted();
		return this.markerTexture;
	}

	@Override
	public Texture getSmallTriangleTexture() {
		this.assertNotDeleted();
		return this.smallTriangleTexture;
	}

	@Override
	public void delete() {
		if (this.deleted) return;
		this.checkboxTexture.delete();
		this.dropdownTexture.delete();
		this.markerTexture.delete();
		this.smallTriangleTexture.delete();
		this.deleted = true;
	}

	private void assertNotDeleted() {
		if (this.deleted) {
			throw new IllegalStateException("The resources are no longer loaded.");
		}
	}
}
