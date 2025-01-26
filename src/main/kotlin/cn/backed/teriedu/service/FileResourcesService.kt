// FileResourcesService.kt
package cn.backed.teriedu.service

import cn.backed.teriedu.entity.FileResources
import cn.backed.teriedu.entity.enum.MediaType
import cn.backed.teriedu.entity.enum.ResourcesType
import com.baomidou.mybatisplus.extension.service.IService

interface FileResourcesService : IService<FileResources>{
    fun saveFileResources(fileName: String, filePath: String, mediaType: MediaType, resourceUUID: String, resourcesType: ResourcesType): Boolean
    fun getResourcesByResourceUUIDAndMediaTypeAndResourceType(resourceUUID: String ,mediaType: MediaType,resourceType: ResourcesType): List<FileResources>
    fun getByResourceUUID(resourceUUID: String , mediaType: MediaType):List<FileResources>
    fun getByResourceUUID(resourceUUID: String ):List<FileResources>
    fun getByID(id: String): FileResources
    fun queryLocalVideo(id: String): FileResources?
}