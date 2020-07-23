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

import au.gov.asd.tac.constellation.visual.vulkan.CVKDevice;
import au.gov.asd.tac.constellation.visual.vulkan.CVKSwapChain;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferInheritanceInfo;
import au.gov.asd.tac.constellation.visual.vulkan.CVKVisualProcessor;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public abstract class CVKRenderable {
    protected CVKVisualProcessor parent;
    protected CVKDevice cvkDevice = null;
    
//    protected long pipelineLayout = 0;
//    protected long graphicsPipeline = 0;
//    protected List<CVKCommandBuffer> commandBuffers = null;
//    protected PointerBuffer handlePointer;
//    
//    protected long vertexBuffer = 0;
//    protected long vertexBufferMemory = 0;
//        
//    protected List<CVKBuffer> vertUniformBuffers = null;
//    protected List<CVKBuffer> geomUniformBuffers = null;
//    protected List<CVKBuffer> vertBuffers = null;
    
    protected boolean isDirty = true;
    protected boolean isInitialised = false;
    
    /*
        Cleanup
    */
    public abstract void Destroy();
    
    /*
        Returns the command buffer for the current Image being sent
        to the GFX drivers
    */
//    public VkCommandBuffer GetCommandBuffer(int index)
//    {
//        return commandBuffers.get(index).GetVKCommandBuffer(); 
//    }
    public abstract VkCommandBuffer GetCommandBuffer(int imageIndex);        
        
    /**
     * Called just before the swapchain is about to be destroyed allowing the
     * object to cleanup its resources.
     * 
     * @return error code
     */
    public abstract int DestroySwapChainResources();
    
    /**
     * 
     * Called just after the swapchain has been recreated
     * 
     * @param cvkSwapChain
     * @return error code
    */
    public abstract int CreateSwapChainResources(CVKSwapChain cvkSwapChain);
    public abstract void IncrementDescriptorTypeRequirements(int descriptorTypeCounts[], int descriptorSetCount);        
    public abstract int RecordCommandBuffer(VkCommandBufferInheritanceInfo inheritanceInfo, int index);

    /*
        Returns the number of vertices used in the vertex buffer
    */
    public abstract int GetVertexCount();

    /*
        TODO HYDRA: Clarify what this means
        Return true if this renderable needs to be updated
    */
    public boolean IsDirty(){ return isDirty; }

    public abstract int DeviceInitialised(CVKDevice cvkDevice);
    
    public boolean NeedsDisplayUpdate() { return false; }
    public int DisplayUpdate() { return VK_SUCCESS; }


    /**
     * Tasks that implement CVKRenderableUpdateTask are created in the VisualProcessor
     * thread in response to user input.  If those tasks have constructors that 
     * code will be executed in the VisualProcessor thread.  Code in the run method
     * is called from the render thread (AWT Event thread).
     */
    @FunctionalInterface
    public static interface CVKRenderableUpdateTask {
        public void run(int imageIndex);
    }         
}
