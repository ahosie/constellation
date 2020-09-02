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
package au.gov.asd.tac.constellation.graph.value.operations;

import au.gov.asd.tac.constellation.graph.value.Operators;
import au.gov.asd.tac.constellation.graph.value.readables.BooleanReadable;
import au.gov.asd.tac.constellation.graph.value.readables.DoubleReadable;
import au.gov.asd.tac.constellation.graph.value.readables.FloatReadable;
import au.gov.asd.tac.constellation.graph.value.readables.IntReadable;
import au.gov.asd.tac.constellation.graph.value.readables.LongReadable;
import au.gov.asd.tac.constellation.graph.value.readables.ObjectReadable;
import au.gov.asd.tac.constellation.graph.value.readables.StringReadable;
import au.gov.asd.tac.constellation.graph.value.writables.BooleanWritable;
import au.gov.asd.tac.constellation.graph.value.writables.DoubleWritable;
import au.gov.asd.tac.constellation.graph.value.writables.FloatWritable;
import au.gov.asd.tac.constellation.graph.value.writables.IntWritable;
import au.gov.asd.tac.constellation.graph.value.writables.LongWritable;
import au.gov.asd.tac.constellation.graph.value.writables.ObjectWritable;
import au.gov.asd.tac.constellation.graph.value.writables.StringWritable;

/**
 *
 * @author sirius
 */
public class Assign {
    
    public static final String NAME = new String("ASSIGN");
    
    public static void register(Operators operators) {
        final var registry = operators.getRegistry(NAME);
        
        registry.register(DoubleWritable.class, DoubleReadable.class, DoubleReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readDouble();
                p1.writeDouble(result);
                return result;
            };
        });
        
        registry.register(FloatWritable.class, FloatReadable.class, FloatReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readFloat();
                p1.writeFloat(result);
                return result;
            };
        });
        
        registry.register(LongWritable.class, LongReadable.class, LongReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readLong();
                p1.writeLong(result);
                return result;
            };
        });
        
        registry.register(IntWritable.class, IntReadable.class, IntReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readInt();
                p1.writeInt(result);
                return result;
            };
        });
        
        registry.register(BooleanWritable.class, BooleanReadable.class, BooleanReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readBoolean();
                p1.writeBoolean(result);
                return result;
            };
        });
        
        registry.register(StringWritable.class, StringReadable.class, StringReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readString();
                p1.writeString(result);
                return result;
            };
        });
        
        registry.register(ObjectWritable.class, ObjectReadable.class, ObjectReadable.class, (p1, p2) -> {
            return () -> {
                final var result = p2.readObject();
                p1.writeObject(result);
                return result;
            };
        });
        
        registry.register(StringWritable.class, LongReadable.class, StringReadable.class, (p1, p2) -> {
            return () -> {
                final var result = String.valueOf(p2.readLong());
                p1.writeString(result);
                return result;
            };
        });
        
        registry.register(StringWritable.class, DoubleReadable.class, StringReadable.class, (p1, p2) -> {
            return () -> {
                final var result = String.valueOf(p2.readDouble());
                p1.writeString(result);
                return result;
            };
        });
    }
    
    static {
        register(Operators.getDefault());
    }
}
