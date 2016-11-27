package org.embulk.filter.reverse_geocoding;

/**
 * GeoHash.
 *
 * @author maachang.
 * @version 2015/04/03
 * @since 1.0
 * <p>
 * Copyright (c) 2015 maachang.
 * This software is released under the MIT License.
 */
public final class GeoHash
{
    protected GeoHash() {}

    /**
     * Lat,Lonを求めるBit長.
     **/
    private static final int BITS_LENGTH = 30;

    /**
     * Base32コード.
     **/
    private static final char[] _ENCODE_32 = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
                                              '9', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'm', 'n', 'p',
                                              'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    /**
     * Base32逆変換コード.
     **/
    private static final long[] _DECODE_32 = new long['z' + 1];

    static {
        final char[] c = _ENCODE_32;
        int len = c.length;
        for (int i = 0; i < len; i++) {
            _DECODE_32[c[i]] = (long) i;
        }
    }

    /**
     * lat,lonをGeoHashエンコード.
     **/
    private static final long _encode(double lat, double lon)
    {
        long shift = 60L;
        long ret = 0L;
        final int len = BITS_LENGTH;
        final int l = _encodeBits(lat, len, -90, 90);
        final int o = _encodeBits(lon, len, -180, 180);
        for (int i = 0; i < len; i++) {
            shift -= 2L;
            ret |= ((long) (((l & (1 << i)) >> i) | (((o & (1 << i)) >> i) << 1))) << shift;
        }
        return ret;
    }

    /**
     * lat or lonのビット値を計算.
     **/
    private static final int _encodeBits(double o, int len, double f, double c)
    {
        int ret = 0;
        double m;
        for (int i = 0; i < len; i++) {
            if (o >= (m = (f + c) / 2)) {
                f = m;
                ret |= 1 << i;
            }
            else {
                c = m;
            }
        }
        return ret;
    }

    /**
     * Base32エンコード.
     **/
    private static final String _encode32(long code)
    {
        long shift = 0L;
        final int len = (BITS_LENGTH << 1) / 5;
        final char[] cc = _ENCODE_32;
        final char[] ret = new char[len];
        for (int i = len - 1; i >= 0; i--) {
            ret[i] = cc[(int) (((code & (0x1fL << shift)) >> shift) & 0x1fL)];
            shift += 5L;
        }
        return new String(ret);
    }

    /**
     * geoHashをデコード.
     **/
    private static final void _decode(double[] out, long code)
    {
        int lat = 0;
        int lon = 0;
        int latLonShift = 0;
        long shift = 60L;
        final int len = BITS_LENGTH;
        for (int i = len - 1; i >= 0; i--) {
            shift -= 2L;
            lat |= ((int) (code >> shift) & 1) << latLonShift;
            lon |= ((int) (code >> (shift + 1)) & 1) << latLonShift;
            latLonShift++;
        }
        out[0] = _decodeBits(lat, len, -90, 90);
        out[1] = _decodeBits(lon, len, -180, 180);
    }

    /**
     * エンコードされたlat,lonビット値から緯度経度計算.
     **/
    private static final double _decodeBits(int code, int len, double f, double d)
    {
        double ret = 0d;
        for (int i = 0; i < len; i++) {
            ret = (f + d) / 2;
            if ((code & (1 << i)) != 0) {
                f = ret;
            }
            else {
                d = ret;
            }
        }
        return ret;
    }

    /**
     * Base32デコード.
     **/
    private static final long _decode32(String code)
    {
        long ret = 0L;
        long shift = 0L;
        final long[] dd = _DECODE_32;
        final int len = code.length();
        for (int i = len - 1; i >= 0; i--) {
            ret |= dd[code.charAt(i)] << shift;
            shift += 5L;
        }
        return ret;
    }

    /**
     * エンコード.
     *
     * @param lat 緯度を設定します.
     * @param lon 経度を設定します.
     * @return String GeoHashコードが返却されます.
     */
    public static final String encode(double lat, double lon)
    {
        return _encode32(_encode(lat, lon));
    }

    /**
     * デコード.
     *
     * @param out [0]緯度 [1]経度 が格納されます.
     * @param hash GeoHashを設定します.
     */
    public static final void decode(double[] out, String hash)
    {
        _decode(out, _decode32(hash));
    }

    /**
     * デコード.
     *
     * @param hash GeoHashを設定します.
     * @return double[0]緯度 double[1]経度 が格納されます.
     */
    public static final double[] decode(String hash)
    {
        double[] ret = new double[2];
        _decode(ret, _decode32(hash));
        return ret;
    }
}