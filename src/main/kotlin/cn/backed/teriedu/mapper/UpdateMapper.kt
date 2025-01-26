package cn.backed.teriedu.mapper

import cn.backed.teriedu.entity.UpdateData
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UpdateMapper: BaseMapper<UpdateData> {
}