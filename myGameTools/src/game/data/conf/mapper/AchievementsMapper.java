package game.data.conf.mapper;

import game.data.conf.entity.Achievements;

public interface AchievementsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Achievements record);

    int insertSelective(Achievements record);

    Achievements selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Achievements record);

    int updateByPrimaryKey(Achievements record);
}