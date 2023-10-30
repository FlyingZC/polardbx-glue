/*
 * Copyright [2013-2021], Alibaba Group Holding Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.polardbx.rpc;

import com.alibaba.polardbx.common.datatype.Decimal;
import com.alibaba.polardbx.common.jdbc.TableName;
import com.alibaba.polardbx.common.utils.GeneralUtil;
import com.alibaba.polardbx.rpc.client.XSession;
import com.alibaba.polardbx.common.datatype.UInt64;
import com.alibaba.polardbx.common.utils.GeneralUtil;
import com.alibaba.polardbx.rpc.client.XSession;
import com.google.protobuf.ByteString;
import com.mysql.cj.x.protobuf.PolarxDatatypes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

/**
 * @version 1.0
 */
public class XUtil {

    public static PolarxDatatypes.Scalar genUtf8RawStringScalar(String value, XSession session) {
        final PolarxDatatypes.Scalar.String.Builder stringBuilder = PolarxDatatypes.Scalar.String.newBuilder();
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        // Default 33.
        // stringBuilder.setCollation(33); // utf8mb3 / bytes
//        stringBuilder.setValue(ByteString.copyFromUtf8(value));

        if (session != null) {
            try {
                stringBuilder.setValue(
                    ByteString.copyFrom(value, XSession.toJavaEncoding(session.getRequestEncodingMySQL())));
            } catch (Exception e) {
                throw GeneralUtil.nestedException(e);
            }
        } else {
            stringBuilder.setValue(ByteString.copyFromUtf8(value));
        }
        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_RAW_STRING);
        scalarBuilder.setVString(stringBuilder);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genUtf8StringScalar(String value) {
        final PolarxDatatypes.Scalar.String.Builder stringBuilder = PolarxDatatypes.Scalar.String.newBuilder();
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        // Default utf8mb4.
        stringBuilder.setCollation(45); // utf8mb4_general_ci
        stringBuilder.setValue(ByteString.copyFromUtf8(value));
        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_STRING);
        scalarBuilder.setVString(stringBuilder);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genStringScalar(String value, XSession session) {
        final PolarxDatatypes.Scalar.String.Builder stringBuilder = PolarxDatatypes.Scalar.String.newBuilder();
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        if (session != null) {
            try {
                stringBuilder.setValue(
                    ByteString.copyFrom(value, XSession.toJavaEncoding(session.getRequestEncodingMySQL())));
            } catch (Exception e) {
                throw GeneralUtil.nestedException(e);
            }
        } else {
            stringBuilder.setValue(ByteString.copyFromUtf8(value));
        }
        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_STRING);
        scalarBuilder.setVString(stringBuilder);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genPlaceholderScalar(int position) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_PLACEHOLDER);
        scalarBuilder.setVPosition(position);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genSIntScalar(long value) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_SINT);
        scalarBuilder.setVSignedInt(value);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genUIntScalar(long value) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_UINT);
        scalarBuilder.setVUnsignedInt(value);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genFloatScalar(float value) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_FLOAT);
        scalarBuilder.setVFloat(value);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genDoubleScalar(double value) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_DOUBLE);
        scalarBuilder.setVDouble(value);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genBooleanScalar(boolean value) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_BOOL);
        scalarBuilder.setVBool(value);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genOctetsScalar(ByteBuffer buffer) {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();
        final PolarxDatatypes.Scalar.Octets.Builder octetsBuilder = PolarxDatatypes.Scalar.Octets.newBuilder();

        octetsBuilder.setValue(ByteString.copyFrom(buffer));
        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_OCTETS);
        scalarBuilder.setVOctets(octetsBuilder);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genNullScalar() {
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_NULL);
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genIdentifierScalar(String identifier) {
        final PolarxDatatypes.Scalar.String.Builder stringBuilder = PolarxDatatypes.Scalar.String.newBuilder();
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        // Default utf8mb4.
        stringBuilder.setCollation(45); // utf8mb4_general_ci
        stringBuilder.setValue(ByteString.copyFromUtf8(identifier));
        if (XConfig.GALAXY_X_PROTOCOL) {
            scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_IDENTIFIER_GALAXY);
            scalarBuilder.setVIdentifierGalaxy(stringBuilder);
        } else {
            scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_IDENTIFIER);
            scalarBuilder.setVIdentifier(stringBuilder);
        }
        return scalarBuilder.build();
    }

    public static PolarxDatatypes.Scalar genIdentifierScalarStringCompatible(String identifier) {
        final PolarxDatatypes.Scalar.String.Builder stringBuilder = PolarxDatatypes.Scalar.String.newBuilder();
        final PolarxDatatypes.Scalar.Builder scalarBuilder = PolarxDatatypes.Scalar.newBuilder();

        // Default utf8mb4.
        stringBuilder.setCollation(45); // utf8mb4_general_ci
        stringBuilder.setValue(ByteString.copyFromUtf8(identifier));
        scalarBuilder.setType(PolarxDatatypes.Scalar.Type.V_IDENTIFIER);
        scalarBuilder.setVString(stringBuilder);
        return scalarBuilder.build();
    }

    private static final BigInteger ZERO = new BigInteger("0");
    private static final BigInteger LONG_LIMIT = new BigInteger("18446744073709551616");

    public static PolarxDatatypes.Array genScalarList(List values, XSession session) {
        PolarxDatatypes.Array.Builder builder = PolarxDatatypes.Array.newBuilder();
        for (Object value : values) {
            PolarxDatatypes.Scalar scalar = genScalar(value, session);
            builder.addValue(XUtil.genAny(scalar));
        }
        return builder.build();
    }

    public static PolarxDatatypes.Scalar genScalar(Object value, XSession session) {
        if (value instanceof String) {
            return genStringScalar((String) value, session);
        } else if (value instanceof Integer || value instanceof Long || value instanceof Short
            || value instanceof Byte) {
            return genSIntScalar(((Number) value).longValue());
        } else if (null == value) {
            return genNullScalar();
        } else if (value instanceof Float) {
            return genFloatScalar((Float) value);
        } else if (value instanceof Double) {
            return genDoubleScalar((Double) value);
        } else if (value instanceof Boolean) {
            return genBooleanScalar((Boolean) value);
        } else if (value instanceof BigInteger) {
            if (((BigInteger) value).compareTo(ZERO) >= 0 &&
                ((BigInteger) value).compareTo(LONG_LIMIT) < 0) {
                return genUIntScalar(((BigInteger) value).longValue());
            } else {
                return genUtf8StringScalar(value.toString());
            }
        } else if (value instanceof BigDecimal) {
            if (session != null && session.supportRawString()) {
                return genUtf8RawStringScalar(value.toString(), session);
            } else {
                return genUtf8StringScalar(value.toString());
            }
        } else if (value instanceof Decimal) {
            if (session != null && session.supportRawString()) {
                return genUtf8RawStringScalar(value.toString(), session);
            } else {
                return genUtf8StringScalar(value.toString());
            }
        } else if (value instanceof Date) {
            return genUtf8StringScalar(value.toString());
        } else if (value instanceof Time) {
            return genUtf8StringScalar(value.toString());
        } else if (value instanceof Timestamp) {
            return genUtf8StringScalar(value.toString());
        } else if (value instanceof java.util.Date) {
            return genUtf8StringScalar(
                null == session ? value.toString() : session.formatTime((java.util.Date) value));
        } else if (value instanceof byte[]) {
            return genOctetsScalar(ByteBuffer.wrap((byte[]) value));
        } else if (value instanceof Blob) {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                final InputStream inputStream = ((Blob) value).getBinaryStream();
                byte[] buffer = new byte[4096];
                int n = 0;
                while (-1 != (n = inputStream.read(buffer))) {
                    outputStream.write(buffer, 0, n);
                }
                return genOctetsScalar(ByteBuffer.wrap(outputStream.toByteArray()));
            } catch (Exception e) {
                throw GeneralUtil.nestedException("XPreparedStatement setBlob error." + value.getClass().getName());
            }
        } else if (value instanceof TableName) {
            final String tableName = ((TableName) value).getTableName();
            if (tableName.length() >= 2 && '`' == tableName.charAt(0) && '`' == tableName
                .charAt(tableName.length() - 1)) {
                return genIdentifierScalar(tableName.substring(1, tableName.length() - 1));
            } else {
                return genIdentifierScalar(tableName);
            }
        } else if (value instanceof UInt64) {
            return genUIntScalar(((UInt64) value).longValue());
        } else {
            // TODO: support more type.
            throw GeneralUtil.nestedException("TODO: support more type. " + value.getClass().getName());
        }
    }

    public static PolarxDatatypes.Any genAny(PolarxDatatypes.Scalar scalar) {
        final PolarxDatatypes.Any.Builder builder = PolarxDatatypes.Any.newBuilder();

        builder.setType(PolarxDatatypes.Any.Type.SCALAR);
        builder.setScalar(scalar);
        return builder.build();
    }

    public static PolarxDatatypes.Any genAny(PolarxDatatypes.Array array) {
        final PolarxDatatypes.Any.Builder builder = PolarxDatatypes.Any.newBuilder();

        builder.setType(PolarxDatatypes.Any.Type.ARRAY);
        builder.setArray(array);
        return builder.build();
    }

}
