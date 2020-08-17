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
package au.gov.asd.tac.constellation.graph.value.types.booleanType;

import au.gov.asd.tac.constellation.graph.value.converter.Biconverter;
import au.gov.asd.tac.constellation.graph.value.converter.ConverterRegistry;
import au.gov.asd.tac.constellation.graph.value.readables.Assign;

/**
 *
 * @author sirius
 */
public class BooleanAssignConverters {
    
    public static <P1 extends BooleanWritable, P2 extends BooleanReadable> void register(ConverterRegistry r, Class<P1> parameterClass1, Class<P2> parameterClass2) {
        r.register(parameterClass1, parameterClass2, Assign.class, new AssignConverter());
    }
    
    public static class AssignConverter implements Biconverter<BooleanWritable, BooleanReadable, Assign> {
        @Override
        public Assign convert(BooleanWritable source1, BooleanReadable source2) {
            return () -> {
                source1.writeBoolean(source2.readBoolean());
            };
        }
    }
}
