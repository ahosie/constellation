/*
 * Copyright 2010-2020 Australian Signals Directorate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package au.gov.asd.tac.constellation.visual.opengl.utilities.glyphs;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The GlyphManagerOpenGLController manages the OpenGL buffers necessary to
 * render text in Constellation.
 *
 * @author sirius
 */
public class GlyphManagerOpenGLController {

//    private static final int EXTERNAL_FORMAT = GL30.GL_RED;
//    private static final int INTERNAL_FORMAT = GL30.GL_R8;

    private static final int FLOATS_PER_GLYPH = 4;
    private static final int BYTES_PER_FLOAT = Float.BYTES;
    private static final int BYTES_PER_GLYPH = BYTES_PER_FLOAT * FLOATS_PER_GLYPH;

    private final GlyphManager glyphManager;

    private final int[] coordinatesBufferName = new int[1];
    private final int[] coordinatesTextureName = new int[1];
    private int coordinatesBufferedGlyphs = 0;
    private int coordinatesBufferSize;

    private final int[] glyphsTextureName = new int[1];
    private int glyphsGlyphsBuffered = 0;
    private int glyphsPageCapacity;

    // The number of pages that have been loaded onto the graphics card
    private int glyphsPagesBuffered = 0;

    private final List<ByteBuffer> glyphsPageBuffers = new ArrayList<>();

    public GlyphManagerOpenGLController(GlyphManager glyphManager) {
        this.glyphManager = glyphManager;
    }

    public GlyphManager getGlyphManager() {
        return glyphManager;
    }

    public void init(/*final GL30 gl*/) {
//        initCoordinates(gl);
//        initGlyphs(gl);
    }

    public void update(/*final GL30 gl*/) {
//        updateCoordinates(gl);
//        updateGlyphs(gl);
    }

    public void bind(/*final GL30 gl*/int coordinatesUniformLocation, int coordinatesTextureUnit, int glyphsUniformLocation, int glyphsTexureUnit) {
//        bindCoordinates(gl, coordinatesUniformLocation, coordinatesTextureUnit);
//        bindGlyphs(gl, glyphsUniformLocation, glyphsTexureUnit);
    }

    private void initCoordinates(/*final GL30 gl*/) {
        // TODO_TT: this whole func
//        gl.glGenBuffers(1, coordinatesBufferName, 0);
//
//        coordinatesBufferSize = glyphManager.getGlyphTextureCoordinates().length * BYTES_PER_FLOAT;
//        gl.glBindBuffer(GL30.GL_TEXTURE_BUFFER, coordinatesBufferName[0]);
//        gl.glBufferData(GL30.GL_TEXTURE_BUFFER, coordinatesBufferSize, null, GL30.GL_DYNAMIC_DRAW);
//
//        gl.glGenTextures(1, coordinatesTextureName, 0);
//
//        gl.glBindTexture(GL30.GL_TEXTURE_BUFFER, coordinatesTextureName[0]);
//        gl.glTexBuffer(GL30.GL_TEXTURE_BUFFER, GL30.GL_RGBA32F, coordinatesBufferName[0]);
    }

    private void updateCoordinates(/*final GL30 gl*/) {
        // TODO_TT: this whole func
//        final int newTextureCoordinatesBufferSize = glyphManager.getGlyphTextureCoordinates().length * BYTES_PER_FLOAT;
//        if (newTextureCoordinatesBufferSize > coordinatesBufferSize) {
//            coordinatesBufferSize = newTextureCoordinatesBufferSize;
//            gl.glBindBuffer(GL30.GL_TEXTURE_BUFFER, coordinatesBufferName[0]);
//            gl.glBufferData(GL30.GL_TEXTURE_BUFFER, coordinatesBufferSize, null, GL30.GL_DYNAMIC_DRAW);
//            coordinatesBufferedGlyphs = 0;
//        }
//
//        if (coordinatesBufferedGlyphs < glyphManager.getGlyphCount()) {
//            final int offset = coordinatesBufferedGlyphs * FLOATS_PER_GLYPH;
//            final int size = glyphManager.getGlyphCount() * FLOATS_PER_GLYPH - offset;
//            gl.glBindBuffer(GL30.GL_TEXTURE_BUFFER, coordinatesBufferName[0]);
//            final FloatBuffer glyphsCoordinates = FloatBuffer.wrap(glyphManager.getGlyphTextureCoordinates(), offset, size);
//            gl.glBufferSubData(GL30.GL_TEXTURE_BUFFER, offset * BYTES_PER_FLOAT, size * BYTES_PER_FLOAT, glyphsCoordinates);
//            coordinatesBufferedGlyphs = glyphManager.getGlyphCount();
//        }
    }

