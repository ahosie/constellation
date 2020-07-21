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
package au.gov.asd.tac.constellation.visual.vulkan.renderables;

import au.gov.asd.tac.constellation.utilities.graphics.Vector3i;
import au.gov.asd.tac.constellation.utilities.icon.ConstellationIcon;
import au.gov.asd.tac.constellation.utilities.icon.DefaultIconProvider;
import au.gov.asd.tac.constellation.utilities.icon.IconManager;
import au.gov.asd.tac.constellation.visual.vulkan.resourcetypes.CVKBuffer;
import au.gov.asd.tac.constellation.visual.vulkan.resourcetypes.CVKCommandBuffer;
import au.gov.asd.tac.constellation.visual.vulkan.CVKDevice;
import au.gov.asd.tac.constellation.visual.vulkan.CVKSwapChain;
import static au.gov.asd.tac.constellation.visual.vulkan.CVKUtils.CVKLOGGER;
import static au.gov.asd.tac.constellation.visual.vulkan.CVKUtils.checkVKret;
import java.awt.image.BufferedImage;
import static au.gov.asd.tac.constellation.visual.vulkan.CVKUtils.debugging;
import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;
import au.gov.asd.tac.constellation.visual.vulkan.resourcetypes.CVKImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY;
import static org.lwjgl.vulkan.VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT;
import static org.lwjgl.vulkan.VK10.VK_COMPARE_OP_NEVER;
import static org.lwjgl.vulkan.VK10.VK_FILTER_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_SRGB;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_UNDEFINED;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_TILING_OPTIMAL;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_SAMPLED_BIT;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_USAGE_TRANSFER_DST_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_COHERENT_BIT;
import static org.lwjgl.vulkan.VK10.VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT;
import static org.lwjgl.vulkan.VK10.VK_NULL_HANDLE;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE;
import static org.lwjgl.vulkan.VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCmdCopyBufferToImage;
import static org.lwjgl.vulkan.VK10.vkCreateSampler;
import static org.lwjgl.vulkan.VK10.vkDestroySampler;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkSamplerCreateInfo;


public class CVKIconTextureAtlas extends CVKRenderable {
    public static final int ICON_WIDTH = 256;
    public static final int ICON_HEIGHT = 256;
    public static final int ICON_COMPONENTS = 4; //ARGB, 1 byte each
    public static final int ICON_SIZE_PIXELS = ICON_WIDTH * ICON_HEIGHT;    
    public static final int ICON_SIZE_BYTES = ICON_SIZE_PIXELS * ICON_COMPONENTS;
    
    public static int textureWidth = 2048; //copied from JOGL but should be calculated, will mean adding more params to UBOs
    public static int textureHeight = 2048;      
    public static int iconsPerLayer = 0;
    public static int iconsPerRow = 0;
    public static int rowsPerLayer = 0;
    public static int textureLayers = 1;
    
    // These icons must be permanently present at these pre-defined indexes.
    // The shaders expect them to be there.
    public static final int HIGHLIGHTED_ICON_INDEX = 0;
    public static final String HIGHLIGHTED_ICON = DefaultIconProvider.HIGHLIGHTED.getExtendedName();
    public static final int UNKNOWN_ICON_INDEX = 1;
    public static final String UNKNOWN_ICON = DefaultIconProvider.UNKNOWN.getExtendedName();

    // Icons for drawing loops.
    public static final int LOOP_DIRECTED_ICON_INDEX = 2;
    public static final String LOOP_DIRECTED_ICON = DefaultIconProvider.LOOP_DIRECTED.getExtendedName();
    public static final int LOOP_UNDIRECTED_ICON_INDEX = 3;
    public static final String LOOP_UNDIRECTED_ICON = DefaultIconProvider.LOOP_UNDIRECTED.getExtendedName();

    // Noise indicator to be drawn when there are too many icons for the texture array.
    public static final int NOISE_ICON_INDEX = 4;
    public static final String NOISE_ICON = DefaultIconProvider.NOISE.getExtendedName();

