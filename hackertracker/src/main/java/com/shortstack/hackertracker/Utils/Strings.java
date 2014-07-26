package com.shortstack.hackertracker.Utils;

/**
 * Copyright 2014 Alex Yanchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import com.shortstack.hackertracker.Common.Constants;

        import java.io.UnsupportedEncodingException;
        import java.math.BigInteger;
        import java.net.URLDecoder;
        import java.net.URLEncoder;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.Collection;

public class Strings {

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

}