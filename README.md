# Reverse Geocoding filter plugin for Embulk

Convert latitude and longitude to prefecture/city name (Only for Japan).  
This plugin is using GeoHash.

## Overview

* **Plugin type**: filter

## Configuration

- **target_lon**: longitude (double, required)
- **target_lat**: latitude (double, required)
- **output_columns**: output columns definition
    + **name**: output column name (string, default: `null`)
    + **type**: output column type (string, default: `string`)
    + **out**: output data type see below(string)
        * pref : prefecture name            
        * city : city name
        * hash : GeoHash value (length = 5)

## Example

```
$ embulk preview -L ./embulk-filter-reverse_geocoding config.yml

+--------------------------+------------+--------------------+-------------+-------------+-------------+
|           address:string | lat:double |         lon:double | pref:string | city:string | hash:string |
+--------------------------+------------+--------------------+-------------+-------------+-------------+
|                    東京都 |        1.0 |                1.0 |             |             |       s00tw |
|        東京都千代田区1−1−1 | 35.6721277 | 139.75891209999997 |       東京都 |  東京都中央区 |       xn76u |
|        六本木グランドタワー | 35.6641222 |         139.729426 |       東京都 |   東京都港区 |       xn76g |
+--------------------------+------------+--------------------+-------------+-------------+-------------+

```


```yaml
filters:
  - type: reverse_geocoding
    target_lon: lon
    target_lat: lat
    output_columns:
      - {name: pref, type: string, out: pref}
      - {name: city, type: string, out: city}
      - {name: hash, type: string, out: hash}
```


## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```

## Thanks For

Geo hash data refers to this repository.    
https://github.com/FrogAppsDev/jpcities