/*
 * Copyright 2010-2020 Australian Signals Directorate
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
package au.gov.asd.tac.constellation.graph.value.types.longType;

import au.gov.asd.tac.constellation.graph.value.converters.Copyable;
import au.gov.asd.tac.constellation.graph.value.types.ArithmeticConverters;
import au.gov.asd.tac.constellation.graph.value.types.byteType.ByteReadable;
import au.gov.asd.tac.constellation.graph.value.types.byteType.ByteWritable;
import au.gov.asd.tac.constellation.graph.value.types.doubleType.DoubleReadable;
import au.gov.asd.tac.constellation.graph.value.types.doubleType.DoubleWritable;
import au.gov.asd.tac.constellation.graph.value.types.floatType.FloatReadable;
import au.gov.asd.tac.constellation.graph.value.types.floatType.FloatWritable;
import au.gov.asd.tac.constellation.graph.value.types.integerType.IntReadable;
import au.gov.asd.tac.constellation.graph.value.types.integerType.IntWritable;
import au.gov.asd.tac.constellation.graph.value.types.shortType.ShortReadable;
import au.gov.asd.tac.constellation.graph.value.types.shortType.ShortWritable;
import au.gov.asd.tac.constellation.graph.value.types.stringType.StringWritable;
import au.gov.asd.tac.constellation.graph.value.types.stringType.StringReadable;

/**
 *
 * @author sirius
 */
public class LongValue implements Copyable, LongReadable, LongWritable, IntReadable, IntWritable, ShortReadable, ShortWritable, ByteReadable, ByteWritable, DoubleReadable, DoubleWritable, FloatReadable, FloatWritable, StringReadable, StringWritable, Comparable<LongValue> {

    static {
        ArithmeticConverters.register();
    }
    
    private long value = 0;
    
    @Override
    public Object copy() {
        var copy = new LongValue();
        copy.value = value;
        return copy;
    }
    
    @Override
    public String readString() {
        return String.valueOf(value);
    }
    
    @Override
    public void writeString(String value) {
        this.value = Long.valueOf(value);
    }
    
    @Override
    public long readLong() {
        return value;
    }
    
    @Override
    public void writeLong(long value) {
        this.value = value;
    }
    
    @Override
    public int readInt() {
        return (int)value;
    }
    
    @Override
    public void writeInt(int value) {
        this.value = value;
    }
    
    @Override
    public short readShort() {
        return (short)value;
    }
    
    @Override
    public void writeShort(short value) {
        this.value = value;
    }
    
    @Override
    public byte readByte() {
        return (byte)value;
    }
    
    @Override
    public void writeByte(byte value) {
        this.value = value;
    }
    
    @Override
    public double readDouble() {
        return value;
    }
    
    @Override
    public void writeDouble(double value) {
        this.value = (long)value;
    }
    
    @Override
    public float readFloat() {
        return value;
    }
    
    @Override
    public void writeFloat(float value) {
        this.value = (long)value;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other instanceof LongValue) {
            return value == ((LongValue)other).value;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
    
    @Override
    public int compareTo(LongValue value) {
        return Long.compare(this.value, value.value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
