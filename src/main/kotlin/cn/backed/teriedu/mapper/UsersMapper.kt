package cn.backed.teriedu.mapper

import cn.backed.teriedu.entity.Users
import com.baomidou.mybatisplus.core.mapper.BaseMapper
import org.apache.ibatis.annotations.Mapper

@Mapper
interface UsersMapper : BaseMapper<Users>