package org.embulk.filter.reverse_geocoding;

import org.h2.tools.Csv;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

public class GeoCodeMap
{
    public static final String LEVEL5_CSV_PATH = "./jpcities-master/geohash5.csv";
    public static final String LEVEL4_CSV_PATH = "./jpcities-master/geohash4.csv";
    static HashMap<String, String> GEOHASH_PREF;

    static {
        GEOHASH_PREF = new HashMap<>();
        URL url = GeoCodeMap.class.getClassLoader().getResource(LEVEL5_CSV_PATH);
        System.out.println(url);
        try {
            ResultSet rs = new Csv().read(url.toString(), null, null);
            ResultSetMetaData meta = rs.getMetaData();
            HashMap<String, String> geoHashMap = new HashMap<>();
            while (rs.next()) {
                geoHashMap.put(rs.getString(1), rs.getString(4));
            }
            rs.close();
        }
        catch (SQLException e) {
            throw new RuntimeException("geohash4.csv is not found!");
        }
    }

    public static String convert2PrefName(double lat, double lon)
    {
        String hash = GeoHash.encode(lat, lon);
        //if null throw exception ? not japanese lat/lon exception
        return GEOHASH_PREF.get(hash);
    }
}