    // Transparency.
    public static final int TRANSPARENT_ICON_INDEX = 5;
    public static final String TRANSPARENT_ICON = DefaultIconProvider.TRANSPARENT.getExtendedName();
    
    
    // Instance members
    private final CVKDevice cvkDevice;
    private CVKImage cvkAtlasImage = null;
    private long hAtlasSampler = VK_NULL_HANDLE;
    private final LinkedHashMap<String, Integer> loadedIcons = new LinkedHashMap<>();
    private int maxIcons = Short.MAX_VALUE; //replace this with a calculated value
    private int lastTransferedIconCount = 0;
    
    
    public int GetAtlasIconCount() { return lastTransferedIconCount; }
    public long GetAtlasImageViewHandle() { return cvkAtlasImage.GetImageViewHandle(); }
    public long GetAtlasSamplerHandle() { return hAtlasSampler; }

    @Override
    public int DeviceInitialised(CVKDevice cvkDevice) {
        return VK_SUCCESS;
    }   
    
    @Override
    public int GetVertexCount(){ return 0; }
    
    @Override
    public int RecordCommandBuffer(CVKSwapChain cvkSwapChain, VkCommandBufferInheritanceInfo inheritanceInfo, int index){
        return VK_SUCCESS;            
    }   
    
    @Override
    public int DisplayUpdate(CVKSwapChain cvkSwapChain, int imageIndex) {
        return VK_SUCCESS;
    }
    
    @Override
    public int SwapChainRecreated(CVKSwapChain cvkSwapChain) {
        return VK_SUCCESS;
    }
        
    @Override
    public void IncrementDescriptorTypeRequirements(int descriptorTypeCounts[], int descriptorSetCount) {
    }
        
    // This could be replaced with a templated Pair type
    private class IndexedConstellationIcon {
        public final int index;
        public final ConstellationIcon icon;
        IndexedConstellationIcon(int index, ConstellationIcon icon) {
            this.index = index;
            this.icon = icon;     
            CVKIconTextureAtlas.iconsPerRow = textureWidth/ICON_WIDTH;
            CVKIconTextureAtlas.rowsPerLayer = textureHeight/ICON_HEIGHT;
            CVKIconTextureAtlas.iconsPerLayer = CVKIconTextureAtlas.iconsPerRow * CVKIconTextureAtlas.rowsPerLayer;
            
        }
    }
    
    
    public CVKIconTextureAtlas(CVKDevice cvkDevice) {
        this.cvkDevice = cvkDevice;
        
        // These icons are guaranteed to be in the iconMap in this order.
        // They must be at these pre-defined indices so other code (in particular the shaders) can use them.
        // See *_INDEX constants above.
        for (final String iconName : new String[]{HIGHLIGHTED_ICON, UNKNOWN_ICON, LOOP_DIRECTED_ICON, LOOP_UNDIRECTED_ICON, NOISE_ICON, TRANSPARENT_ICON}) {
            AddIcon(iconName);
        }        
    }
    
      
    public int AddIcon(final String label) {
        final Integer iconIndex = loadedIcons.get(label);
        if (iconIndex == null) {
            final int index = loadedIcons.size();
            if (index >= maxIcons) {
                // Too many icons: return NOISE icon.
                return NOISE_ICON_INDEX;
            }

            loadedIcons.put(label, index);
            return index;
        }

        return iconIndex;
    }
    
    /**
     * Converts an icon index into column, row and layer indices
     * 
     * Example where 
     *   iconsPerRow   = 4
     *   rowsPerLayer  = 5
     *   iconsPerLayer = 20
     * 
     *  0  1  2  3
     *  4  5  6  7
      * 8  9 10 11
     * 12 13 14 15
     * 16 17 18 19
     * 
     * 20 21 22 23
     * 24 25 26 27
     * 28 29 30 31
     * 32 33 34 35
     * 36 37 38 39
     * 
     * 40 41 42 43
     * 44 45 46 47
     * 48 49 50 51
     * 52 53 54 55
     * 56 57 58 59
     * 
     * index = 27
     * x = 27 % 4	 = 3       
     * y = (27 % 20) / 4 = 1
     * z = 27 / 20       = 1
     * 
     * index = 38
     * x = 38 % 4        = 2       
     * y = (38 % 20) / 4 = 4
     * z = 38 / 20       = 1
     * 
     * index = 48
     * x = 48 % 4	 = 0       
     * y = (48 % 20) / 4 = 2
     * z = 48 / 20       = 2
     * 
     * @param index
     * @return vector of lookup indices
     */
    public Vector3i IndexToTextureIndices(int index) {
        return new Vector3i(index % CVKIconTextureAtlas.iconsPerRow,
                            (index % CVKIconTextureAtlas.iconsPerLayer) / CVKIconTextureAtlas.iconsPerRow,
                            index / iconsPerLayer);     
    }
    
