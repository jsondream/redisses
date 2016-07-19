package com.jsondream.redisses.common;

import junit.framework.TestCase;
import org.springframework.cglib.beans.BeanMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     测试类
 * </p>
 *
 * @author wangguangdong
 * @version 1.0
 * @Date 16/7/19
 */
public class ObjectCommonUtilsTest extends TestCase {

    public void test() {
        Map<String, String> map = null;
        Game game = new Game();
        game.setId("10001");
        game.setUserId("10002");
        game.setGameAliasName("lol");
        game.setGameName("英雄联盟");
        game.setGameId("102");
        String key = "obj.key.id.10001";
        //System.out.println("属性列表是:"+ObjectCommonUtils.getAllFieldName(game.getClass()));
        assertNotNull(ObjectCommonUtils.getAllFieldName(game.getClass()));
        //System.out.println(ObjectCommonUtils.objResolveToMap(game));
        assertEquals("{gameId=102, gameName=英雄联盟, gameAliasName=lol, id=10001, userId=10002}",
            ObjectCommonUtils.objResolveToMap(game).toString());
        //System.out.println(ObjectCommonUtils.objResolveToMap(game, "id", "userId","gameName"));
        assertEquals("{gameName=英雄联盟, id=10001, userId=10002}",
            ObjectCommonUtils.objResolveToMap(game, "id", "userId", "gameName").toString());
        //        System.out.println(
        //            "about v1:  " + ObjectCommonUtils.objResolveToMap(game, "id", "userId",
        //                "gameName1", "gameId"));
        assertNotNull(
            ObjectCommonUtils.objResolveToMap(game, "id", "userId", "gameName1", "gameId"));
        //        System.out.println(
        //            "about v2:  " + ObjectCommonUtils.objResolveToMapV2(game, "id", "userId",
        //                "gameName1", "gameId"));
        assertEquals("{id=10001, userId=10002}",
            ObjectCommonUtils.objResolveToMapV2(game, "id", "userId", "gameName1", "gameId")
                .toString());
        List<String> myList = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            myList.add("");
        }

        /** 正常执行的时候*/
        // v1
        long start = System.currentTimeMillis();
        myList.stream().forEach(
            r -> ObjectCommonUtils.objResolveToMap(game, "id", "userId", "gameName", "gameId"));
        System.out
            .println("v1正常执行完" + myList.size() + "次的时间是" + (System.currentTimeMillis() - start));

        // v2
        start = System.currentTimeMillis();
        myList.stream().forEach(
            r -> ObjectCommonUtils.objResolveToMapV2(game, "id", "userId", "gameName", "gameId"));
        System.out
            .println("v2正常执行完" + myList.size() + "次的时间是" + (System.currentTimeMillis() - start));

        // v3
        start = System.currentTimeMillis();
        try {

            myList.stream().forEach(r -> {
                try {
                    ObjectCommonUtils.objResolveToMapV3(game, "id", "userId", "gameName", "gameId");
                } catch (Exception e) {
                }
            });
        } finally {

        }

        System.out
            .println("v3正常执行完" + myList.size() + "次的时间是" + (System.currentTimeMillis() - start));

        // v4
        start = System.currentTimeMillis();
        myList.stream().forEach(r -> {
            try {
                ObjectCommonUtils.convertBean(game);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out
            .println("v4正常执行完" + myList.size() + "次的时间是" + (System.currentTimeMillis() - start));

        Map<String, String> map11 = new HashMap<String, String>();
        // v5
        start = System.currentTimeMillis();
        myList.stream().forEach(r -> {
            // 这种方式比v4转换map要快
            BeanMap b = BeanMap.create(game);
            //但是在取得特定类型(如Map<String, String>)的时候putAll的话没有v4快
            map11.putAll(b);

        });
        System.out
            .println("v4正常执行完" + myList.size() + "次的时间是" + (System.currentTimeMillis() - start));
    }

    public class Game implements Serializable {

        // 游历id
        private String id;
        // 用户id
        private String userId;
        // 游戏id
        private String gameId;
        // 游戏名称
        private String gameName;
        // 游戏别名
        private String gameAliasName;
        // 游戏icon
        private String icon;

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getGameName() {
            return this.gameName;
        }

        public void setGameName(String gameName) {
            this.gameName = gameName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }

        public String getGameAliasName() {
            return gameAliasName;
        }

        public void setGameAliasName(String gameAliasName) {
            this.gameAliasName = gameAliasName;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

    }
}
