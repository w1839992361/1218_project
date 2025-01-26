package cn.backed.teriedu.service

import cn.backed.teriedu.entity.UpdateData
import com.baomidou.mybatisplus.extension.service.IService

interface UpdateService:IService<UpdateData> {
    fun getAllUpdateFiles(tagId: Long): List<String>
    fun getUpdateVersion(): String
    fun getTagFiles(tagId: Long): ArrayList<HashMap<String,String>>
}