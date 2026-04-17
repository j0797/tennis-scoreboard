package com.example.tennisscoreboard.mapper;


import com.example.tennisscoreboard.entity.Match;
import com.example.tennisscoreboard.model.OngoingMatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OngoingMatchMapper {
    OngoingMatchMapper INSTANCE = Mappers.getMapper(OngoingMatchMapper.class);

    @Mapping(source = "playerOne", target = "player1")
    @Mapping(source = "playerTwo", target = "player2")
    @Mapping(source = "winner", target = "winner")
    @Mapping(target = "id", ignore = true)
    Match toEntity(OngoingMatch ongoingMatch);
}
