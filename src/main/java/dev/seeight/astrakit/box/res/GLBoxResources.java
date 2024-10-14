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
