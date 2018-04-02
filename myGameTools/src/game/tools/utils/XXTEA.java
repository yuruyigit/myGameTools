package game.tools.utils;

import java.util.Arrays;

public class XXTEA {
	
	public static final String KEY = "[game@You#Shi!chef*2016_16)6}";
	
    private XXTEA() {}

    /** 
     * @param args 
     */ 
    public static void main(String[] args) throws Exception { 
//        String s = "hello worldhjgfgfjghjgjhghhjghjjhghjkghjghjkgtyfgyfvg::"; 
////        byte[] k = "1234567890abcdef".getBytes(); 
        String k = "[game@You#Shi!chef*2016_16)6}";
//        
//        long startTime = System.currentTimeMillis();
//        byte[] e = encrypt(s.getBytes(), k);
//        long endTime = System.currentTimeMillis();
//        System.out.println("加密后 ： " + new String(e) + " 耗时："+ (endTime - startTime) +  Arrays.toString(e)); 
//        
//        startTime = System.currentTimeMillis();
//        byte[] d = decrypt(e, k); 
//        endTime = System.currentTimeMillis();
//        System.out.println("解密后 ： " + new String(d) + " 耗时："+ (endTime - startTime) +  Arrays.toString(d));
        
        String content = "{\"protocolNo\":110001,\"platfromId\":\"6\",\"deviceId\":\"-553709736\",\"userId\":\"5c752b92453e812f\",\"channelId\":\"300219\",\"token\":\"5c752b92453e812f_5c752b92453e812f_3188ecaab6d4e9f769f268980f9cc035\"}";
        
        
//        byte [] src = {-9,-73,38,7,6,25,31,12,-14,20,-82,66,57,77,-77,-75,68,108,51,-61,-48,104,-95,-97,116,23,-52,-43,21,-29,72,-115,-33,25,-114,-52,93,126,-14,91,63,80,66,109,121,26,-70,-11,-116,101,-79,-23,-62,13,-47,97,-13,-107,72,68,-101,-71,-76,52,-65,9,83,127,-29,-98,-92,-39,122,120,124,48,-85,20,-19,64,107,-69,-30,-68,-8,-8,52,38,-95,-19,-75,52,-85,27,40,-10,35,120,-123,-46,64,82,78,-56,17,2,-105,94,-38,95,20,93,72,112,-6,113,-41,-106,-94,85};
        byte [] src = {-9,-73,38,7,6,25,31,12,-14,20,-82,66,57,77,-77,-75,68,108,51,-61,-48,104,-95,-97,116,23,-52,-43,21,-29,72,-115,-33,25,-114,-52,93,126,-14,91,63,80,66,109,121,26,-70,-11,-116,101,-79,-23,-62,13,-47,97,-13,-107,72,68,-101,-71,-76,52,-65,9,83,127,-29,-98,-92,-39,122,120,124,48,-85,20,-19,64,107,-69,-30,-68,-8,-8,52,38,-95,-19,-75,52,-85,27,40,-10,35,120,-123,-46,64,82,78,-56,17,2,-105,94,-38,95,20,93,72,112,-6,113,-41,-106,-94,85};
        long startTime = System.currentTimeMillis();
        byte[] d = decrypt(src, k); 
        long endTime = System.currentTimeMillis();
        System.out.println("解密后 ： " + new String(d) + " 耗时："+ (endTime - startTime) +  Arrays.toString(d));
    } 

    public static byte[] encrypt(byte[] data)
    {
    	return encrypt((byte[])data, XXTEA.KEY);
    }
    
    public static byte[] decrypt(byte[] data)
    {
    	return decrypt((byte[])data, XXTEA.KEY);
    }
    /**
     * Encrypt data with key.
     * @param data
     * @param key
     * @return	返回一个xxtea的加密数据
     */
    public static byte[] encrypt(byte[] data, String key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                encrypt(toIntArray(data, true), toIntArray(key.getBytes(), false)), false);
    }

    /**
     * Decrypt data with key.
     * @param data
     * @param key
     * @return 返回一个xxtea的解密数据
     */
    public static byte[] decrypt(byte[] data, String key) {
        if (data.length == 0) {
            return data;
        }
        return toByteArray(
                decrypt(toIntArray(data, false), toIntArray(key.getBytes(), false)), true);
    }

    /**
     * Encrypt data with key.
     * 
     * @param v
     * @param k
     * @return
     */
    public static int[] encrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n], y = v[0], delta = 0x9E3779B9, sum = 0, e;
        int p, q = 6 + 52 / (n + 1);

        while (q-- > 0) {
            sum = sum + delta;
            e = sum >>> 2 & 3;
            for (p = 0; p < n; p++) {
                y = v[p + 1];
                z = v[p] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                        ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }
            y = v[0];
            z = v[n] += (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                    ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
        }
        return v;
    }

    /**
     * Decrypt data with key.
     * 
     * @param v
     * @param k
     * @return
     */
    public static int[] decrypt(int[] v, int[] k) {
        int n = v.length - 1;

        if (n < 1) {
            return v;
        }
        if (k.length < 4) {
            int[] key = new int[4];

            System.arraycopy(k, 0, key, 0, k.length);
            k = key;
        }
        int z = v[n], y = v[0], delta = 0x9E3779B9, sum, e;
        int p, q = 6 + 52 / (n + 1);

        sum = q * delta;
        while (sum != 0) {
            e = sum >>> 2 & 3;
            for (p = n; p > 0; p--) {
                z = v[p - 1];
                y = v[p] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                        ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            }
            z = v[n];
            y = v[0] -= (z >>> 5 ^ y << 2) + (y >>> 3 ^ z << 4)
                    ^ (sum ^ y) + (k[p & 3 ^ e] ^ z);
            sum = sum - delta;
        }
        return v;
    }

    /**
     * Convert byte array to int array.
     * 
     * @param data
     * @param includeLength
     * @return
     */
    private static int[] toIntArray(byte[] data, boolean includeLength) {
        int n = (((data.length & 3) == 0)
                ? (data.length >>> 2)
                : ((data.length >>> 2) + 1));
        int[] result;

        if (includeLength) {
            result = new int[n + 1];
            result[n] = data.length;
        } else {
            result = new int[n];
        }
        n = data.length;
        for (int i = 0; i < n; i++) {
            result[i >>> 2] |= (0x000000ff & data[i]) << ((i & 3) << 3);
        }
        return result;
    }

    /**
     * Convert int array to byte array.
     * 
     * @param data
     * @param includeLength
     * @return
     */
    private static byte[] toByteArray(int[] data, boolean includeLength) {
        int n = data.length << 2;
        if (includeLength) {
            int m = data[data.length - 1];

            if (m > n) {
                return null;
            } else {
                n = m;
            }
        }
        byte[] result = new byte[n];

        for (int i = 0; i < n; i++) {
            result[i] = (byte) ((data[i >>> 2] >>> ((i & 3) << 3)) & 0xff);
        }
        return result;
    }
}