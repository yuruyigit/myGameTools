package game.data.conf.mapper;

import java.util.ArrayList;

import game.data.conf.entity.PrfWarZoneTitle;

public interface PrfWarZoneTitleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PrfWarZoneTitle record);

    int insertSelective(PrfWarZoneTitle record);

    PrfWarZoneTitle selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PrfWarZoneTitle record);

    int updateByPrimaryKey(PrfWarZoneTitle record);

	ArrayList<PrfWarZoneTitle> selectAll();
}