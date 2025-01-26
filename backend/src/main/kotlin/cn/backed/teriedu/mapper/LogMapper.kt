package cn.backed.teriedu.mapper

import cn.backed.teriedu.entity.Log
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper


@Mapper
interface LogMapper:BaseMapper<Log> {
}