    public long IndexToBufferOffset(int index) {
        long offset = 0;
        Vector3i texIndices = IndexToTextureIndices(index);  
        
        // add size of previous icons on this row
        offset += texIndices.getX() * ICON_SIZE_BYTES;
        
        // add size of previous rows on this layer
        offset += texIndices.getY() * iconsPerRow * ICON_SIZE_BYTES;
                
        // add size of previous layers
        offset += texIndices.getZ() * iconsPerLayer * ICON_SIZE_BYTES;
        
        int povCalc = index * ICON_SIZE_BYTES;
        assert(offset == povCalc);
        
        return offset;
    }
    
    private static boolean IsEmpty(ByteBuffer buffer) {
        for (int i = 0; i < buffer.capacity(); ++i) {
            if (buffer.get(i) != 0) {
                return false;
            }
        }
        return true;
    }
    
    private int AddIconsToAtlas(List<IndexedConstellationIcon> icons) {
        int ret;
        
        try (MemoryStack stack = stackPush()) {
            // Allocate one big staging buffer for all of the images.  This will be inefficient if
            // this function is called often with a growing list of icons.  If that happens the best
            // solution will be to better manage the updating of the atlas rather than minimise the
            // staging buffer size.
            int requiredLayers = (icons.size() / iconsPerLayer) + 1;
            
            // Create destination image            
            cvkAtlasImage = CVKImage.Create(cvkDevice, 
                                            textureWidth, 
                                            textureHeight, 
                                            requiredLayers, 
                                            VK_FORMAT_R8G8B8A8_SRGB, //non-linear format to give more fidelity to the hues we are most able to perceive
                                            VK_IMAGE_TILING_OPTIMAL, //we usually sample rectangles rather than long straight lines
                                            VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_SAMPLED_BIT,
                                            VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                                            VK_IMAGE_ASPECT_COLOR_BIT);              
            
           // Command to copy pixels and transition formats
            CVKCommandBuffer cvkCopyCmd = CVKCommandBuffer.Create(cvkDevice, VK_COMMAND_BUFFER_LEVEL_PRIMARY);
            cvkCopyCmd.DEBUGNAME = "CVKIconTextureAtlas cvkCopyCmd";
            ret = cvkCopyCmd.Begin(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            checkVKret(ret);            
            
            // Transition image from undefined to transfer destination optimal
            ret = cvkAtlasImage.Transition(cvkCopyCmd, VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL);
            checkVKret(ret);            

            int numIcons = icons.size();
            List<CVKBuffer> iconStagingBuffers = new ArrayList<>(numIcons);
            
            for (int iIcon = 0; iIcon < numIcons; ++iIcon) {
                IndexedConstellationIcon el = icons.get(iIcon);
                BufferedImage iconImage = el.icon.buildBufferedImage();     
               
                CVKBuffer cvkStagingBuffer = CVKBuffer.Create(cvkDevice, 
                                                              ICON_SIZE_BYTES, 
                                                              VK_BUFFER_USAGE_TRANSFER_SRC_BIT,
                                                              VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT);  
                cvkStagingBuffer.DEBUGNAME = String.format("CVKIconTextureAtlas cvkStagingBuffer %d", iIcon);
                
               // Convert the buffered image if its not in our desired state.
                if (TYPE_4BYTE_ABGR != iconImage.getType()) {
                    BufferedImage convertedImg = new BufferedImage(iconImage.getWidth(), iconImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    convertedImg.getGraphics().drawImage(iconImage, 0, 0, null);
                    iconImage = convertedImg;
                }     
                int width = iconImage.getWidth();
                int height = iconImage.getHeight();
                assert(width <= ICON_WIDTH);
                assert(height <= ICON_HEIGHT);
                
                // Get pixel data into a direct buffer
                ByteBuffer pixels = ByteBuffer.wrap(((DataBufferByte) iconImage.getRaster().getDataBuffer()).getData());
                if (debugging) {
                    if (IsEmpty(pixels) && el.index != TRANSPARENT_ICON_INDEX) {
                        CVKLOGGER.warning(String.format("Icon %d is empty", el.index));
                    }
                }
                
                // To save us having to swizzle with ever render, do it now (ABGR->RGBA, AWT what were you thinking?)
                for (int i = 0; i < pixels.capacity(); i+=4) {
                    byte a = pixels.get(i);
                    byte b = pixels.get(i+1);
                    
                    // swap alpha and red
                    pixels.put(i, pixels.get(i+3));
                    pixels.put(i+3, a);
                    
                    // swap blue and green
                    pixels.put(i+1, pixels.get(i+2));
                    pixels.put(i+2, b);             
                    
                    // This made the pixels grey, source might be unsigned?
//                    pixels.put(i, Byte.MAX_VALUE);
//                    pixels.put(i+1, Byte.MAX_VALUE);
//                    pixels.put(i+2, Byte.MAX_VALUE);
                }
                             
                // Copy pixels, note for undersized icons we need extra offsets to pad the top and sides                    
                if (width == ICON_WIDTH && height == ICON_HEIGHT) {
                    assert(pixels.capacity() == ICON_SIZE_BYTES);
                    cvkStagingBuffer.Put(pixels, 0, 0, ICON_SIZE_BYTES);
                } else {
                    // Zero this buffer so undersized icons are padded with transparent pixels
                    cvkStagingBuffer.ZeroMemory();     
                    
                    // Offsets to centre the icon are in pixels
                    int colOffset = (ICON_WIDTH - width) / 2;
                    int rowOffset = (ICON_HEIGHT - height) / 2;
                    
                    // Adjust the start position to the right row
                    for (int iRow = 0; iRow < height; ++iRow) {       
                        // offset to the start of this row
                        int writePos = (iRow + rowOffset) * ICON_WIDTH * ICON_COMPONENTS;
                        assert(((iRow + rowOffset + 1) * ICON_WIDTH * ICON_COMPONENTS) <= ICON_SIZE_BYTES);
                        
                        // offset from the start of the row to the start of the icon
                        writePos += colOffset * ICON_COMPONENTS;
                        int readPos = iRow * width * ICON_COMPONENTS;
                        cvkStagingBuffer.Put(pixels, writePos, readPos, width * ICON_COMPONENTS);
                    }
                }
                iconStagingBuffers.add(cvkStagingBuffer);
                                              
                // Calculate offset into staging buffer for the current image layer
                Vector3i texIndices = IndexToTextureIndices(el.index);

                // Setup a buffer image copy structure for the current image layer
                VkBufferImageCopy.Buffer copyLayerBuffer = VkBufferImageCopy.callocStack(1, stack);
                VkBufferImageCopy copyLayer = copyLayerBuffer.get(0);
                copyLayer.bufferOffset(0);
                copyLayer.bufferRowLength(ICON_WIDTH);//0);    // Tightly packed
                copyLayer.bufferImageHeight(ICON_HEIGHT);//0);  // Tightly packed
                copyLayer.imageSubresource().aspectMask(VK_IMAGE_ASPECT_COLOR_BIT);
                copyLayer.imageSubresource().mipLevel(0);
                copyLayer.imageSubresource().baseArrayLayer(texIndices.getW());
                copyLayer.imageSubresource().layerCount(1);
                copyLayer.imageOffset().set(texIndices.getU() * ICON_WIDTH,
                                            texIndices.getV() * ICON_HEIGHT, 
                                            0);
                copyLayer.imageExtent(VkExtent3D.callocStack(stack).set(ICON_WIDTH, ICON_HEIGHT, 1));
//                CVKLOGGER.info(String.format("Icon %d: (%d, %d)",
//                        el.index, texIndices.getU() * ICON_WIDTH, texIndices.getV() * ICON_HEIGHT));
                
                // Copy staging buffer to atlas texture
                vkCmdCopyBufferToImage(cvkCopyCmd.GetVKCommandBuffer(),
                                       cvkStagingBuffer.GetBufferHandle(),
                                       cvkAtlasImage.GetImageHandle(),
                                       VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL,
                                       copyLayerBuffer);                               
            }
            
            // Now the image is populated, transition it for reading
            ret = cvkAtlasImage.Transition(cvkCopyCmd, VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL);
            checkVKret(ret);      
            
            // Ok nothing has actually happened yet, time to execute the transitions and copy
            ret = cvkCopyCmd.EndAndSubmit();
            checkVKret(ret);    
            cvkCopyCmd.Destroy();
            
            // We've finished with the staging buffer
            iconStagingBuffers.forEach(el -> {el.Destroy();});
            
            // Create a sampler to match the image.  Note the sampler allows us to sample
            // an image but isn't tied to a specific image, note the lack of image or 
            // imageview parameters below.
            VkSamplerCreateInfo vkSamplerCreateInfo = VkSamplerCreateInfo.callocStack(stack);                        
            vkSamplerCreateInfo.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO);
            vkSamplerCreateInfo.maxAnisotropy(1.0f);
            vkSamplerCreateInfo.magFilter(VK_FILTER_LINEAR);
            vkSamplerCreateInfo.minFilter(VK_FILTER_LINEAR);
            vkSamplerCreateInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
            vkSamplerCreateInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
            vkSamplerCreateInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
            vkSamplerCreateInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
            vkSamplerCreateInfo.mipLodBias(0.0f);
            vkSamplerCreateInfo.maxAnisotropy(8);
            vkSamplerCreateInfo.compareOp(VK_COMPARE_OP_NEVER);
            vkSamplerCreateInfo.minLod(0.0f);
            vkSamplerCreateInfo.maxLod(0.0f);
            vkSamplerCreateInfo.borderColor(VK_BORDER_COLOR_FLOAT_OPAQUE_WHITE);
            
            LongBuffer pTextureSampler = stack.mallocLong(1);
            ret = vkCreateSampler(cvkDevice.GetDevice(), vkSamplerCreateInfo, null, pTextureSampler);
            checkVKret(ret);
            hAtlasSampler = pTextureSampler.get(0);
            assert(hAtlasSampler != VK_NULL_HANDLE);                       
        }
        
        return ret;
    }
    
    @Override
    public void Destroy() {
        if (cvkAtlasImage != null) {
            cvkAtlasImage.Destroy();
            cvkAtlasImage = null;            
        }

        if (hAtlasSampler != VK_NULL_HANDLE) {
            vkDestroySampler(cvkDevice.GetDevice(), hAtlasSampler, null);    
            hAtlasSampler = VK_NULL_HANDLE;
        }
        
        lastTransferedIconCount = 0;
    }
        
    
    public int Init() {
        assert(lastTransferedIconCount == 0);
        assert(cvkAtlasImage == null);
        assert(hAtlasSampler == VK_NULL_HANDLE);

        
//        final Set<String> iconNames = IconManager.getIconNames(false);
//        CVKLOGGER.info("\n====ALL ICONS====");
//        iconNames.forEach(el -> {CVKLOGGER.info(el);});
//        CVKLOGGER.info("");
        
        int ret = VK_SUCCESS;
        if (loadedIcons.size() > 0) {
            List<IndexedConstellationIcon> allIcons = new ArrayList<>();
            loadedIcons.entrySet().forEach(entry -> {                
                allIcons.add(new IndexedConstellationIcon(entry.getValue(), IconManager.getIcon(entry.getKey()))); 
            });
                        
            ret = AddIconsToAtlas(allIcons);
            checkVKret(ret);
            lastTransferedIconCount = loadedIcons.size();  
        }
        return ret;
    }
    
    @Override
    public int RecreateSharedResources(CVKSwapChain cvkSwapChain) {
        Destroy();
        return Init();
    }
    
    @Override
    public boolean SharedResourcesNeedUpdating() {
        return loadedIcons.size() > lastTransferedIconCount;
    }   
    
    @Override
    public VkCommandBuffer GetCommandBuffer(int imageIndex)
    {
        return null;
    }      
}
