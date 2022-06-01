package com.crucistau.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crucistau.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crucisTau
 * @since 2022-04-04
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from tb_role where flag = #{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}
