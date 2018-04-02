package game.tools.gzip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 
 *Module:          ZipUtil.java
 *Description:    对字符串的压缩及解压
 */
public class ZipStrUtil {

    public static void main(String[] args) throws IOException {
        // 字符串超过一定的长度
//        String str = "{\"buyPowerTimesDaily\":2,\"isSignAllReward\":false,\"loginType\":2,\"m\":\"21\",\"newMail\":false,\"nextDayCount\":3,\"openID\":\"\",\"openKey\":\"\",\"roleArr\":[{\"r\":{\"acceptStamina\":1,\"cId\":\"50\",\"curEndlessScore\":0,\"disasterId\":0,\"disasterIntegra\":0,\"endlessRankArr\":[2,0,0],\"endlessRankCount\":\"2,0,0,\",\"exp\":11600,\"fighting\":239,\"headIco\":\"images/fakeFriendList/icon/user6.png\",\"id\":10010000000050,\"lanternNum\":3,\"lanternTime\":1438660748381,\"lastWeekScore\":0,\"leaseCardArr\":[[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0]],\"level\":13,\"lineupUseCardArr\":[[10010000000050,74,2000002,1,1,0,1438660627981,44,0],[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0],[0,0,0,0,0,0,0,0,0]],\"lineupUseCardStr\":\"10010000000050:74:2000002:1:1:0:1438660627981:44:0,\",\"maxEndlessScore\":0,\"name\":\"岩石的塞西尔\",\"sex\":0,\"token\":\"bcdf79e9-0be2-4c22-9de3-0d9a0ec75462\",\"useHeroId\":1000001,\"useHeroLevel\":1,\"useLineupId\":50,\"userId\":\"visitor_1_cfd4986e-2052-406e-9a99-e7310850df19\",\"vipId\":0,\"vipPoint\":0,\"weekScoreArr\":[0,0]},\"rc\":{\"battleGoldPer\":0,\"bossTimes\":0,\"cardGrid\":0,\"dayBattleClearCount\":0,\"dayBossBattleCount\":\"5300002:2,\",\"dayBossBattleCountArr\":[[5300002,2]],\"dayDiamondGachaCount\":0,\"dayGoldGachaCount\":0,\"dayHireCardCount\":0,\"disasterCycleCount\":0,\"disasterKillCount\":0,\"disasterReliveCount\":0,\"foreveryDeathRelive\":0,\"gachaDiamondTenCount\":0,\"loginCount\":1,\"nextDayLoginCount\":3,\"powerBuy\":0,\"saoDan\":0,\"signCount\":0},\"rr\":{\"blessing\":0,\"buyPowerTimesDaily\":2,\"diamond\":0,\"disasterSoul\":0,\"exploit\":0,\"gold\":3000,\"leaseCardGold\":0,\"stamina\":195},\"rs\":{\"dayGetActivityId\":\"\",\"daySendStaminaStr\":\"\",\"exploitRefreshTimePos\":0,\"hireCardIdStr\":\"\",\"leaseLogStr\":\"\",\"setting\":\"1,1,1\"}}],\"seq\":1,\"t\":\"1\",\"todaySign\":false}";
//    	String str = "{\"buyPowerTimesDaily\":2,\"isSignAllReward\":false,\"loginType\":{\"buyPowerTimesDaily\":2,\"isSignAllReward\":false,\"loginType\":";
    	String str = "{\"buyPowerTimesDaily\":2,\"curTime\":1441094233212,\"isBattle\":false,\"isSignAllReward\":false,\"loginType\":2,\"m\":\"21\",\"newMail\":false,\"nextDayCount\":5,\"roleArr\":[{\"r\":{\"acceptStamina\":1,\"cId\":\"37\",\"curEndlessScore\":27739,\"disasterId\":0,\"disasterIntegra\":0,\"endlessRankCount\":\"0,0,0\",\"exp\":14937,\"fighting\":1627,\"headIco\":\"http://q.qlogo.cn/qqapp/1104634022/96B770BB27CE5C369E4E4BDFAF6AF6B7/100\",\"id\":10010000000037,\"lanternNum\":3,\"lanternTime\":1440657373158,\"lastWeekScore\":0,\"level\":14,\"lineupUseCardStr\":\"10010000000037:17581:2010018:10:2:444:1440588906788:296:3,\",\"maxEndlessScore\":27739,\"name\":\"阿树\",\"sex\":1,\"sign\":\"\",\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"useHeroCardStr\":\"2010018:10:2,2010002:10:3,2010012:13:3,\",\"useHeroId\":1000101,\"useHeroLevel\":11,\"useLineupId\":37,\"userId\":\"mobileQQ_96B770BB27CE5C369E4E4BDFAF6AF6B7\",\"vipId\":0,\"vipPoint\":0,\"weekScore\":\"0,0\",\"weekScoreArr\":[0,0]},\"rc\":{\"battleGoldPer\":0,\"bossTimes\":0,\"bossTryCount\":0,\"cardGrid\":0,\"dayBattleClearCount\":0,\"dayDiamondGachaCount\":0,\"dayGoldGachaCount\":0,\"dayGoldGachaFreeCount\":0,\"dayHireCardCount\":0,\"disasterCycleCount\":0,\"disasterKillCount\":0,\"disasterReliveCount\":0,\"foreveryDeathRelive\":0,\"gachaDiamondTenCount\":5,\"loginCount\":1,\"nextDayLoginCount\":5,\"powerBuy\":0,\"saoDan\":0,\"signCount\":0},\"rr\":{\"blessing\":0,\"buyPowerTimesDaily\":2,\"diamond\":645,\"disasterSoul\":0,\"exploit\":0,\"gold\":252452,\"leaseCardGold\":0,\"stamina\":144,\"starBead\":2},\"rs\":{\"dayGetActivityId\":\"\",\"getSevenActivityId\":\"18,19,\",\"guide\":\"1,2,3,7,8,4,4,4,4,4,4,4,4,4,7,6,9,10,11,12,13,15,16,17,14,18\",\"hireCardIdStr\":\"\",\"hireCardIdStrs\":\"\",\"leaseLogStr\":\"\",\"setting\":\"1,1,1\",\"starChartLevel\":22}}],\"sevenIdStr\":\"18,19,\",\"t\":\"2\",\"tag\":2,\"todaySign\":false,\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"version\":1}{\"buyPowerTimesDaily\":2,\"curTime\":1441094233212,\"isBattle\":false,\"isSignAllReward\":false,\"loginType\":2,\"m\":\"21\",\"newMail\":false,\"nextDayCount\":5,\"roleArr\":[{\"r\":{\"acceptStamina\":1,\"cId\":\"37\",\"curEndlessScore\":27739,\"disasterId\":0,\"disasterIntegra\":0,\"endlessRankCount\":\"0,0,0\",\"exp\":14937,\"fighting\":1627,\"headIco\":\"http://q.qlogo.cn/qqapp/1104634022/96B770BB27CE5C369E4E4BDFAF6AF6B7/100\",\"id\":10010000000037,\"lanternNum\":3,\"lanternTime\":1440657373158,\"lastWeekScore\":0,\"level\":14,\"lineupUseCardStr\":\"10010000000037:17581:2010018:10:2:444:1440588906788:296:3,\",\"maxEndlessScore\":27739,\"name\":\"阿树\",\"sex\":1,\"sign\":\"\",\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"useHeroCardStr\":\"2010018:10:2,2010002:10:3,2010012:13:3,\",\"useHeroId\":1000101,\"useHeroLevel\":11,\"useLineupId\":37,\"userId\":\"mobileQQ_96B770BB27CE5C369E4E4BDFAF6AF6B7\",\"vipId\":0,\"vipPoint\":0,\"weekScore\":\"0,0\",\"weekScoreArr\":[0,0]},\"rc\":{\"battleGoldPer\":0,\"bossTimes\":0,\"bossTryCount\":0,\"cardGrid\":0,\"dayBattleClearCount\":0,\"dayDiamondGachaCount\":0,\"dayGoldGachaCount\":0,\"dayGoldGachaFreeCount\":0,\"dayHireCardCount\":0,\"disasterCycleCount\":0,\"disasterKillCount\":0,\"disasterReliveCount\":0,\"foreveryDeathRelive\":0,\"gachaDiamondTenCount\":5,\"loginCount\":1,\"nextDayLoginCount\":5,\"powerBuy\":0,\"saoDan\":0,\"signCount\":0},\"rr\":{\"blessing\":0,\"buyPowerTimesDaily\":2,\"diamond\":645,\"disasterSoul\":0,\"exploit\":0,\"gold\":252452,\"leaseCardGold\":0,\"stamina\":144,\"starBead\":2},\"rs\":{\"dayGetActivityId\":\"\",\"getSevenActivityId\":\"18,19,\",\"guide\":\"1,2,3,7,8,4,4,4,4,4,4,4,4,4,7,6,9,10,11,12,13,15,16,17,14,18\",\"hireCardIdStr\":\"\",\"hireCardIdStrs\":\"\",\"leaseLogStr\":\"\",\"setting\":\"1,1,1\",\"starChartLevel\":22}}],\"sevenIdStr\":\"18,19,\",\"t\":\"2\",\"tag\":2,\"todaySign\":false,\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"version\":1}{\"buyPowerTimesDaily\":2,\"curTime\":1441094233212,\"isBattle\":false,\"isSignAllReward\":false,\"loginType\":2,\"m\":\"21\",\"newMail\":false,\"nextDayCount\":5,\"roleArr\":[{\"r\":{\"acceptStamina\":1,\"cId\":\"37\",\"curEndlessScore\":27739,\"disasterId\":0,\"disasterIntegra\":0,\"endlessRankCount\":\"0,0,0\",\"exp\":14937,\"fighting\":1627,\"headIco\":\"http://q.qlogo.cn/qqapp/1104634022/96B770BB27CE5C369E4E4BDFAF6AF6B7/100\",\"id\":10010000000037,\"lanternNum\":3,\"lanternTime\":1440657373158,\"lastWeekScore\":0,\"level\":14,\"lineupUseCardStr\":\"10010000000037:17581:2010018:10:2:444:1440588906788:296:3,\",\"maxEndlessScore\":27739,\"name\":\"阿树\",\"sex\":1,\"sign\":\"\",\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"useHeroCardStr\":\"2010018:10:2,2010002:10:3,2010012:13:3,\",\"useHeroId\":1000101,\"useHeroLevel\":11,\"useLineupId\":37,\"userId\":\"mobileQQ_96B770BB27CE5C369E4E4BDFAF6AF6B7\",\"vipId\":0,\"vipPoint\":0,\"weekScore\":\"0,0\",\"weekScoreArr\":[0,0]},\"rc\":{\"battleGoldPer\":0,\"bossTimes\":0,\"bossTryCount\":0,\"cardGrid\":0,\"dayBattleClearCount\":0,\"dayDiamondGachaCount\":0,\"dayGoldGachaCount\":0,\"dayGoldGachaFreeCount\":0,\"dayHireCardCount\":0,\"disasterCycleCount\":0,\"disasterKillCount\":0,\"disasterReliveCount\":0,\"foreveryDeathRelive\":0,\"gachaDiamondTenCount\":5,\"loginCount\":1,\"nextDayLoginCount\":5,\"powerBuy\":0,\"saoDan\":0,\"signCount\":0},\"rr\":{\"blessing\":0,\"buyPowerTimesDaily\":2,\"diamond\":645,\"disasterSoul\":0,\"exploit\":0,\"gold\":252452,\"leaseCardGold\":0,\"stamina\":144,\"starBead\":2},\"rs\":{\"dayGetActivityId\":\"\",\"getSevenActivityId\":\"18,19,\",\"guide\":\"1,2,3,7,8,4,4,4,4,4,4,4,4,4,7,6,9,10,11,12,13,15,16,17,14,18\",\"hireCardIdStr\":\"\",\"hireCardIdStrs\":\"\",\"leaseLogStr\":\"\",\"setting\":\"1,1,1\",\"starChartLevel\":22}}],\"sevenIdStr\":\"18,19,\",\"t\":\"2\",\"tag\":2,\"todaySign\":false,\"token\":\"850dfc71-df3f-4c76-a52b-1dd91cd43b80\",\"version\":1}";    	
        System.out.println("\n原始的字符串为------->" + str);
        float len0=str.length();
        System.out.println("原始的字符串长度为------->"+len0);
        
        String ys = compress(str);
        System.out.println("\n压缩后的字符串为----->" + ys);
        float len1=ys.length();
        System.out.println("压缩后的字符串长度为----->" + len1);

        String jy = unCompress(ys);
        System.out.println("\n解压缩后的字符串为--->" + jy);
        System.out.println("解压缩后的字符串长度为--->"+jy.length());
        
        System.out.println("\n压缩比例为"+len1/len0);
        
        //判断
        if(str.equals(jy)){
            System.out.println("先压缩再解压以后字符串和原来的是一模一样的");
        }
    }

