package game.data.conf.mapper;

import java.util.ArrayList;

import game.data.conf.entity.BookworldCommons;

public interface BookworldCommonsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BookworldCommons record);

    int insertSelective(BookworldCommons record);

    BookworldCommons selectByPrimaryKey(Integer id);
    
    ArrayList<BookworldCommons> selectBy1000();

    int updateByPrimaryKeySelective(BookworldCommons record);

    int updateByPrimaryKey(BookworldCommons record);
}