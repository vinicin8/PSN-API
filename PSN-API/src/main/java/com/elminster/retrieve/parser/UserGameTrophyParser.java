package com.elminster.retrieve.parser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.elminster.common.parser.IParser;
import com.elminster.common.parser.ParseException;
import com.elminster.common.util.CollectionUtil;
import com.elminster.common.util.DateUtil;
import com.elminster.common.util.StringUtil;
import com.elminster.retrieve.data.game.TrophyType;
import com.elminster.retrieve.data.json.JsonCompareTrophies;
import com.elminster.retrieve.data.json.JsonGameUser;
import com.elminster.retrieve.data.json.JsonTrophyInfo;
import com.elminster.retrieve.data.user.PSNUserTrophy;

public class UserGameTrophyParser implements IParser<JsonCompareTrophies, List<PSNUserTrophy>> {

  /** the logger. */
  private static final Log logger = LogFactory.getLog(UserGameTrophyParser.class);
  
  /**
   * {@inheritDoc}
   */
  @Override
  public List<PSNUserTrophy> parse(JsonCompareTrophies json) throws ParseException {
    if (null == json) {
      return null;
    }
    List<PSNUserTrophy> userTrophies = new ArrayList<PSNUserTrophy>();
    String gameId = json.getGameId();
    List<JsonGameUser> gameUsers = json.getUsers();
    if (CollectionUtil.isNotEmpty(gameUsers)) {
      // get the opponent's trophy list
      JsonGameUser gameUser = gameUsers.get(0);
      if (null != gameUser) {
        List<JsonTrophyInfo> list = gameUser.getList();
        if (CollectionUtil.isNotEmpty(list)) {
          for (JsonTrophyInfo ti : list) {
            PSNUserTrophy userTrophy = new PSNUserTrophy();
            userTrophy.setDescription(ti.getDesc());
            String trophyWon = ti.getTrophyWon();
            if (StringUtil.isNotEmpty(trophyWon)) {
              userTrophy.setEarned(true);
              String trophyStamp = ti.getTrophyStamp();
              // eg. 2015-01-07T15:45:12Z
              Date date = null;
              try {
                date = DateUtil.parserDateStr(trophyStamp, DateUtil.ISO8601_FORMAT);
              } catch (java.text.ParseException e) {
                logger.warn("failed to parse the date: " + trophyStamp);
              }
              userTrophy.setEarnedDate(date);
            }
            userTrophy.setGameId(gameId);
            userTrophy.setImageUrl(ti.getImgUrl());
            userTrophy.setTitle(ti.getTitle());
            userTrophy.setTrophyId(String.valueOf(ti.getTrophyId()));
            userTrophy.setType(TrophyType.getTrophyType(ti.getType()));
            userTrophies.add(userTrophy);
          }
        }
      }
    }
    
    return userTrophies;
  }

}