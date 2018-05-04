package game.data.conf.mapper;

import game.data.conf.entity.CardStarUps;

public interface CardStarUpsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CardStarUps record);

    int insertSelective(CardStarUps record);

    CardStarUps selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CardStarUps record);

    int updateByPrimaryKey(CardStarUps record);
}