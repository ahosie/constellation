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

import au.gov.asd.tac.constellation.graph.value.OperatorRegistry;
import au.gov.asd.tac.constellation.graph.value.Operators;
import au.gov.asd.tac.constellation.graph.value.StringOperation;

/**
 *
 * @author sirius
 */
public class Contains {

    public static final String NAME = "CONTAINS";

    private Contains() {
        // added private constructor to hide implicit public constructor - S1118.
    }

    public static final StringOperation STRING_OPERATION = new StringOperation() {
        @Override
        public boolean execute(String p1, String p2) {
            return p1 != null && p2 != null && p1.contains(p2);
        }
    };

    public static void register(Operators operators) {
        final OperatorRegistry registry = operators.getRegistry(NAME);
        STRING_OPERATION.register(registry);
    }

    static {
        register(Operators.getDefault());
    }
}