    /**
     * 字符串的压缩
     * 
     * @param str
     *            待压缩的字符串
     * @return    返回压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 使用默认缓冲区大小创建新的输出流
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        // 将 b.length 个字节写入此输出流
        gzip.write(str.getBytes());
        gzip.close();
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("ISO-8859-1");
    }
    
    /**
     * 字符串的解压
     * 
     * @param str
     *            对字符串解压
     * @return    返回解压缩后的字符串
     * @throws IOException
     */
    public static String unCompress(String str) throws IOException {
        if (null == str || str.length() <= 0) {
            return str;
        }
        // 创建一个新的 byte 数组输出流
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // 创建一个 ByteArrayInputStream，使用 buf 作为其缓冲区数组
        ByteArrayInputStream in = new ByteArrayInputStream(str
                .getBytes("ISO-8859-1"));
        // 使用默认缓冲区大小创建新的输入流
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n = 0;
        while ((n = gzip.read(buffer)) >= 0) {// 将未压缩数据读入字节数组
            // 将指定 byte 数组中从偏移量 off 开始的 len 个字节写入此 byte数组输出流
            out.write(buffer, 0, n);
        }
        // 使用指定的 charsetName，通过解码字节将缓冲区内容转换为字符串
        return out.toString("UTF-8");
    }

}