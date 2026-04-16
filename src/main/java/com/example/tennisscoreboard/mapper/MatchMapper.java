package com.example.tennisscoreboard.mapper;

import com.example.tennisscoreboard.dto.MatchDto;
import com.example.tennisscoreboard.entity.Match;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = PlayerMapper.class)
public interface MatchMapper {
    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    MatchDto toDto(Match match);
}
