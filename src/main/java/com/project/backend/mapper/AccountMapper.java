package com.project.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.backend.entity.dto.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    /**
     * 根据名字或者邮箱查找用户
     * @return 返回用户实体类
     */
    Account findByNameOrEmail();

}