    private void bindCoordinates(/*final GL30 gl*/int uniformLocation, int textureUnit) {
//        GL30.glActiveTexture(GL30.GL_TEXTURE0 + textureUnit);
//        // TODO_TT:
////        gl.glBindTexture(GL30.GL_TEXTURE_BUFFER, coordinatesTextureName[0]);
//        GL30.glUniform1i(uniformLocation, textureUnit);
    }

    private void initGlyphs(/*final GL30 gl*/) {

        // TODO_TT:
//        gl.glGenTextures(1, glyphsTextureName, 0);

//        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, glyphsTextureName[0]);
//
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);

        final int width = glyphManager.getTextureWidth();
        final int height = glyphManager.getTextureHeight();
        final int pageCount = glyphManager.getGlyphPageCount();
        // TODO_TT:
//        gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, INTERNAL_FORMAT, width, height, pageCount, 0, EXTERNAL_FORMAT, GL30.GL_UNSIGNED_BYTE, null);
        glyphsPageCapacity = pageCount;
    }

    private void updateGlyphs(/*final GL30 gl*/) {

        final int width = glyphManager.getTextureWidth();
        final int height = glyphManager.getTextureHeight();
        final int pageCount = glyphManager.getGlyphPageCount();
        final int glyphCount = glyphManager.getGlyphCount();

        // If there have been new glyphs then some of then might be on the last page
        // buffered to the graphics card. We need to mark this last page as unbuffered
        // so that it gets buffered again.
        if (glyphCount > glyphsGlyphsBuffered && glyphsPagesBuffered > 0) {
            glyphsPagesBuffered--;
            glyphsPageBuffers.remove(glyphsPageBuffers.size() - 1);
        }

        glyphsGlyphsBuffered = glyphCount;

        if (pageCount > glyphsPageCapacity) {
//            GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, glyphsTextureName[0]);
            // TODO_TT:
//            gl.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, INTERNAL_FORMAT, width, height, pageCount, 0, EXTERNAL_FORMAT, GL30.GL_UNSIGNED_BYTE, null);
            glyphsPageCapacity = pageCount;
            glyphsPagesBuffered = 0;
        }

        while (glyphsPagesBuffered < pageCount) {

            final ByteBuffer pixelBuffer;
            if (glyphsPageBuffers.size() > glyphsPagesBuffered) {
                pixelBuffer = glyphsPageBuffers.get(glyphsPagesBuffered);
                pixelBuffer.rewind();
            } else {
                pixelBuffer = ByteBuffer.allocateDirect(width * height);
                glyphManager.readGlyphTexturePage(glyphsPagesBuffered, pixelBuffer);
                glyphsPageBuffers.add(pixelBuffer);
                pixelBuffer.flip();
            }

//            GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, glyphsTextureName[0]);
//            GL30.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, 0, 0, glyphsPagesBuffered, width, height, 1, EXTERNAL_FORMAT, GL30.GL_UNSIGNED_BYTE, pixelBuffer);

            glyphsPagesBuffered++;
        }
    }

    private void bindGlyphs(/*final GL30 gl*/int uniformLocation, int textureUnit) {
//        GL30.glUniform1i(uniformLocation, textureUnit);
//        GL30.glActiveTexture(GL30.GL_TEXTURE0 + textureUnit);
//        GL30.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, glyphsTextureName[0]);
    }
}
